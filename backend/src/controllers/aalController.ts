import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

/**
 * Controller for APEX AI Launchpad (AAL) activities and onboarding.
 */

export const getOnboarding = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'] as string | undefined;
    if (!userId) {
      return res.status(400).json({ message: 'User ID is required' });
    }
    const onboarding = await prisma.aalOnboarding.findUnique({
      where: { userId }
    });
    if (!onboarding) {
      return res.status(404).json({ message: 'AAL Onboarding not found' });
    }
    res.status(200).json(onboarding);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching onboarding', error: error.message });
  }
};

export const updateMindsetScore = async (req: Request, res: Response) => {
  try {
    const { userId, mindsetScore } = req.body;
    const onboarding = await prisma.aalOnboarding.upsert({
      where: { userId },
      update: { mindsetScore },
      create: { userId, mindsetScore }
    });
    res.status(200).json(onboarding);
  } catch (error: any) {
    res.status(500).json({ message: 'Error updating mindset score', error: error.message });
  }
};

export const submitActivity = async (req: Request, res: Response) => {
  try {
    const { userId, activityNumber, submissionUrl } = req.body;
    const activity = await prisma.aalActivity.create({
      data: {
        userId,
        activityNumber,
        submissionUrl,
        status: 'SUBMITTED'
      }
    });
    res.status(201).json(activity);
  } catch (error: any) {
    res.status(500).json({ message: 'Error submitting activity', error: error.message });
  }
};

export const getActivities = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'] as string | undefined;
    if (!userId) {
      return res.status(400).json({ message: 'User ID is required' });
    }
    const activities = await prisma.aalActivity.findMany({
      where: { userId },
      orderBy: { activityNumber: 'asc' }
    });
    res.status(200).json(activities);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching activities', error: error.message });
  }
};

export const verifyActivity = async (req: Request, res: Response) => {
  try {
    const activityId = req.params['activityId'] as string | undefined;
    if (!activityId) {
      return res.status(400).json({ message: 'Activity ID is required' });
    }
    const activity = await prisma.aalActivity.update({
      where: { id: activityId },
      data: { status: 'VERIFIED' }
    });
    res.status(200).json(activity);
  } catch (error: any) {
    res.status(500).json({ message: 'Error verifying activity', error: error.message });
  }
};
