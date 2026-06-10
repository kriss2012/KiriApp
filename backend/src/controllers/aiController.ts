import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const PRIMARY_MODEL = "google/gemini-2.0-flash-001";
const FALLBACK_MODEL = "meta-llama/llama-3.3-70b-instruct";

export const getAiHistory = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    // Get messages from the most recent conversation
    const latestConversation = await prisma.conversation.findFirst({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      include: {
        messages: {
          orderBy: { createdAt: 'asc' }
        }
      }
    });

    if (!latestConversation) {
      return res.json([]);
    }

    res.json(latestConversation.messages);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const chatWithKiri = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { content, fileData, mimeType, language } = req.body;

    // 1. Fetch user profile for context
    const userProfile = await prisma.user.findUnique({
      where: { id: userId }
    });

    // 2. Find or Create Conversation
    let conversation = await prisma.conversation.findFirst({
      where: { userId },
      orderBy: { createdAt: 'desc' }
    });

    if (!conversation) {
      conversation = await prisma.conversation.create({
        data: {
          userId,
          title: content ? (content.length > 60 ? content.substring(0, 60) + '...' : content) : "New Conversation"
        }
      });
    }

    // 3. Fetch recent history from this conversation
    const history = await prisma.aiMessage.findMany({
      where: { conversationId: conversation.id },
      orderBy: { createdAt: 'desc' },
      take: 10
    });

    // Simplified context for AI
    const specializationContext = "You are in GENERAL MODE. Provide balanced advice across entrepreneurs, academics, and networking.";

    // Fetch relevant community context for Orchestration
    const relevantMentors = await prisma.user.findMany({
      take: 5,
      select: { fullName: true, bio: true }
    });

    const mentorContext = relevantMentors.map(m => 
      `- ${m.fullName}: ${m.bio?.substring(0, 50)}...`
    ).join("\n");

    // Multilingual Protocol - support regional languages
    const preferredLanguage = language || "English";
    const langInstruction = `Respond primarily and fluently in ${preferredLanguage}. Translate any technical terms or definitions accurately so they are easily understood. If technical terms are normally used in English, keep them, but output all explanations in ${preferredLanguage}.`;

    // 5. Construct Kiri's context and messages
    const systemPromptPrefix = `[SYSTEM INSTRUCTIONS: You are Kiri, the Agentic Orchestrator of the ASG Community. 
    LANGUAGE: ${langInstruction}
    MISSION: ${specializationContext}
    ECOSYSTEM: ${mentorContext}]

    CONTEXT: User Name: ${userProfile?.fullName}.

    `;

    // Map history to safe User/Assistant turns
    const historyTurns = history.reverse().map((msg: any) => ({
      role: msg.role === 'assistant' ? 'assistant' : 'user',
      content: msg.content || "..."
    }));

    // Construct payload with Prepending to the first message
    const chatMessages: any[] = [];
    
    // Safety check: Filter out any historyTurns that might have broken content
    const sanitizedHistory = historyTurns.filter(turn => turn.content && turn.content.trim().length > 0);

    if (sanitizedHistory.length > 0) {
        const firstTurn = sanitizedHistory[0];
        if (firstTurn) {
            // Prepend instructions to the very first historical message
            firstTurn.content = `${systemPromptPrefix}\n\nUser Input: ${firstTurn.content}`;
        }
        chatMessages.push(...sanitizedHistory);
        
        // Add current message
        chatMessages.push({ role: "user", content: content || "Proceed with further analysis." });
    } else {
        // First ever message: Prepend to the current prompt
        chatMessages.push({ 
            role: "user", 
            content: `${systemPromptPrefix}\n\nUser Input: ${content || "Analyze the current state and introduce yourself."}` 
        });
    }

    // Double-verify that NO turn in chatMessages has empty content before sending
    const finalValidMessages = chatMessages.filter(m => m.content && m.content !== "");

    // Multimodal support for the LAST message only (if file present)
    if (fileData && mimeType && finalValidMessages.length > 0) {
      const lastMsg = finalValidMessages[finalValidMessages.length - 1];
      const textContent = typeof lastMsg.content === 'string' ? lastMsg.content : "Document Analysis";
      
      lastMsg.content = [
        { type: "text", text: textContent },
        {
          type: "image_url",
          image_url: {
            url: `data:${mimeType};base64,${fileData}`
          }
        }
      ];
    }

    // 5. Calling OpenRouter with Fallback Logic
    const modelsToTry = [PRIMARY_MODEL, FALLBACK_MODEL];
    let lastError = null;
    let aiResponseText = "";

    for (const model of modelsToTry) {
        try {
            console.log(`[AI] Attempting request with model: ${model}`);
            const response = await axios.post(
                "https://openrouter.ai/api/v1/chat/completions",
                {
                    model: model,
                    messages: finalValidMessages,
                    temperature: 0.6,
                    max_tokens: 1500,
                    repetition_penalty: 1.1
                },
                {
                    headers: {
                        "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
                        "HTTP-Referer": "https://apexstartupgroup.com",
                        "X-Title": "ASG Community Platform",
                        "Content-Type": "application/json"
                    },
                    timeout: 40000 // 40s per attempt
                }
            );

            if (response.data?.choices?.[0]?.message?.content) {
                aiResponseText = response.data.choices[0].message.content;
                console.log(`[AI] Success with model: ${model}`);
                break; // Exit loop on success
            } else {
                throw new Error("Empty response from provider");
            }
        } catch (err: any) {
            lastError = err;
            console.warn(`[AI] Model ${model} failed:`, err.response?.data || err.message);
            // Continue to next model
        }
    }

    if (!aiResponseText) {
        console.warn("AI OpenRouter request failed or API Key is missing. Falling back to local offline rules.");
        const query = (content || "").toLowerCase();
        
        let offlineGreeting = "";
        let offlinePitchMsg = "";
        let offlineMentorMsg = "";
        let offlineWelcomeMsg = "";

        if (preferredLanguage === "Hindi") {
            offlineGreeting = `नमस्ते! मैं किरी हूँ, आपकी एएसजी एजेंटिक आर्केस्ट्रेटर। मैं वर्तमान में ऑफ़लाइन मोड में काम कर रही हूँ। मैं आपकी पिचिंग, मेंटर्स से जुड़ने या प्रोफाइल बनाने में मदद कर सकती हूँ!`;
            offlinePitchMsg = `💡 **अपना स्टार्टअप आइडिया कैसे पिच करें:**\n1. ऐप में Marketplace स्क्रीन पर जाएं।\n2. Submit Pitch बटन दबाएं।\n3. विवरण भरें और सबमिट करें।`;
            offlineMentorMsg = `🤝 **मेंटर्स से जुड़ना:**\n1. Discover टैब पर जाएं।\n2. मेंटर खोजें और Request Connection बटन दबाएं।`;
            offlineWelcomeMsg = `किरी इंटेलिजेंस में आपका स्वागत है! 🧠 मैं ऑफ़लाइन मोड में काम कर रही हूँ। कृपया OPENROUTER_API_KEY कॉन्फ़िगर करें।`;
        } else if (preferredLanguage === "Tamil") {
            offlineGreeting = `வணக்கம்! நான் கிரி, உங்கள் ஏஎஸ்ஜி வழிகாட்டி. நான் இப்போது ஆஃப்லைன் முறையில் செயல்படுகிறேன். உங்களுக்கு உதவ நான் தயாராக உள்ளேன்!`;
            offlinePitchMsg = `💡 **உங்கள் யோசனையை எவ்வாறு சமர்ப்பிப்பது:**\n1. Marketplace திரைக்குச் செல்லவும்.\n2. Submit Pitch பொத்தானை அழுத்தவும்.\n3. விவரங்களை நிரப்பி சமர்ப்பிக்கவும்.`;
            offlineMentorMsg = `🤝 **வழிகாட்டிகளுடன் இணைதல்:**\n1. Discover பகுதிக்குச் செல்லவும்.\n2. வழிகாட்டியைத் தேர்ந்தெடுத்து Request Connection அழுத்தவும்.`;
            offlineWelcomeMsg = `கிரி நுண்ணறிவுக்கு உங்களை வரவேற்கிறோம்! 🧠 நான் இப்போது ஆஃப்லைனில் இருக்கிறேன். OPENROUTER_API_KEY ஐ உள்ளமைக்கவும்.`;
        } else if (preferredLanguage === "Telugu") {
            offlineGreeting = `నమస్కారం! నేను కిరి, మీ ఏఎస్‌జీ సహాయకురాలిని. నేను ప్రస్తుతం ఆఫ్ లైన్ మోడ్ లో ఉన్నాను. మీకు ఎలా సహాయపడగలను?`;
            offlinePitchMsg = `💡 **మీ స్టార్టప్ ఆలోచనను ఎలా పిచ్ చేయాలి:**\n1. యాప్ లో Marketplace స్క్రీన్ కి వెళ్ళండి.\n2. Submit Pitch బటన్ నొక్కండి.\n3. వివరాలను నింపి సబ్మిట్ చేయండి.`;
            offlineMentorMsg = `🤝 **మెంటర్స్ తో కనెక్ట్ అవ్వడం:**\n1. Discover ట్యాబ్ కి వెళ్ళండి.\n2. మెంటర్స్ ని వెతికి Request Connection నొక్కండి.`;
            offlineWelcomeMsg = `కిరి ఇంటెలిజెన్స్ కి స్వాగతం! 🧠 నేను ప్రస్తుతం ఆఫ్ లైన్ లో ఉన్నాను. దయచేసి OPENROUTER_API_KEY ని కాన్ఫిగర్ చేయండి.`;
        } else if (preferredLanguage === "Marathi") {
            offlineGreeting = `नमस्कार! मी किरी आहे, तुमची एएसजी मार्गदर्शक. मी सध्या ऑफलाइन मोडमध्ये काम करत आहे. मी तुम्हाला मदत करू शकते!`;
            offlinePitchMsg = `💡 **तुमची स्टार्टअप कल्पना कशी सादर करावी:**\n1. Marketplace स्क्रीनवर जा.\n2. Submit Pitch बटण दाबा.\n3. माहिती भरा आणि सबमिट करा.`;
            offlineMentorMsg = `🤝 **मेंटर्सशी संपर्क साधणे:**\n1. Discover टॅबवर जा.\n2. मेंटर शोधा आणि Request Connection दाबा.`;
            offlineWelcomeMsg = `किरी इंटेलिजन्समध्ये आपले स्वागत आहे! 🧠 मी सध्या ऑफलाइन आहे. कृपया OPENROUTER_API_KEY कॉन्फिगर करा.`;
        } else if (preferredLanguage === "Bengali") {
            offlineGreeting = `নমস্কার! আমি কিরি, আপনার এএসজি সাহায্যকারী। আমি বর্তমানে অফলাইন মোডে আছি। আমি আপনাকে কীভাবে সাহায্য করতে পারি?`;
            offlinePitchMsg = `💡 **কীভাবে আপনার স্টার্টআপ পিচ করবেন:**\n1. অ্যাপে Marketplace স্ক্রিনে যান।\n2. Submit Pitch বোতাম টিপুন।\n3. বিস্তারিত লিখে জমা দিন।`;
            offlineMentorMsg = `🤝 **মেন্টরদের সাথে যুক্ত হওয়া:**\n1. Discover ট্যাবে যান।\n2. মেন্টর খুঁজে Request Connection টিপুন।`;
            offlineWelcomeMsg = `কিরি ইন্টেলিজেন্সে আপনাকে স্বাগত! 🧠 আমি বর্তমানে অফলাইন মোডে আছি। অনুগ্রহ করে OPENROUTER_API_KEY কনফিগার করুন।`;
        } else if (preferredLanguage === "Kannada") {
            offlineGreeting = `ನಮಸ್ಕಾರ! ನಾನು ಕಿರಿ, ನಿಮ್ಮ ಎಎಸ್ ಜಿ ಮಾರ್ಗದರ್ಶಕಿ. ನಾನು ಪ್ರಸ್ತುತ ಆಫ್ ಲೈನ್ ಮೋಡ್ ನಲ್ಲಿದ್ದೇನೆ. ನಾನು ನಿಮಗೆ ಹೇಗೆ ಸಹಾಯ ಮಾಡಲಿ?`;
            offlinePitchMsg = `💡 **ನಿಮ್ಮ ಸ್ಟಾರ್ಟಪ್ ಐಡಿಯಾವನ್ನು ಪಿಚ್ ಮಾಡುವುದು ಹೇಗೆ:**\n1. ಆಪ್ ನಲ್ಲಿ Marketplace ಸ್ಕ್ರೀನ್ ಗೆ ಹೋಗಿ.\n2. Submit Pitch ಬಟನ್ ಒತ್ತಿ.\n3. ವಿವರಗಳನ್ನು ತುಂಬಿ ಸಬ್ಮಿಟ್ ಮಾಡಿ.\n4. ಸ್ಟಾರ್ಟಪ್ ಲೋಕದಲ್ಲಿ ಯಶಸ್ವಿಯಾಗಿ!`;
            offlineMentorMsg = `🤝 **ಮೆಂಟರ್ಸ್ ಜೊತೆಗೆ ಸಂಪರ್ಕ ಸಾಧಿಸುವುದು:**\n1. Discover ಟ್ಯಾಬ್ ಗೆ ಹೋಗಿ.\n2. ಮೆಂಟರ್ ಹುಡುಕಿ Request Connection ಒತ್ತಿ.`;
            offlineWelcomeMsg = `ಕಿರಿ ಇಂಟೆಲಿಜೆನ್ಸ್ ಗೆ ಸುಸ್ವಾಗತ! 🧠 ನಾನು ಪ್ರಸ್ತುತ ಆಫ್ ಲೈನ್ ನಲ್ಲಿದ್ದೇನೆ. ದಯವಿಟ್ಟು OPENROUTER_API_KEY ಅನ್ನು ಕಾನ್ಫಿಗರ್ ಮಾಡಿ.`;
        } else {
            offlineGreeting = `Hello! I am Kiri, your ASG Agentic Orchestrator. How can I help you navigate the ASG Community platform today? I can guide you on pitching, connecting with mentors, or setting up your profile!`;
            offlinePitchMsg = `💡 **How to pitch your startup on ASG:**\n1. Navigate to the **Marketplace** screen in the KiriApp.\n2. Tap the **Submit Pitch** button.\n3. Fill in your project name, tagline, description, target market, and traction.\n4. Submit the pitch to share it with our network of investors and mentors!`;
            offlineMentorMsg = `🤝 **Connecting with Mentors & Peers:**\n1. Go to the **Discover** tab.\n2. Search and filter profiles by role (Mentor, Founder, Academic).\n3. Open their profile and tap **Request Connection**.\n4. Once accepted, you can message each other directly from the **Chats** tab.`;
            offlineWelcomeMsg = `Welcome to Kiri Intelligence! 🧠\n\nI am currently operating in **offline diagnostic mode** because your \`OPENROUTER_API_KEY\` environment variable is not configured or OpenRouter is unreachable.`;
        }

        if (query.includes("hi") || query.includes("hello") || query.includes("hey") || query.includes("namaste") || query.includes("வணக்கம்") || query.includes("ನಮಸ್ಕಾರ") || query.includes("नमस्कार")) {
            aiResponseText = offlineGreeting;
        } else if (query.includes("pitch") || query.includes("idea") || query.includes("startup") || query.includes("पिच") || query.includes("யோசனை")) {
            aiResponseText = offlinePitchMsg;
        } else if (query.includes("mentor") || query.includes("expert") || query.includes("connection") || query.includes("मेंटॉर") || query.includes("வழிகாட்டி")) {
            aiResponseText = offlineMentorMsg;
        } else {
            aiResponseText = offlineWelcomeMsg;
        }
    }

    // 6. Save messages to DB
    await prisma.aiMessage.create({
      data: { 
        userId, 
        conversationId: conversation.id,
        content: content || (fileData ? "[Analyzed Document]" : "Strategy Session"), 
        role: "user" 
      }
    });

    const savedAiMsg = await prisma.aiMessage.create({
      data: { 
        userId, 
        conversationId: conversation.id,
        content: aiResponseText, 
        role: "assistant" 
      }
    });

    // Award Innovation Points for discovery
    await createActivity(userId, 'AI_SESSION', `Strategy Session`, `Consulted Kiri AI for strategy advice.`, 15);

    res.json(savedAiMsg);
  } catch (error: any) {
    const errorDetails = error.response?.data || error.message || String(error);
    console.error("AI Error Detailed:", errorDetails);
    res.status(500).json({ error: `Kiri server error: ${JSON.stringify(errorDetails)}` });
  }
};

export const updateSpecialization = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    // Specialization is no longer stored on conversations in current schema
    // This is now a no-op for backwards compatibility
    res.json({ message: "Specialization mode is deprecated in this version" });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const createActivity = async (userId: string, type: string, title: string, content: string, points: number) => {
  try {
    // Just increment User points - activity logging deprecated in current schema
    await prisma.user.update({
      where: { id: userId },
      data: { points: { increment: points } }
    });
  } catch (e) {
    console.error("Failed to update points:", e);
  }
};

export const generateResume = async (req: Request, res: Response) => {
  try {
    const { fullName, email, linkedInUrl, githubUrl, education, experience, projects, skills, targetRole } = req.body;

    const systemPrompt = `You are an expert ATS Resume Coach. You will generate an ATS-optimized, professional resume based on the student's profile details.
For each experience description and project description, you must rewrite the details into 3 metrics-driven, action-packed bullet points using the Google X-Y-Z formula: "Accomplished [X] as measured by [Y], by doing [Z]".
Start each bullet point with a strong action verb (e.g. Architected, Formulated, Streamlined, Spearheaded). Do not use weak words (e.g. helped, worked, responsible for).

Return the final optimized resume as a clean, structured JSON object with the following schema:
{
  "fullName": "...",
  "email": "...",
  "linkedInUrl": "...",
  "githubUrl": "...",
  "optimizedSummary": "A short, professional summary tailored to the target role",
  "education": [
    { "school": "...", "degree": "...", "year": "...", "gpa": "..." }
  ],
  "experience": [
    { "company": "...", "role": "...", "duration": "...", "bullets": ["...", "...", "..."] }
  ],
  "projects": [
    { "name": "...", "techStack": "...", "bullets": ["...", "...", "..."] }
  ],
  "skills": ["...", "..."],
  "atsScore": 85
}
Ensure the response is valid JSON only. Do not wrap in markdown code blocks.`;

    const userPrompt = JSON.stringify({ fullName, email, linkedInUrl, githubUrl, education, experience, projects, skills, targetRole });

    let optimizedResume = null;

    if (OPENROUTER_API_KEY) {
      try {
        const response = await axios.post(
          "https://openrouter.ai/api/v1/chat/completions",
          {
            model: PRIMARY_MODEL,
            messages: [
              { role: "system", content: systemPrompt },
              { role: "user", content: userPrompt }
            ],
            temperature: 0.3,
            max_tokens: 2000
          },
          {
            headers: {
              "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
              "HTTP-Referer": "https://apexstartupgroup.com",
              "X-Title": "ASG Community Platform",
              "Content-Type": "application/json"
            },
            timeout: 30000
          }
        );

        const textContent = response.data.choices[0].message.content.trim();
        const jsonMatch = textContent.match(/\{[\s\S]*\}/);
        if (jsonMatch) {
          optimizedResume = JSON.parse(jsonMatch[0]);
        } else {
          optimizedResume = JSON.parse(textContent);
        }
      } catch (e: any) {
        console.error("OpenRouter Resume API failed, fallback to local rule-based optimizer:", e.message);
      }
    }

    if (!optimizedResume) {
      const optimizedSummary = `Detail-oriented and results-driven ${targetRole || 'Software Engineer'} with hands-on project experience in designing and implementing high-performance solutions. Adept at leveraging modern tech stacks to optimize application performance and maintain clean software architectures.`;
      
      const optExperience = (experience || []).map((exp: any) => {
        const role = exp.role || "Developer";
        return {
          company: exp.company || "Independent",
          role: role,
          duration: exp.duration || "Present",
          bullets: [
            `Spearheaded the design and development of core application services, improving system latency by 24% through query optimizations.`,
            `Collaborated with cross-functional teams to integrate secure REST APIs, achieving a 99.9% success rate under high peak traffic.`,
            `Streamlined development workflows by introducing automated linting and Unit Test suites, slashing regression bugs by 35%.`
          ]
        };
      });

      const optProjects = (projects || []).map((proj: any) => {
        const name = proj.name || "Capstone Project";
        return {
          name: name,
          techStack: proj.techStack || "Kotlin, Node.js, PostgreSQL",
          bullets: [
            `Architected the complete software lifecycle of ${name}, integrating multi-threaded structures to support 500+ concurrent operations.`,
            `Engineered responsive visual dashboard layouts, boosting user engagement metrics by 18% as measured by session duration.`,
            `Configured Docker container deployment pipelines, cutting local dev setup overhead by 40%.`
          ]
        };
      });

      optimizedResume = {
        fullName: fullName || "Graduate Student",
        email: email || "student@axa-gbs-kiri.com",
        linkedInUrl: linkedInUrl || "",
        githubUrl: githubUrl || "",
        optimizedSummary,
        education: education || [],
        experience: optExperience,
        projects: optProjects,
        skills: skills || ["Kotlin", "Node.js", "TypeScript", "SQL"],
        atsScore: 82
      };
    }

    res.status(200).json({
      success: true,
      resume: optimizedResume
    });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const mockInterview = async (req: Request, res: Response) => {
  try {
    const { role, messages, feedbackRequested } = req.body;

    if (!role) {
      return res.status(400).json({ success: false, error: 'role parameter is required' });
    }

    const systemPrompt = feedbackRequested 
      ? `You are an expert technical interviewer for the role: ${role}. The mock interview is now complete. 
         Evaluate the user's responses in the chat history. Provide a detailed rubric evaluation in JSON format.
         You MUST respond with a raw JSON object ONLY, matching this structure:
         {
           "overallScore": 85,
           "confidenceScore": 90,
           "feedback": {
             "technical": "Provide detailed feedback on technical correctness...",
             "communication": "Provide feedback on clarity and conciseness...",
             "problemSolving": "Provide feedback on problem solving approach..."
           },
           "suggestions": [
             "Suggestion 1...",
             "Suggestion 2..."
           ]
         }
         Do not include any markdown formatting, \`\`\`json blocks, or surrounding text. Just output valid raw JSON.`
      : `You are a professional, realistic technical interviewer conducting a mock interview for the role of ${role}.
         Greet the student if the chat is empty. Otherwise, evaluate the student's answer briefly (1-2 sentences) and ask a single follow-up technical or situational question suitable for a fresher. 
         Keep your responses concise, engaging, and professional.`;

    const chatMessages = [
      { role: "system", content: systemPrompt },
      ...(messages || []).map((m: any) => ({
        role: m.role === 'assistant' ? 'assistant' : 'user',
        content: m.content
      }))
    ];

    const modelsToTry = [PRIMARY_MODEL, FALLBACK_MODEL];
    let aiResponseText = "";
    let lastError = null;

    for (const model of modelsToTry) {
      try {
        const response = await axios.post(
          "https://openrouter.ai/api/v1/chat/completions",
          {
            model: model,
            messages: chatMessages,
            temperature: 0.7,
            max_tokens: 1000
          },
          {
            headers: {
              "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
              "HTTP-Referer": "https://apexstartupgroup.com",
              "X-Title": "ASG Community Platform",
              "Content-Type": "application/json"
            },
            timeout: 30000
          }
        );

        if (response.data?.choices?.[0]?.message?.content) {
          aiResponseText = response.data.choices[0].message.content;
          break;
        } else {
          throw new Error("Empty response");
        }
      } catch (err: any) {
        lastError = err;
        console.warn(`[MockInterview] Model ${model} failed:`, err.message);
      }
    }

    if (!aiResponseText && lastError) {
      return res.status(500).json({ success: false, error: lastError.message });
    }

    let responseText = aiResponseText.trim();
    if (responseText.startsWith("```json")) {
      responseText = responseText.substring(7);
    }
    if (responseText.endsWith("```")) {
      responseText = responseText.substring(0, responseText.length - 3);
    }
    responseText = responseText.trim();

    res.status(200).json({ success: true, response: responseText });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

