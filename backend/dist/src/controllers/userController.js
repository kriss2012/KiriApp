import prisma from '../utils/prisma.js';
import { Role } from '@prisma/client';
export const getProfile = async (req, res) => {
    try {
        const userId = req.params['userId'];
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
                createdAt: true
            }
        });
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        res.status(200).json(user);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching profile', error: error.message });
    }
};
export const updateProfile = async (req, res) => {
    try {
        const userId = req.params['userId'];
        if (typeof userId !== 'string') {
            return res.status(400).json({ message: 'Invalid User ID' });
        }
        const { fullName, bio, skills, avatarUrl, department, college, year, section, role } = req.body;
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
                role
            }
        });
        res.status(200).json(user);
    }
    catch (error) {
        res.status(500).json({ message: 'Error updating profile', error: error.message });
    }
};
export const toggleEventAccess = async (req, res) => {
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
    }
    catch (error) {
        res.status(500).json({ message: 'Error toggling event access', error: error.message });
    }
};
export const getAllVerifiedUsers = async (req, res) => {
    try {
        const users = await prisma.user.findMany({
            where: { isVerified: true },
            select: { id: true, fullName: true, role: true, avatarUrl: true, canCreateEvents: true }
        });
        res.status(200).json(users);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching verified users', error: error.message });
    }
};
export const searchUsers = async (req, res) => {
    try {
        const name = req.query['name'];
        const role = req.query['role'];
        const where = {};
        if (name) {
            where.fullName = { contains: name, mode: 'insensitive' };
        }
        if (role && role !== 'ALL') {
            where.role = role;
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
    }
    catch (error) {
        res.status(500).json({ message: 'Error searching users', error: error.message });
    }
};
//# sourceMappingURL=userController.js.map