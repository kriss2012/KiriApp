import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const requestSession = async (req: Request, res: Response) => {
  try {
    const founderId = (req as any).user.id;
    const { mentorId, topic, scheduledAt } = req.body;

    // MentorSession model doesn't exist in current schema - return mock response
    res.status(201).json({
      id: 'mock-session-id',
      mentorId,
      founderId,
      topic,
      scheduledAt: scheduledAt ? new Date(scheduledAt) : null,
      status: 'PENDING',
      message: 'Mentor sessions feature coming soon'
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getSessions = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    // MentorSession model doesn't exist in current schema - return empty array
    res.json([]);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const updateSessionStatus = async (req: Request, res: Response) => {
  try {
    const sessionId = req.params['sessionId'] as string;
    const { status, scheduledAt } = req.body;

    // MentorSession model doesn't exist in current schema - return mock response
    res.json({
      id: sessionId,
      status,
      scheduledAt: scheduledAt ? new Date(scheduledAt) : null,
      message: 'Mentor sessions feature coming soon'
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
