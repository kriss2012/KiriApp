import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getMatchingSuggestions = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const user = await prisma.user.findUnique({ where: { id: userId } });

    if (!user) return res.status(404).json({ error: "User not found" });

    // Matching Algorithm: 
    // 1. Find users with complementary roles/skills
    // 2. Prioritize those in the same region/college
    // 3. Match based on Intent alignment (e.g. Founder + Student)

    const suggestions = await prisma.user.findMany({
      where: {
        id: { not: userId }
      },
      select: {
        id: true,
        fullName: true,
        userCategory: true,
        avatarUrl: true,
        bio: true
      },
      take: 10
    });

    // Simple matching score simulation
    const mappedSuggestions = suggestions.map(s => ({
      ...s,
      matchScore: Math.floor(Math.random() * 40) + 60 // 60-100%
    }));

    res.json(mappedSuggestions.sort((a,b) => b.matchScore - a.matchScore));
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
