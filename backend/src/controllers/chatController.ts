import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';

export const sendMessage = async (req: Request, res: Response) => {
  try {
    const { senderId, receiverId, content } = req.body;

    // Check if connection exists
    const existing = await prisma.connection.findFirst({
      where: {
        OR: [
          { senderId, receiverId },
          { senderId: receiverId, receiverId: senderId }
        ]
      }
    });

    if (!existing) {
      // Create a PENDING connection if it's a message to a stranger
      await prisma.connection.create({
        data: { senderId, receiverId, status: 'PENDING' }
      });

      // Create Notification for receiver
      const sender = await prisma.user.findUnique({ where: { id: senderId } });
      await createNotification(
        receiverId,
        'New Message Request',
        `${sender?.fullName || 'Someone'} sent you a message: "${content.substring(0, 30)}..."`,
        'REQUEST'
      );
    }

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
