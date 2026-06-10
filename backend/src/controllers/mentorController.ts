import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';

export const requestSession = async (req: Request, res: Response) => {
  try {
    const founderId = (req as any).user.id;
    const { mentorId, topic, scheduledAt } = req.body;

    if (!mentorId || !topic) {
      return res.status(400).json({ error: 'mentorId and topic are required' });
    }

    const session = await prisma.mentorSession.create({
      data: {
        mentorId,
        founderId,
        topic,
        scheduledAt: scheduledAt ? new Date(scheduledAt) : null,
        status: 'PENDING'
      },
      include: {
        mentor: true,
        founder: true
      }
    });

    // Notify the mentor about the mentorship request
    await createNotification(
      mentorId,
      'New Mentorship Request',
      `${session.founder.fullName} has requested a 1:1 session: "${topic}"`,
      'REQUEST',
      session.id
    );

    res.status(201).json(session);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getSessions = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const sessions = await prisma.mentorSession.findMany({
      where: {
        OR: [
          { founderId: userId },
          { mentorId: userId }
        ]
      },
      include: {
        mentor: true,
        founder: true
      },
      orderBy: {
        createdAt: 'desc'
      }
    });
    res.json(sessions);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const updateSessionStatus = async (req: Request, res: Response) => {
  try {
    const sessionId = req.params['sessionId'] as string;
    const { status, scheduledAt } = req.body;

    const updateData: any = {};
    if (status) {
      updateData.status = status;
    }
    if (scheduledAt) {
      updateData.scheduledAt = new Date(scheduledAt);
    }

    const session = await prisma.mentorSession.update({
      where: { id: sessionId },
      data: updateData,
      include: {
        mentor: true,
        founder: true
      }
    });

    // Notify the founder about status update
    await createNotification(
      session.founderId,
      'Mentorship Request Update',
      `Your mentorship request with ${session.mentor.fullName} has been ${status.toLowerCase()}.`,
      'ALERT',
      session.id
    );

    res.json(session);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
