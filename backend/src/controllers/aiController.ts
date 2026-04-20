import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const MODEL = "google/gemini-2.0-flash-001"; // Updated to fixed model slug

export const getAiHistory = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    // Get the most recent conversation's messages
    const latestConversation = await prisma.conversation.findFirst({
      where: { userId, isArchived: false },
      orderBy: { updatedAt: 'desc' },
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
    const { content, fileData, mimeType } = req.body;

    // 1. Fetch user profile for context
    const userProfile = await prisma.user.findUnique({
      where: { id: userId }
    });

    // 2. Find or Create Conversation
    let conversation = await prisma.conversation.findFirst({
      where: { userId, isArchived: false },
      orderBy: { updatedAt: 'desc' }
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

    const specialization = (conversation as any).specialization || "GENERAL";
    let specializationContext = "";
    
    switch(specialization) {
        case "TECH":
            specializationContext = "You are in TECH MODE. Focus on code quality, scalability, tech architecture, and modern stacks (Native Android, Node.js).";
            break;
        case "LEGAL":
            specializationContext = "You are in LEGAL MODE. Focus on Indian startup laws, regional compliance in Maharashtra, IP protection, and registration steps.";
            break;
        case "GTM":
            specializationContext = "You are in GTM (Go-To-Market) MODE. Focus on marketing strategies, regional networking, and user acquisition in Tier 2/3 cities.";
            break;
        default:
            specializationContext = "You are in GENERAL MODE. Provide balanced advice across entrepreneurs, academics, and networking.";
    }

    // Fetch relevant community context for Orchestration
    const relevantMentors = await prisma.user.findMany({
      where: {
        AND: [
          { role: { in: ['MENTOR', 'FOUNDER'] } },
          { isVerified: true }
        ]
      },
      take: 5,
      select: { fullName: true, bio: true, skills: true }
    });

    const mentorContext = relevantMentors.map(m => 
      `- ${m.fullName} (${(m.skills as string[]).join(", ")}): ${m.bio?.substring(0, 50)}...`
    ).join("\n");

    // 4. Multilingual Protocol
    const preferredLang = (userProfile?.preferredLanguage as string || "ENGLISH").toUpperCase();
    let langInstruction = "Respond primarily in English.";
    if (preferredLang === "MARATHI") {
        langInstruction = "The user prefers MARATHI. Respond in Marathi (using Devanagari script) while keeping technical terms in English.";
    } else if (preferredLang === "HINDI") {
        langInstruction = "The user prefers HINDI. Respond in Hindi (using Devanagari script) while keeping technical terms in English.";
    }

    // 5. Construct Kiri's context and messages
    const systemPromptPrefix = `[SYSTEM INSTRUCTIONS: You are Kiri, the Agentic Orchestrator of the ASG Community. 
    LANGUAGE: ${langInstruction}
    SPECIALIZATION: ${specialization}
    MISSION: ${specializationContext}
    ECOSYSTEM: ${mentorContext}]

    CONTEXT: User Name: ${userProfile?.fullName}, Role: ${userProfile?.role}, College: ${userProfile?.college}.

    `;

    // Map history to safe User/Assistant turns
    const historyTurns = history.reverse().map((msg: any) => ({
      role: msg.role === 'assistant' ? 'assistant' : 'user',
      content: msg.content || "..."
    }));

    const MODEL = "google/gemini-pro-1.5"; // Switched to Pro 1.5 for higher stability with OpenRouter proxy

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

    // 5. Call OpenRouter with Hardened Settings
    const response = await axios.post(
      "https://openrouter.ai/api/v1/chat/completions",
      {
        model: MODEL,
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
        timeout: 50000 // 50s for heavy vision tasks
      }
    );

    const aiResponseText = response.data.choices[0].message.content || "I'm having trouble generating a response.";

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

    // Update conversation timestamp
    await prisma.conversation.update({
      where: { id: conversation.id },
      data: { updatedAt: new Date() }
    });

    // Award Innovation Points for discovery
    const specLabel = specialization || "GENERAL";
    await createActivity(userId, 'AI_SESSION', `Strategy Session (${specLabel})`, `Consulted Kiri AI regarding ${specLabel.toLowerCase()} strategies.`, 15);

    res.json(savedAiMsg);
  } catch (error: any) {
    console.error("AI Error Detailed:", error.response?.data || error.message);
    res.status(500).json({ error: "Kiri is having trouble thinking. Please try again later." });
  }
};

export const updateSpecialization = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { specialization } = req.body;
    
    const latestConversation = await prisma.conversation.findFirst({
      where: { userId, isArchived: false },
      orderBy: { updatedAt: 'desc' }
    });

    if (!latestConversation) {
      return res.status(404).json({ message: 'Conversation not found' });
    }

    const updated = await prisma.conversation.update({
      where: { id: latestConversation.id },
      data: { specialization }
    });

    res.json(updated);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const createActivity = async (userId: string, type: string, title: string, content: string, points: number) => {
  try {
    await prisma.activity.create({
      data: { userId, type, title, content, points }
    });
    // Increment User points
    await prisma.user.update({
      where: { id: userId },
      data: { points: { increment: points } }
    });
  } catch (e) {
    console.error("Failed to log activity:", e);
  }
};
