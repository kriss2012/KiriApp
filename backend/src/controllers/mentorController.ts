import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const requestSession = async (req: Request, res: Response) => {
  try {
    const founderId = (req as any).user.id;
    const { mentorId, topic, scheduledAt } = req.body;

    const session = await prisma.mentorSession.create({
      data: {
        mentorId,
        founderId,
        topic,
        scheduledAt: scheduledAt ? new Date(scheduledAt) : null
      }
    });

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
          { mentorId: userId },
          { founderId: userId }
        ]
      },
      include: {
        mentor: { select: { fullName: true, avatarUrl: true } },
        founder: { select: { fullName: true, avatarUrl: true } }
      },
      orderBy: { createdAt: 'desc' }
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

    const session = await prisma.mentorSession.update({
      where: { id: sessionId },
      data: { 
        status,
        scheduledAt: scheduledAt ? new Date(scheduledAt) : null
      }
    });
    res.json(session);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
