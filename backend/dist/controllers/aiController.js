import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';
dotenv.config();
const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const PRIMARY_MODEL = "google/gemini-2.0-flash-001";
const FALLBACK_MODEL = "meta-llama/llama-3.3-70b-instruct";
export const getAiHistory = async (req, res) => {
    try {
        const userId = req.user.id;
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
        const mentorContext = relevantMentors.map(m => `- ${m.fullName}: ${m.bio?.substring(0, 50)}...`).join("\n");
        // Multilingual Protocol - default to English
        const langInstruction = "Respond primarily in English.";
        // 5. Construct Kiri's context and messages
        const systemPromptPrefix = `[SYSTEM INSTRUCTIONS: You are Kiri, the Agentic Orchestrator of the ASG Community. 
    LANGUAGE: ${langInstruction}
    MISSION: ${specializationContext}
    ECOSYSTEM: ${mentorContext}]

    CONTEXT: User Name: ${userProfile?.fullName}.

    `;
        // Map history to safe User/Assistant turns
        const historyTurns = history.reverse().map((msg) => ({
            role: msg.role === 'assistant' ? 'assistant' : 'user',
            content: msg.content || "..."
        }));
        // Construct payload with Prepending to the first message
        const chatMessages = [];
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
        }
        else {
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
                const response = await axios.post("https://openrouter.ai/api/v1/chat/completions", {
                    model: model,
                    messages: finalValidMessages,
                    temperature: 0.6,
                    max_tokens: 1500,
                    repetition_penalty: 1.1
                }, {
                    headers: {
                        "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
                        "HTTP-Referer": "https://apexstartupgroup.com",
                        "X-Title": "ASG Community Platform",
                        "Content-Type": "application/json"
                    },
                    timeout: 40000 // 40s per attempt
                });
                if (response.data?.choices?.[0]?.message?.content) {
                    aiResponseText = response.data.choices[0].message.content;
                    console.log(`[AI] Success with model: ${model}`);
                    break; // Exit loop on success
                }
                else {
                    throw new Error("Empty response from provider");
                }
            }
            catch (err) {
                lastError = err;
                console.warn(`[AI] Model ${model} failed:`, err.response?.data || err.message);
                // Continue to next model
            }
        }
        if (!aiResponseText) {
            console.error("AI Exhaustion: All models failed.", lastError?.response?.data || lastError?.message);
            return res.status(500).json({ error: "Kiri is currently overwhelmed. Please try again in a few moments." });
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
    }
    catch (error) {
        console.error("AI Error Detailed:", error.response?.data || error.message);
        res.status(500).json({ error: "Kiri is having trouble thinking. Please try again later." });
    }
};
export const updateSpecialization = async (req, res) => {
    try {
        const userId = req.user.id;
        // Specialization is no longer stored on conversations in current schema
        // This is now a no-op for backwards compatibility
        res.json({ message: "Specialization mode is deprecated in this version" });
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const createActivity = async (userId, type, title, content, points) => {
    try {
        // Just increment User points - activity logging deprecated in current schema
        await prisma.user.update({
            where: { id: userId },
            data: { points: { increment: points } }
        });
    }
    catch (e) {
        console.error("Failed to update points:", e);
    }
};
//# sourceMappingURL=aiController.js.map