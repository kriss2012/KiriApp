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
        // 4. Construct Kiri's context and messages
        const systemPrompt = `You are Kiri AI, the 'Second Brain' of the ASG (Apex Startup Group) Community Platform in Jalgaon. 
        Your goal is to assist users with entrepreneurship, academic growth, and networking.
        
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
        res.json(savedAiMsg);
    }
    catch (error) {
        console.error("AI Error:", error.response?.data || error.message);
        res.status(500).json({ error: "Kiri is having trouble thinking. Please try again later." });
    }
};
//# sourceMappingURL=aiController.js.map