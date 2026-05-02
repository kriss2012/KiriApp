import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getProfile = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }

    const [user, eventsCount, sentAccepted, receivedAccepted] = await Promise.all([
      prisma.user.findUnique({
        where: { id: userId },
        select: {
          id: true,
          email: true,
          fullName: true,
          userCategory: true,
          digitalPersona: true,
          bio: true,
          avatarUrl: true,
          phoneNumber: true,
          points: true,
          createdAt: true,
          // Added fields for complete profile
          department: true,
          college: true,
          year: true,
          section: true,
          website: true,
          githubUrl: true,
          linkedInUrl: true,
          services: true
        }
      }),
      prisma.event.count({ where: { ownerId: userId } }),
      prisma.connection.count({ where: { senderId: userId, status: 'ACCEPTED' } }),
      prisma.connection.count({ where: { receiverId: userId, status: 'ACCEPTED' } })
    ]);

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    const connectionsCount = sentAccepted + receivedAccepted;
    const profileResponse = {
      ...user,
      eventsCount,
      connectionsCount
    };

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
          ...profileResponse,
          email: "Connect to view",
          phoneNumber: "Connect to view"
        });
      }
    } else if (!requesterId) {
      // Unauthenticated - mask sensitive info
      return res.status(200).json({
        ...profileResponse,
        email: "Connect to view",
        phoneNumber: "Connect to view"
      });
    }

    res.status(200).json(profileResponse);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching profile', error: error.message });
  }
};

export const updateProfile = async (req: Request, res: Response) => {
  try {
    const userId = req.params['userId'];
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    // Security: Only allow users to update their own profile
    if (requesterId !== userId) {
        return res.status(403).json({ message: 'Unauthorized: You can only update your own profile.' });
    }
    if (typeof userId !== 'string') {
      return res.status(400).json({ message: 'Invalid User ID' });
    }
    const {
      fullName,
      bio,
      avatarUrl,
      phoneNumber,
      role,
      department,
      college,
      year,
      section,
      website,
      githubUrl,
      linkedInUrl,
      services
    } = req.body;

    console.log(`[UpdateProfile] Incoming payload for user ${userId}:`, JSON.stringify(req.body, null, 2));

    // Map role string to UserCategory enum if applicable
    const categoryMapping: Record<string, any> = {
      'STUDENT': 'STUDENT',
      'FOUNDER': 'NON_STUDENT',
      'MENTOR': 'NON_STUDENT',
      'SPOC': 'FACULTY',
      'ALUMNI': 'ALUMNI'
    };

    const user = await prisma.user.update({
      where: { id: userId },
      data: {
        fullName,
        bio,
        avatarUrl,
        phoneNumber,
        department,
        college,
        year,
        section,
        website,
        githubUrl,
        linkedInUrl,
        services,
        ...(role ? {
          userCategory: categoryMapping[role],
          stakeholderRoles: {
            deleteMany: {}, // Clear existing roles
            create: {
              roleName: role
            }
          }
        } : {})
      },
      // Explicit select — returns the same field set as getProfile
      select: {
        id: true,
        email: true,
        fullName: true,
        userCategory: true,
        digitalPersona: true,
        bio: true,
        avatarUrl: true,
        phoneNumber: true,
        points: true,
        createdAt: true,
        department: true,
        college: true,
        year: true,
        section: true,
        website: true,
        githubUrl: true,
        linkedInUrl: true,
        services: true
      }
    });

    console.log(`[UpdateProfile] ✅ Successfully updated user ${userId} — name: "${user.fullName}"`);

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


export const getAllVerifiedUsers = async (req: Request, res: Response) => {
  try {
    const users = await prisma.user.findMany({
      select: { id: true, fullName: true, userCategory: true, avatarUrl: true }
    });
    res.status(200).json(users);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching users', error: error.message });
  }
};

export const searchUsers = async (req: Request, res: Response) => {
  try {
    const name = req.query['name'] as string | undefined;

    const where: any = {};
    if (name) {
      where.fullName = { contains: name as string, mode: 'insensitive' };
    }

    const users = await prisma.user.findMany({
      where,
      select: {
        id: true,
        fullName: true,
        userCategory: true,
        avatarUrl: true,
        bio: true
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
    const activities = await prisma.aalActivity.findMany({
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
    const [aalActivitiesCount, sentRequestsCount, receivedRequestsCount] = await Promise.all([
      prisma.aalActivity.count({ where: { userId } }),
      prisma.connection.count({ where: { senderId: userId } }),
      prisma.connection.count({ where: { receiverId: userId } })
    ]);
    res.json({
      aalActivitiesCount,
      sentRequestsCount,
      receivedRequestsCount
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};


export const verifyActivity = async (req: Request, res: Response) => {
  try {
    const activityId = req.params['activityId'] as string;
    // For now, verification just marks it with a status
    const activity = await prisma.aalActivity.update({
      where: { id: activityId },
      data: { status: 'VERIFIED' }
    });
    res.json(activity);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};
