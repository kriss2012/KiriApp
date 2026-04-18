import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const sendMessage = async (req: Request, res: Response) => {
  try {
    const { senderId, receiverId, content } = req.body;
    const message = await prisma.message.create({
      data: { senderId, receiverId, content }
    });
    res.status(201).json(message);
  } catch (error: any) {
    res.status(500).json({ message: 'Error sending message', error: error.message });
  }
};

export const getChatHistory = async (req: Request, res: Response) => {
  try {
    const user1 = req.params['user1'];
    const user2 = req.params['user2'];

    if (typeof user1 !== 'string' || typeof user2 !== 'string') {
      return res.status(400).json({ message: 'Invalid User IDs' });
    }

    const messages = await prisma.message.findMany({
      where: {
        OR: [
          { senderId: user1, receiverId: user2 },
          { senderId: user2, receiverId: user1 }
        ]
      },
      orderBy: { createdAt: 'asc' }
    });
    res.status(200).json(messages);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching chat history', error: error.message });
  }
};
