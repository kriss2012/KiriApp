import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getNotifications = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }

    const notifications = await prisma.notification.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' }
    });

    res.status(200).json(notifications);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching notifications', error: error.message });
  }
};

export const markAsRead = async (req: Request, res: Response) => {
  try {
    const notificationId = req.params['id'];
    if (typeof notificationId !== 'string') {
      return res.status(400).json({ message: 'Invalid Notification ID' });
    }

    const notification = await prisma.notification.update({
      where: { id: notificationId },
      data: { isRead: true }
    });

    res.status(200).json(notification);
  } catch (error: any) {
    res.status(500).json({ message: 'Error marking notification as read', error: error.message });
  }
};

export const createNotification = async (userId: string, title: string, content: string, type: string, relatedId: string | null = null) => {
  try {
    return await prisma.notification.create({
      data: {
        userId,
        title,
        content,
        type,
        relatedId: relatedId ?? null
      }
    });
  } catch (error) {
    console.error('Failed to create notification:', error);
  }
};
