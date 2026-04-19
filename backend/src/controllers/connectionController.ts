import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';

export const sendRequest = async (req: Request, res: Response) => {
  try {
    const { senderId, receiverId } = req.body;

    const existing = await prisma.connection.findUnique({
      where: {
        senderId_receiverId: { senderId, receiverId }
      }
    });

    if (existing) {
      return res.status(400).json({ message: 'Request already sent' });
    }

    const connection = await prisma.connection.create({
      data: { senderId, receiverId, status: 'PENDING' },
      include: { sender: true }
    });

    // Create Notification for receiver
    await createNotification(
      receiverId,
      'New Connection Request',
      `${connection.sender.fullName} wants to connect with you.`,
      'REQUEST'
    );

    res.status(201).json(connection);
  } catch (error: any) {
    res.status(500).json({ message: 'Error sending request', error: error.message });
  }
};

export const acceptRequest = async (req: Request, res: Response) => {
  try {
    const { connectionId } = req.body;

    const connection = await prisma.connection.update({
      where: { id: connectionId },
      data: { status: 'ACCEPTED' },
      include: { receiver: true }
    });

    // Create Notification for sender
    await createNotification(
      connection.senderId,
      'Request Accepted',
      `${connection.receiver.fullName} accepted your connection request!`,
      'REQUEST'
    );

    res.status(200).json(connection);
  } catch (error: any) {
    res.status(500).json({ message: 'Error accepting request', error: error.message });
  }
};

export const getUserConnections = async (req: Request, res: Response) => {
    try {
        const userId = req.params['userId'];
        const connections = await prisma.connection.findMany({
            where: {
                OR: [
                    { senderId: userId },
                    { receiverId: userId }
                ]
            },
            include: {
                sender: { select: { id: true, fullName: true, avatarUrl: true, role: true } },
                receiver: { select: { id: true, fullName: true, avatarUrl: true, role: true } }
            }
        });
        res.status(200).json(connections);
    } catch (error: any) {
        res.status(500).json({ message: 'Error fetching connections', error: error.message });
    }
};
