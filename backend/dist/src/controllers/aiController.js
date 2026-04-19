import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';
dotenv.config();
const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const MODEL = "google/gemini-2.0-flash-001"; // Updated to fixed model slug
export const getAiHistory = async (req, res) => {
    try {
        const userId = req.user.id;
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
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const chatWithKiri = async (req, res) => {
    try {
        const userId = req.user.id;
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
        const specialization = conversation.specialization || "GENERAL";
        let specializationContext = "";
        switch (specialization) {
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
        const mentorContext = relevantMentors.map(m => `- ${m.fullName} (${m.skills.join(", ")}): ${m.bio?.substring(0, 50)}...`).join("\n");
        // 4. Multilingual Protocol
        const preferredLang = (userProfile?.preferredLanguage || "ENGLISH").toUpperCase();
        let langInstruction = "Respond primarily in English.";
        if (preferredLang === "MARATHI") {
            langInstruction = "The user prefers MARATHI. Respond in Marathi (using Devanagari script) while keeping technical terms in English.";
        }
        else if (preferredLang === "HINDI") {
            langInstruction = "The user prefers HINDI. Respond in Hindi (using Devanagari script) while keeping technical terms in English.";
        }
        // 5. Construct Kiri's context and messages
        const systemPrompt = `You are Kiri, the Agentic Orchestrator of the ASG Community. 
    You help regional startups and students innovate.
    
    LANGUAGE PROTOCOL: ${langInstruction}
    
    SPECIALIZATION: ${specialization}
    RESEARCH PROTOCOL: ${specializationContext}
    
    ACTIVE ECOSYSTEM MEMBERS (Suggestion Context):
    ${mentorContext}
    
    MISSION: If the user needs help in a specific field, suggest contacting one of the members listed above by name if their skills match.
    BEHAVIOR: Be professional, premium, and focused on regional impact.
    
    Current User Context:
    Name: ${userProfile?.fullName}
    Role: ${userProfile?.role}
    College: ${userProfile?.college}
    Department: ${userProfile?.department}
    
    Guidelines:
    - Be concise, analytical, and professional.
    - Give specific advice tailored to the Jalgaon ecosystem when possible.
    - Encourage networking and connection requests within the ASG community.
    - Always act as a supportive 'Second Brain'.`;
        const chatMessages = [
            { role: "system", content: systemPrompt },
            ...history.reverse().map((msg) => ({
                role: msg.role,
                content: msg.content
            }))
        ];
        // Multimodal payload construction
        let currentMessageContent = content;
        if (fileData && mimeType) {
            currentMessageContent = [
                { type: "text", text: content || "Analyze this document." },
                {
                    type: "image_url",
                    image_url: {
                        url: `data:${mimeType};base64,${fileData}`
                    }
                }
            ];
        }
        chatMessages.push({ role: "user", content: currentMessageContent });
        // 5. Call OpenRouter
        const response = await axios.post("https://openrouter.ai/api/v1/chat/completions", {
            model: MODEL,
            messages: chatMessages
        }, {
            headers: {
                "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
                "HTTP-Referer": "https://apexstartupgroup.com",
                "X-Title": "ASG Community Platform",
                "Content-Type": "application/json"
            }
        });
        const aiResponseText = response.data.choices[0].message.content;
        // 6. Save messages to DB
        await prisma.aiMessage.create({
            data: {
                userId,
                conversationId: conversation.id,
                content: content || "[Analyzed Document]",
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
        await createActivity(userId, 'AI_SESSION', `Strategy Session (${specialization})`, `Consulted Kiri AI regarding ${specialization.toLowerCase()} strategies.`, 15);
        res.json(savedAiMsg);
    }
    catch (error) {
        console.error("AI Error:", error.response?.data || error.message);
        res.status(500).json({ error: "Kiri is having trouble thinking. Please try again later." });
    }
};
export const updateSpecialization = async (req, res) => {
    try {
        const userId = req.user.id;
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
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const createActivity = async (userId, type, title, content, points) => {
    try {
        await prisma.activity.create({
            data: { userId, type, title, content, points }
        });
        // Increment User points
        await prisma.user.update({
            where: { id: userId },
            data: { points: { increment: points } }
        });
    }
    catch (e) {
        console.error("Failed to log activity:", e);
    }
};
//# sourceMappingURL=aiController.js.map