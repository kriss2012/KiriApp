import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getMarketTrends = async (req: Request, res: Response) => {
  try {
    // Group pitches by status to see where the innovation is concentrated
    const trends = await prisma.pitch.groupBy({
      by: ['status'],
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
          select: { fullName: true, avatarUrl: true }
        },
        _count: { select: { backers: true } }
      }
    });

    // Strategy: Health Score based on Backer Count and creation recency
    const rankedPitches = pitches.map(p => {
      const backerCount = p._count.backers;
      const daysSinceCreation = Math.floor((Date.now() - new Date(p.createdAt).getTime()) / (1000 * 60 * 60 * 24));
      const healthScore = Math.min(100, (backerCount * 10) + Math.max(0, 30 - daysSinceCreation)); 
      
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
    // Return activity counts by user category
    const heatmap = await prisma.user.groupBy({
      by: ['userCategory'],
      _count: {
        id: true
      }
    });

    res.json(heatmap);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
