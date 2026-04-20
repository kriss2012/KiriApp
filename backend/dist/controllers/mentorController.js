import prisma from '../utils/prisma.js';
export const requestSession = async (req, res) => {
    try {
        const founderId = req.user.id;
        const { mentorId, topic, scheduledAt } = req.body;
        const session = await prisma.mentorSession.create({
            data: {
                mentorId,
                founderId,
                topic,
                scheduledAt: scheduledAt ? new Date(scheduledAt) : null
            }
        });
        res.status(201).json(session);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const getSessions = async (req, res) => {
    try {
        const userId = req.user.id;
        const sessions = await prisma.mentorSession.findMany({
            where: {
                OR: [
                    { mentorId: userId },
                    { founderId: userId }
                ]
            },
            include: {
                mentor: { select: { fullName: true, avatarUrl: true } },
                founder: { select: { fullName: true, avatarUrl: true } }
            },
            orderBy: { createdAt: 'desc' }
        });
        res.json(sessions);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const updateSessionStatus = async (req, res) => {
    try {
        const sessionId = req.params['sessionId'];
        const { status, scheduledAt } = req.body;
        const session = await prisma.mentorSession.update({
            where: { id: sessionId },
            data: {
                status,
                scheduledAt: scheduledAt ? new Date(scheduledAt) : null
            }
        });
        res.json(session);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
//# sourceMappingURL=mentorController.js.map