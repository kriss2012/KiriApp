import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const MODEL = "nvidia/nemotron-3-super-120b-a12b:free"; // Upgraded to powerful NVIDIA Nemotron model

export const getAiHistory = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const history = await prisma.aiMessage.findMany({
      where: { userId },
      orderBy: { createdAt: 'asc' }
    });
    res.json(history);
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

    // 2. Fetch recent history (last 10 messages)
    const history = await prisma.aiMessage.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      take: 10
    });

    // 3. Construct Kiri's context and messages
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
      ...history.reverse().map((msg: any) => ({
        role: msg.role,
        content: msg.content
      }))
    ];

    // Multimodal payload construction
    let currentMessageContent: any = content;
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

    // 4. Call OpenRouter
    const response = await axios.post(
      "https://openrouter.ai/api/v1/chat/completions",
      {
        model: MODEL,
        messages: chatMessages
      },
      {
        headers: {
          "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
          "HTTP-Referer": "https://apexstartupgroup.com",
          "X-Title": "ASG Community Platform",
          "Content-Type": "application/json"
        }
      }
    );

    const aiResponseText = response.data.choices[0].message.content;

    // 5. Save only the text context to DB (Don't save files as requested)
    await prisma.aiMessage.create({
      data: { userId, content: content || "[Analyzed Document]", role: "user" }
    });

    const savedAiMsg = await prisma.aiMessage.create({
      data: { userId, content: aiResponseText, role: "assistant" }
    });

    res.json(savedAiMsg);
  } catch (error: any) {
    console.error("AI Error:", error.response?.data || error.message);
    res.status(500).json({ error: "Kiri is having trouble thinking. Please try again later." });
  }
};
