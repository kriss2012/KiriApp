import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY;
const MODEL = "meta-llama/llama-3.1-8b-instruct:free"; // Switched to high-speed free model to avoid Render timeout

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
  const requestId = Math.random().toString(36).substring(7);
  console.log(`[${requestId}] AI Chat Start`);
  try {
    const userId = (req as any).user.id;
    const { content, fileData, mimeType } = req.body;
    
    console.log(`[${requestId}] User: ${userId}, Content Length: ${content?.length}, Has File: ${!!fileData}`);
    if (fileData) console.log(`[${requestId}] File Type: ${mimeType}, File Size: ${(fileData.length / 1024).toFixed(2)} KB`);

    // 1. Fetch user profile
    console.log(`[${requestId}] Fetching profile...`);
    const userProfile = await prisma.user.findUnique({
      where: { id: userId }
    });
    console.log(`[${requestId}] Profile fetched.`);

    // 2. Fetch history
    console.log(`[${requestId}] Fetching history...`);
    const history = await prisma.aiMessage.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      take: 10
    });
    console.log(`[${requestId}] History fetched.`);

    // 3. Construct payload
    console.log(`[${requestId}] Constructing payload...`);
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
    console.log(`[${requestId}] Payload ready. Model: ${MODEL}`);

    // 4. Call OpenRouter
    console.log(`[${requestId}] Calling OpenRouter...`);
    const startTime = Date.now();
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
        },
        timeout: 90000 // 90 second timeout for large models
      }
    );
    const duration = Date.now() - startTime;
    console.log(`[${requestId}] OpenRouter responded in ${duration}ms`);

    const aiResponseText = response.data.choices[0].message.content;

    // 5. Save to DB
    console.log(`[${requestId}] Saving dialogue to DB...`);
    await prisma.aiMessage.create({
      data: { userId, content: content || "[Analyzed Document]", role: "user" }
    });

    const savedAiMsg = await prisma.aiMessage.create({
      data: { userId, content: aiResponseText, role: "assistant" }
    });
    console.log(`[${requestId}] Saved. Success.`);

    res.json(savedAiMsg);
  } catch (error: any) {
    console.error(`[${requestId}] AI Error:`, error.response?.data || error.message);
    const status = error.response?.status || 500;
    res.status(status).json({ error: "Kiri is having trouble thinking. Error: " + (error.response?.data?.error?.message || error.message) });
  }
};
