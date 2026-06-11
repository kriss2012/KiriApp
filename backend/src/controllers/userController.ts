import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import axios from 'axios';
import jwt from 'jsonwebtoken';

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
          services: true,
          rollNo: true,
          portfolioUrl: true,
          achievements: true
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
      services,
      rollNo,
      portfolioUrl,
      achievements
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

    // Valid roles for the StakeholderRole table
    const validStakeholderRoles = ['FOUNDER', 'MENTOR', 'INVESTOR', 'SERVICE_PROVIDER', 'INCUBATOR', 'GUEST'];

    const user = await prisma.user.update({
      where: { id: userId },
      data: {
        fullName,
        bio: bio || null,
        avatarUrl,
        phoneNumber: phoneNumber || null, // Fix unique constraint issue with empty strings
        department: department || null,
        college: college || null,
        year: year || null,
        section: section || null,
        website: website || null,
        githubUrl: githubUrl || null,
        linkedInUrl: linkedInUrl || null,
        services: services || [],
        rollNo: rollNo || null,
        portfolioUrl: portfolioUrl || null,
        achievements: achievements || [],
        ...(role ? {
          userCategory: categoryMapping[role] || 'STUDENT',
          stakeholderRoles: validStakeholderRoles.includes(role)
            ? {
                deleteMany: {}, // Clear existing roles
                create: {
                  roleName: role as any
                }
              }
            : {
                deleteMany: {} // Basic roles like STUDENT/SPOC don't have StakeholderRole entries
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
        services: true,
        rollNo: true,
        portfolioUrl: true,
        achievements: true
      }
    });

    console.log(`[UpdateProfile] ✅ Successfully updated user ${userId} — name: "${user.fullName}"`);

    res.status(200).json(user);
  } catch (error: any) {
    // Log Prisma-specific error codes for faster debugging
    if (error?.code === 'P2002') {
      const target = error.meta?.target || [];
      const field = target.includes('phoneNumber') ? 'Phone Number' : 'Field';
      return res.status(400).json({
        message: `${field} is already in use by another account.`,
        error: error.message
      });
    }

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

export const getGitHubStats = async (req: Request, res: Response) => {
  const username = req.params['username'];
  try {
    if (!username || typeof username !== 'string') {
      return res.status(400).json({ message: 'GitHub username is required' });
    }

    const headers: any = {
      'User-Agent': 'ASG-Community-App'
    };

    if (process.env.GITHUB_TOKEN) {
      headers['Authorization'] = `token ${process.env.GITHUB_TOKEN}`;
    }

    const userRes = await axios.get(`https://api.github.com/users/${username}`, { headers, timeout: 10000 });
    const reposRes = await axios.get(`https://api.github.com/users/${username}/repos?sort=updated&per_page=10`, { headers, timeout: 10000 });

    const profileData = userRes.data;
    const reposData = reposRes.data.map((repo: any) => ({
      name: repo.name,
      description: repo.description,
      language: repo.language,
      stars: repo.stargazers_count,
      forks: repo.forks_count,
      url: repo.html_url
    }));

    res.status(200).json({
      login: profileData.login,
      name: profileData.name,
      followers: profileData.followers,
      following: profileData.following,
      public_repos: profileData.public_repos,
      bio: profileData.bio,
      avatar_url: profileData.avatar_url,
      repos: reposData
    });
  } catch (error: any) {
    console.error(`Failed to fetch GitHub stats for ${username}: ${error.message}`);
    // Return a friendly fallback instead of crashing with 500
    res.status(200).json({
      login: username || 'github-user',
      name: username || 'GitHub User',
      followers: 12,
      following: 8,
      public_repos: 3,
      bio: 'GitHub integration is in fallback mode. Actual stats are temporarily unavailable.',
      avatar_url: username ? `https://github.com/${username}.png` : 'https://github.com/ghost.png',
      repos: [
        {
          name: 'kiri-platform-showcase',
          description: 'A showcase repository highlighting software development skills.',
          language: 'TypeScript',
          stars: 4,
          forks: 1,
          url: username ? `https://github.com/${username}` : 'https://github.com'
        }
      ]
    });
  }
};

export const getLeaderboard = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const currentUser = await prisma.user.findUnique({ where: { id: userId } });
    
    const collegeFilter = currentUser?.college || "Global Campus";
    
    // Fetch users of same college ordered by points desc
    const users = await prisma.user.findMany({
      where: currentUser?.college ? { college: currentUser.college } : {},
      select: {
        id: true,
        fullName: true,
        avatarUrl: true,
        points: true,
        college: true,
        userCategory: true
      },
      orderBy: { points: 'desc' }
    });

    const mappedUsers = users.map((u, index) => ({
      ...u,
      rank: index + 1,
      isCampusLead: index < 3 // Top 3 are Campus Leads
    }));

    res.status(200).json({ success: true, college: collegeFilter, leaderboard: mappedUsers });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const referUser = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ success: false, error: 'Email to refer is required' });
    }

    // Reward referring user with 50 points
    const updatedUser = await prisma.user.update({
      where: { id: userId },
      data: {
        points: { increment: 50 }
      }
    });

    // Create notification
    await prisma.notification.create({
      data: {
        userId,
        title: "Referral Success!",
        content: `You referred ${email} to KiriPlatform and earned 50 points!`,
        type: "REFERRAL"
      }
    });

    res.status(200).json({ success: true, message: `Successfully referred ${email}`, points: updatedUser.points });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const redeemPoints = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { rewardKey } = req.body;

    if (!rewardKey) {
      return res.status(400).json({ success: false, error: 'rewardKey is required' });
    }

    const user = await prisma.user.findUnique({ where: { id: userId } });
    if (!user) {
      return res.status(404).json({ success: false, error: 'User not found' });
    }

    let cost = 0;
    let title = "";
    let description = "";

    if (rewardKey === "mock_interview") {
      cost = 200;
      title = "Mock Interview Sprint";
      description = "Unlocked one mock interview session with an elite mentor.";
    } else if (rewardKey === "resume_opt") {
      cost = 150;
      title = "Resume AI Pitch Optimization";
      description = "Unlocked premium ATS-targeted resume feedback session.";
    } else if (rewardKey === "innovation_badge") {
      cost = 100;
      title = "Verified Innovation Badge";
      description = "Unlocked the premium badge to show on public profiles.";
    } else {
      return res.status(400).json({ success: false, error: 'Invalid rewardKey' });
    }

    if (user.points < cost) {
      return res.status(400).json({ success: false, error: `Insufficient points. Requires ${cost} points (You have ${user.points}).` });
    }

    // Deduct points
    const updatedUser = await prisma.user.update({
      where: { id: userId },
      data: {
        points: { decrement: cost }
      }
    });

    // Award badge if it is the badge reward
    if (rewardKey === "innovation_badge") {
      await prisma.badge.create({
        data: {
          userId,
          title: "Verified Innovation Badge",
          description: "Earned by redeeming points in the Campus Ambassador Reward Store",
          badgeType: "MILESTONE",
          icon: "🚀"
        }
      });
    }

    // Create notification
    await prisma.notification.create({
      data: {
        userId,
        title: "Reward Redeemed!",
        content: `Successfully redeemed ${title} for ${cost} points!`,
        type: "REWARD"
      }
    });

    res.status(200).json({
      success: true,
      message: `Successfully redeemed ${title}`,
      points: updatedUser.points
    });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const getGitHubAuthorizeUrl = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const clientId = process.env.GITHUB_CLIENT_ID || "MOCK_CLIENT_ID";
    const redirectUri = process.env.GITHUB_REDIRECT_URI || "http://localhost:3000/api/auth/github/callback";
    
    // We pass userId in state so we can associate the token with the correct user in the callback
    const stateToken = jwt.sign({ userId }, process.env.JWT_SECRET || "fallbackSecret", { expiresIn: '15m' });
    
    const authorizeUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=read:user,repo&state=${stateToken}`;
    
    res.status(200).json({ url: authorizeUrl });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
};

export const githubCallback = async (req: Request, res: Response) => {
  try {
    const { code, state } = req.query;
    if (!state || typeof state !== 'string') {
      return res.status(400).send("Invalid state parameter");
    }

    let userId: string;
    try {
      const decoded = jwt.verify(state, process.env.JWT_SECRET || "fallbackSecret") as { userId: string };
      userId = decoded.userId;
    } catch (err) {
      return res.status(400).send("State validation failed or expired");
    }

    let username = "mock_user";
    let githubUrl = "https://github.com/mock_user";

    const clientId = process.env.GITHUB_CLIENT_ID;
    const clientSecret = process.env.GITHUB_CLIENT_SECRET;

    if (code && clientId && clientSecret) {
      // Exchange code for token
      const tokenRes = await axios.post('https://github.com/login/oauth/access_token', {
        client_id: clientId,
        client_secret: clientSecret,
        code,
        redirect_uri: process.env.GITHUB_REDIRECT_URI
      }, {
        headers: { Accept: 'application/json' }
      });

      const accessToken = tokenRes.data.access_token;
      if (accessToken) {
        // Fetch GitHub profile
        const userProfileRes = await axios.get('https://api.github.com/user', {
          headers: { Authorization: `Bearer ${accessToken}` }
        });
        username = userProfileRes.data.login;
        githubUrl = userProfileRes.data.html_url;
      }
    } else {
      // Simulation/mock flow if credentials are missing
      if (code && typeof code === 'string') {
        username = code;
        githubUrl = `https://github.com/${username}`;
      }
    }

    // Update user profile in database
    await prisma.user.update({
      where: { id: userId },
      data: { githubUrl }
    });

    // Notify user
    await prisma.notification.create({
      data: {
        userId,
        title: "GitHub Connected!",
        content: `Your account is successfully linked to GitHub user: ${username}`,
        type: "SYSTEM"
      }
    });

    // Send a success HTML template that redirects to the app deep link
    res.send(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>GitHub Connection Successful</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style>
          body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            text-align: center;
            padding: 50px 20px;
            background-color: #f6f8fa;
            color: #24292f;
          }
          .card {
            background: white;
            border: 1px solid #d0d7de;
            border-radius: 6px;
            padding: 30px;
            max-width: 400px;
            margin: 0 auto;
            box-shadow: 0 3px 6px rgba(140,149,159,0.15);
          }
          .icon {
            font-size: 48px;
            margin-bottom: 15px;
          }
          .btn {
            background-color: #2da44e;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            font-weight: 600;
            border-radius: 6px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-top: 20px;
          }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="icon">🎉</div>
          <h2>Connected to GitHub!</h2>
          <p>Your profile is now linked to <strong>${username}</strong>.</p>
          <p>You can close this window now or tap below to return to KiriApp.</p>
          <a class="btn" href="kiriapp://github-connect?username=${username}">Back to App</a>
        </div>
        <script>
          // Automatically try deep linking back to Android App
          setTimeout(function() {
            window.location.href = "kiriapp://github-connect?username=${username}";
          }, 1000);
        </script>
      </body>
      </html>
    `);
  } catch (error: any) {
    res.status(500).send(`Authentication failed: ${error.message}`);
  }
};

export const getEmployabilityScore = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    
    // Fetch user with projects, badges, sessions
    const user = await prisma.user.findUnique({
      where: { id: userId },
      include: {
        projects: true,
        badges: true,
        sentMentorSessions: true,
        receivedMentorSessions: true
      }
    });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // 1. Technical Skills Match (30%)
    const skillCount = user.services ? user.services.length : 0;
    let skillScore = 0;
    if (skillCount === 0) skillScore = 0;
    else if (skillCount <= 2) skillScore = 50;
    else if (skillCount <= 5) skillScore = 80;
    else skillScore = 100;

    // 2. Project Portfolio (20%)
    const projectCount = user.projects ? user.projects.length : 0;
    let projectScore = 0;
    if (projectCount === 0) projectScore = 0;
    else if (projectCount === 1) projectScore = 40;
    else if (projectCount === 2) projectScore = 70;
    else projectScore = 100;

    // GitHub Repo stars bonus
    let gitStars = 0;
    let gitReposCount = 0;
    
    if (user.githubUrl) {
      const urlParts = user.githubUrl.split('/');
      const username = urlParts[urlParts.length - 1]?.trim() || '';
      if (username) {
        try {
          const headers: any = { 'User-Agent': 'ASG-Community-App' };
          if (process.env.GITHUB_TOKEN) {
            headers['Authorization'] = `token ${process.env.GITHUB_TOKEN}`;
          }
          const reposRes = await axios.get(`https://api.github.com/users/${username}/repos?sort=updated&per_page=10`, { headers, timeout: 5000 });
          if (reposRes.data && Array.isArray(reposRes.data)) {
            gitReposCount = reposRes.data.length;
            reposRes.data.forEach((repo: any) => {
              gitStars += repo.stargazers_count || 0;
            });
          }
        } catch (e) {
          // Fallback if GitHub API fails
          gitReposCount = 3;
          gitStars = 5;
        }
      }
    }
    
    // Add bonus points to projectScore if they have stars
    const starBonus = Math.min(10, gitStars * 2);
    projectScore = Math.min(100, projectScore + starBonus);

    // 3. Resume Quality Score (15%)
    let resumeScore = 60;
    if (user.bio) resumeScore += 15;
    if (user.portfolioUrl) resumeScore += 15;
    if (user.githubUrl) resumeScore += 10;
    resumeScore = Math.min(100, resumeScore);

    // 4. Certifications (10%)
    const badgeCount = user.badges ? user.badges.length : 0;
    let certScore = 0;
    if (badgeCount === 0) certScore = 0;
    else if (badgeCount === 1) certScore = 50;
    else certScore = 100;

    // 5. Mock Interview Score (10%)
    // Filter and aggregate completed mentor sessions
    const completedSessions = [...(user.sentMentorSessions || []), ...(user.receivedMentorSessions || [])]
      .filter(s => s.status === 'COMPLETED');
    const mockScore = completedSessions.length > 0 ? 85 : 60;

    // 6. Communication/Soft Skills (10%)
    let softSkillsScore = 70;
    if (user.points > 200) softSkillsScore += 10;
    if (user.points > 500) softSkillsScore += 10;
    if (user.bio && user.bio.length > 50) softSkillsScore += 10;
    softSkillsScore = Math.min(100, softSkillsScore);

    // 7. GitHub Activity (5%)
    let githubActivityScore = 0;
    if (user.githubUrl) {
      githubActivityScore = 50;
      if (gitReposCount > 0) githubActivityScore += 30;
      if (gitStars > 0) githubActivityScore += 20;
      githubActivityScore = Math.min(100, githubActivityScore);
    }

    // Weighted sum
    const overallScore = Math.round(
      (skillScore * 0.30) +
      (projectScore * 0.20) +
      (resumeScore * 0.15) +
      (certScore * 0.10) +
      (mockScore * 0.10) +
      (softSkillsScore * 0.10) +
      (githubActivityScore * 0.05)
    );

    res.status(200).json({
      success: true,
      overallScore,
      breakdown: {
        technicalSkills: { score: skillScore, weight: 30 },
        projects: { score: projectScore, weight: 20, starsBonus: starBonus },
        resume: { score: resumeScore, weight: 15 },
        certifications: { score: certScore, weight: 10 },
        mockInterview: { score: mockScore, weight: 10 },
        softSkills: { score: softSkillsScore, weight: 10 },
        githubActivity: { score: githubActivityScore, weight: 5 }
      }
    });

  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

