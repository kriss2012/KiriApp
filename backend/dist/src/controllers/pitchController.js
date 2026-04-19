import prisma from '../utils/prisma.js';
import { createActivity } from './aiController.js';
import { createNotification } from './notificationController.js';
export const createPitch = async (req, res) => {
    try {
        const userId = req.user.id;
        const { title, description, problem, solution, impact, fundingGoal, category } = req.body;
        const pitch = await prisma.pitch.create({
            data: {
                title,
                description,
                problem,
                solution,
                impact,
                fundingGoal: parseFloat(fundingGoal),
                category: category || 'TECH',
                founderId: userId
            }
        });
        // Log high-value activity for NAAC
        await createActivity(userId, 'RECORD_CREATED', 'Innovation Pitch Submitted', `Marketplace Pitch: ${title}`, 100);
        res.status(201).json(pitch);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const getAllPitches = async (req, res) => {
    try {
        const pitches = await prisma.pitch.findMany({
            include: {
                founder: {
                    select: { fullName: true, avatarUrl: true, college: true }
                },
                _count: {
                    select: { backers: true }
                }
            },
            orderBy: { createdAt: 'desc' }
        });
        res.json(pitches);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const backPitch = async (req, res) => {
    try {
        const userId = req.user.id;
        const { pitchId, points } = req.body;
        // 1. Check if user has enough points
        const user = await prisma.user.findUnique({ where: { id: userId } });
        if (!user || user.points < points) {
            return res.status(400).json({ error: "Insufficient Innovation Points" });
        }
        // 2. Create backing
        const backing = await prisma.backer.create({
            data: {
                userId,
                pitchId,
                points
            }
        });
        // 3. Deduct points from user
        await prisma.user.update({
            where: { id: userId },
            data: { points: { decrement: points } }
        });
        // 4. Notify founder
        const pitch = await prisma.pitch.findUnique({ where: { id: pitchId } });
        if (pitch) {
            await createNotification(pitch.founderId, 'Project Backed!', `${user.fullName} supported your pitch with ${points} points.`, 'SYSTEM');
        }
        res.json(backing);
    }
    catch (error) {
        if (error.code === 'P2002') {
            return res.status(400).json({ error: "You have already backed this project." });
        }
        res.status(500).json({ error: error.message });
    }
};
//# sourceMappingURL=pitchController.js.map