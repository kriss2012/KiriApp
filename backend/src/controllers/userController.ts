import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { Role } from '@prisma/client';

export const getProfile = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }

    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
        studentLevel: true,
        department: true,
        college: true,
        year: true,
        section: true,
        isVerified: true,
        canCreateEvents: true,
        bio: true,
        skills: true,
        avatarUrl: true,
        githubUrl: true,
        linkedInUrl: true,
        phoneNumber: true,
        website: true,
        services: true,
        intent: true,
        preferredLanguage: true,
        createdAt: true
      }
    });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Mask sensitive info if not the owner and not connected
    if (requesterId && requesterId !== userId) {
      const connection = await prisma.connection.findFirst({
        where: {
          OR: [
            { senderId: requesterId, receiverId: userId },
            { senderId: userId, receiverId: requesterId }
          ],
          status: 'ACCEPTED'
        }
      });

      if (!connection) {
        // Mask it
        return res.status(200).json({
          ...user,
          email: "Connect to view",
          phoneNumber: "Connect to view"
        });
      }
    } else if (!requesterId) {
      // Unauthenticated - mask sensitive info
      return res.status(200).json({
        ...user,
        email: "Connect to view",
        phoneNumber: "Connect to view"
      });
    }

    res.status(200).json(user);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching profile', error: error.message });
  }
};

export const updateProfile = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    // Security: Only allow users to update their own profile (or ADMIN)
    if (requesterId !== userId && (req as any).user?.role !== 'ADMIN') {
        return res.status(403).json({ message: 'Unauthorized: You can only update your own profile.' });
    }
    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }
    const { fullName, bio, skills, avatarUrl, department, college, year, section, role, githubUrl, linkedInUrl, intent, preferredLanguage, phoneNumber, website, services } = req.body;

    // Robustness: Ensure role matches Enum casing (Prisma is strict)
    const normalizedRole = role ? role.toString().toUpperCase() : undefined;

    console.log(`[UpdateProfile] Incoming payload for user ${userId}:`, JSON.stringify(req.body, null, 2));

    const user = await prisma.user.update({
      where: { id: userId },
      data: {
        fullName,
        bio,
        skills,
        avatarUrl,
        department,
        college,
        year,
        section,
        role: normalizedRole as any,
        githubUrl,
        linkedInUrl,
        intent,
        preferredLanguage,
        phoneNumber,
        website,
        services
      },
      // Explicit select — returns the same field set as getProfile so the
      // Android DTO deserializes correctly and canCreateEvents is always present.
      select: {
        id: true,
        email: true,
        fullName: true,
        role: true,
        studentLevel: true,
        department: true,
        college: true,
        year: true,
        section: true,
        isVerified: true,
        canCreateEvents: true,
        bio: true,
        skills: true,
        avatarUrl: true,
        githubUrl: true,
        linkedInUrl: true,
        phoneNumber: true,
        website: true,
        services: true,
        intent: true,
        preferredLanguage: true,
        createdAt: true
      }
    });

    console.log(`[UpdateProfile] ✅ Successfully updated user ${userId} — name: "${user.fullName}", role: "${user.role}"`);

    res.status(200).json(user);
  } catch (error: any) {
    // Log Prisma-specific error codes for faster debugging
    if (error?.code) {
      console.error(`[UpdateProfile] ❌ Prisma error P${error.code} for user ${req.params['userId']}:`, error.meta ?? error.message);
    } else {
      console.error(`[UpdateProfile] ❌ Unexpected error:`, error);
    }
    res.status(500).json({ message: 'Error updating profile', error: error.message });
  }
};

export const toggleEventAccess = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }
    const { canCreateEvents } = req.body;

    const user = await prisma.user.update({
      where: { id: userId },
      data: { canCreateEvents }
    });

    res.status(200).json({ message: `Event access ${canCreateEvents ? 'granted' : 'revoked'} for ${user.fullName}` });
  } catch (error: any) {
    res.status(500).json({ message: 'Error toggling event access', error: error.message });
  }
};

export const getAllVerifiedUsers = async (req: Request, res: Response) => {
  try {
    const users = await prisma.user.findMany({
      where: { isVerified: true },
      select: { id: true, fullName: true, role: true, avatarUrl: true, canCreateEvents: true }
    });
    res.status(200).json(users);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching verified users', error: error.message });
  }
};

export const searchUsers = async (req: Request, res: Response) => {
  try {
    const name = req.query['name'] as string | undefined;
    const role = req.query['role'] as string | undefined;

    const where: any = {};
    if (name) {
      where.fullName = { contains: name as string, mode: 'insensitive' };
    }
    if (role && role !== 'ALL') {
      where.role = role as Role;
    }

    const users = await prisma.user.findMany({
      where,
      select: {
        id: true,
        fullName: true,
        role: true,
        avatarUrl: true,
        college: true,
        bio: true,
        skills: true
      },
      orderBy: { createdAt: 'desc' }
    });

    res.status(200).json(users);
  } catch (error: any) {
    res.status(500).json({ message: 'Error searching users', error: error.message });
  }
};

export const getActivities = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'] as string;
    const activities = await prisma.activity.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' }
    });
    res.json(activities);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getUserStats = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'] as string;
    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { 
        points: true, 
        _count: { 
          select: { 
            activities: true, 
            sentRequests: true,
            receivedRequests: true 
          } 
        } 
      }
    });
    res.json(user);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const getCollegeActivity = async (req: Request, res: Response) => {
  try {
    const collegeName = req.params['collegeName'] as string;
    // SPOCs can only see their own college
    const activities = await prisma.activity.findMany({
      where: {
        user: { college: collegeName }
      },
      include: {
        user: { select: { fullName: true, role: true } }
      },
      orderBy: { createdAt: 'desc' },
      take: 50
    });
    res.json(activities);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const verifyActivity = async (req: Request, res: Response) => {
  try {
    const activityId = req.params['activityId'] as string;
    // For now, verification just marks it with a status in content
    const activity = await prisma.activity.update({
      where: { id: activityId },
      data: { content: `[VERIFIED BY SPOC] ${req.body.comments || ''}` }
    });
    res.json(activity);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
