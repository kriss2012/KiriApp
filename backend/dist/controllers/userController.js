import prisma from '../utils/prisma.js';
import axios from 'axios';
export const getProfile = async (req, res) => {
    try {
        const userId = req.params['userId'];
        const requesterId = req.user?.id || req.user?.userId;
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
        }
        else if (!requesterId) {
            // Unauthenticated - mask sensitive info
            return res.status(200).json({
                ...profileResponse,
                email: "Connect to view",
                phoneNumber: "Connect to view"
            });
        }
        res.status(200).json(profileResponse);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching profile', error: error.message });
    }
};
export const updateProfile = async (req, res) => {
    try {
        const userId = req.params['userId'];
        const requesterId = req.user?.id || req.user?.userId;
        // Security: Only allow users to update their own profile
        if (requesterId !== userId) {
            return res.status(403).json({ message: 'Unauthorized: You can only update your own profile.' });
        }
        if (typeof userId !== 'string') {
            return res.status(400).json({ message: 'Invalid User ID' });
        }
        const { fullName, bio, avatarUrl, phoneNumber, role, department, college, year, section, website, githubUrl, linkedInUrl, services, rollNo, portfolioUrl, achievements } = req.body;
        console.log(`[UpdateProfile] Incoming payload for user ${userId}:`, JSON.stringify(req.body, null, 2));
        // Map role string to UserCategory enum if applicable
        const categoryMapping = {
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
                                roleName: role
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
    }
    catch (error) {
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
        }
        else {
            console.error(`[UpdateProfile] ❌ Unexpected error:`, error);
        }
        res.status(500).json({ message: 'Error updating profile', error: error.message });
    }
};
export const getAllVerifiedUsers = async (req, res) => {
    try {
        const users = await prisma.user.findMany({
            select: { id: true, fullName: true, userCategory: true, avatarUrl: true }
        });
        res.status(200).json(users);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching users', error: error.message });
    }
};
export const searchUsers = async (req, res) => {
    try {
        const name = req.query['name'];
        const where = {};
        if (name) {
            where.fullName = { contains: name, mode: 'insensitive' };
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
    }
    catch (error) {
        res.status(500).json({ message: 'Error searching users', error: error.message });
    }
};
export const getActivities = async (req, res) => {
    try {
        const userId = req.params['userId'];
        const activities = await prisma.aalActivity.findMany({
            where: { userId },
            orderBy: { createdAt: 'desc' }
        });
        res.json(activities);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const getUserStats = async (req, res) => {
    try {
        const userId = req.params['userId'];
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
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const verifyActivity = async (req, res) => {
    try {
        const activityId = req.params['activityId'];
        // For now, verification just marks it with a status
        const activity = await prisma.aalActivity.update({
            where: { id: activityId },
            data: { status: 'VERIFIED' }
        });
        res.json(activity);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const getGitHubStats = async (req, res) => {
    try {
        const username = req.params['username'];
        if (!username || typeof username !== 'string') {
            return res.status(400).json({ message: 'GitHub username is required' });
        }
        const headers = {
            'User-Agent': 'ASG-Community-App'
        };
        if (process.env.GITHUB_TOKEN) {
            headers['Authorization'] = `token ${process.env.GITHUB_TOKEN}`;
        }
        const userRes = await axios.get(`https://api.github.com/users/${username}`, { headers, timeout: 10000 });
        const reposRes = await axios.get(`https://api.github.com/users/${username}/repos?sort=updated&per_page=10`, { headers, timeout: 10000 });
        const profileData = userRes.data;
        const reposData = reposRes.data.map((repo) => ({
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
    }
    catch (error) {
        console.error(`Failed to fetch GitHub stats for ${req.params['username']}: ${error.message}`);
        res.status(500).json({ message: 'Failed to fetch GitHub stats', error: error.message });
    }
};
//# sourceMappingURL=userController.js.map