import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getMyBadges = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;

    // Check if user has any badges. If not, let's auto-award/seed some initial badges to showcase the system.
    const badgeCount = await prisma.badge.count({ where: { userId } });
    if (badgeCount === 0) {
      await prisma.badge.createMany({
        data: [
          {
            userId,
            title: "Career Pioneer",
            description: "Onboarded successfully to EmployAI Connect platform.",
            badgeType: "MILESTONE",
            icon: "🚀"
          },
          {
            userId,
            title: "Market Scholar",
            description: "Analyzed live job market intelligence trends.",
            badgeType: "SKILL",
            icon: "📈"
          },
          {
            userId,
            title: "Resume Architect",
            description: "Optimized a professional resume using Google X-Y-Z formula.",
            badgeType: "ACHIEVEMENT",
            icon: "📄"
          }
        ]
      });
    }

    const badges = await prisma.badge.findMany({
      where: { userId },
      orderBy: { earnedAt: 'desc' }
    });

    res.status(200).json({ success: true, badges });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const getUserBadges = async (req: Request, res: Response) => {
  try {
    const { userId } = req.params;
    if (!userId) {
      return res.status(400).json({ success: false, error: 'userId parameter is required' });
    }

    const badges = await prisma.badge.findMany({
      where: { userId: userId as string },
      orderBy: { earnedAt: 'desc' }
    });

    res.status(200).json({ success: true, badges });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const awardBadge = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { title, description, badgeType, icon } = req.body;

    if (!title || !description || !badgeType || !icon) {
      return res.status(400).json({ success: false, error: 'title, description, badgeType, and icon are required' });
    }

    // Check if already awarded to prevent duplicates
    const existing = await prisma.badge.findFirst({
      where: { userId, title }
    });

    if (existing) {
      return res.status(200).json({ success: true, message: 'Badge already awarded', badge: existing });
    }

    const badge = await prisma.badge.create({
      data: {
        userId,
        title,
        description,
        badgeType,
        icon
      }
    });

    res.status(201).json({ success: true, badge });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};
