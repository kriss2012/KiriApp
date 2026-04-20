import prisma from '../utils/prisma.js';
import crypto from 'crypto';
export const generateInviteCode = async (req, res) => {
    try {
        const { targetRole, expiryDays } = req.body;
        // Generate a unique 8-character code
        const code = crypto.randomBytes(4).toString('hex').toUpperCase();
        const expiresAt = new Date();
        expiresAt.setDate(expiresAt.getDate() + (expiryDays || 7));
        const invite = await prisma.inviteCode.create({
            data: {
                code,
                targetRole,
                expiresAt
            }
        });
        res.status(201).json(invite);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
export const getInvites = async (req, res) => {
    try {
        const invites = await prisma.inviteCode.findMany({
            orderBy: { createdAt: 'desc' }
        });
        res.json(invites);
    }
    catch (error) {
        res.status(500).json({ error: error.message });
    }
};
//# sourceMappingURL=inviteController.js.map