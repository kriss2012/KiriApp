import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';
import { emitToUser } from '../utils/socket.js';

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
      data: { senderId, receiverId, content },
      include: { sender: true, receiver: true }
    });

    // PERMANENT FIX: Emit real-time message
    emitToUser(receiverId, 'receive_message', message);
    // Also emit to sender (to sync multiple devices if needed, or acknowledge)
    emitToUser(senderId, 'receive_message', message);

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

export const getConversations = async (req: Request, res: Response) => {
    try {
        const userId = (req as any).user.id;

        // Fetch all messages involving the user
        const messages = await prisma.message.findMany({
            where: {
                OR: [
                    { senderId: userId },
                    { receiverId: userId }
                ]
            },
            orderBy: { createdAt: 'desc' },
            include: {
                sender: { select: { id: true, fullName: true, avatarUrl: true, userCategory: true } },
                receiver: { select: { id: true, fullName: true, avatarUrl: true, userCategory: true } }
            }
        });

        // Group by user pair
        const convosMap = new Map();

        messages.forEach((msg: any) => {
            const otherUser = msg.senderId === userId ? msg.receiver : msg.sender;
            if (!otherUser) return;

            if (!convosMap.has(otherUser.id)) {
                convosMap.set(otherUser.id, {
                    otherUser,
                    lastMessage: msg,
                    unreadCount: 0 // Placeholder for unread logic
                });
            }
        });

        // Optional: Add AI Bot as a persistent conversation if wanted (handled frontend too)
        
        const conversations = Array.from(convosMap.values());
        res.json(conversations);

    } catch (error: any) {
        res.status(500).json({ error: error.message });
    }
};

export const searchUsers = async (req: Request, res: Response) => {
    try {
        const { query } = req.query;
        if (!query) return res.json([]);

        const users = await prisma.user.findMany({
            where: {
                AND: [
                    { fullName: { contains: query as string, mode: 'insensitive' } },
                    { id: { not: (req as any).user.id } }
                ]
            },
            take: 10,
            select: { id: true, fullName: true, avatarUrl: true, userCategory: true }
        });

        res.json(users);
    } catch (error: any) {
        res.status(500).json({ error: error.message });
    }
};
