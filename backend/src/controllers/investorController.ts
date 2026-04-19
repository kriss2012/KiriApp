import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getMarketTrends = async (req: Request, res: Response) => {
  try {
    // Group pitches by category to see where the innovation is concentrated
    const trends = await prisma.pitch.groupBy({
      by: ['category'],
      _count: { id: true },
      _avg: { fundingGoal: true },
    });
    res.json(trends);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getHighPotentialPitches = async (req: Request, res: Response) => {
  try {
    const pitches = await prisma.pitch.findMany({
      include: {
        founder: {
          select: { fullName: true, avatarUrl: true, githubUrl: true, _count: { select: { activities: true } } }
        },
        _count: { select: { backers: true } }
      }
    });

    // Strategy: Health Score = (Activities * 2) + (Backer Count * 3) + (Days since creation - weighted inversely)
    const rankedPitches = pitches.map(p => {
      const activityCount = p.founder._count.activities;
      const backerCount = p._count.backers;
      const healthScore = Math.min(100, (activityCount * 5) + (backerCount * 10)); // Simplified for Demo 
      
      return {
        ...p,
        healthScore
      };
    });

    res.json(rankedPitches.sort((a,b) => b.healthScore - a.healthScore));
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getInnovationHeatmap = async (req: Request, res: Response) => {
  try {
    const heatmap = await prisma.user.groupBy({
      by: ['department'],
      _count: {
        id: true
      },
      where: {
        activities: { some: {} }
      }
    });

    res.json(heatmap);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
