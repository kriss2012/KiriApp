import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';
import { createActivity } from './aiController.js';
export const sendRequest = async (req, res) => {
    try {
        const { senderId, receiverId } = req.body;
        if (senderId === receiverId) {
            return res.status(400).json({ message: 'Cannot connect to yourself' });
        }
        const existing = await prisma.connection.findFirst({
            where: {
                OR: [
                    { senderId, receiverId },
                    { senderId: receiverId, receiverId: senderId }
                ]
            }
        });
        if (existing) {
            return res.status(400).json({
                message: existing.status === 'PENDING' ? 'Request already pending' : 'Already connected',
                status: existing.status
            });
        }
        const connection = await prisma.connection.create({
            data: { senderId, receiverId, status: 'PENDING' },
            include: { sender: true }
        });
        // Create Notification for receiver
        await createNotification(receiverId, 'New Connection Request', `${connection.sender.fullName} wants to connect with you.`, 'REQUEST', connection.id);
        res.status(201).json(connection);
    }
    catch (error) {
        res.status(500).json({ message: 'Error sending request', error: error.message });
    }
};
export const acceptRequest = async (req, res) => {
    try {
        const { connectionId, requestId } = req.body;
        const id = connectionId || requestId;
        if (!id) {
            return res.status(400).json({ message: 'connectionId or requestId is required' });
        }
        const connection = await prisma.connection.update({
            where: { id: id },
            data: { status: 'ACCEPTED' },
            include: { sender: true, receiver: true }
        });
        // Notify sender
        await createNotification(connection.senderId, 'Connection Accepted!', `${connection.receiver.fullName} accepted your connection request.`, 'REQUEST');
        // Log Activity for NAAC
        await createActivity(connection.receiverId, 'CONNECTION', 'New Mentor/Peer Connection', `Connected with ${connection.sender.fullName}.`, 25);
        await createActivity(connection.senderId, 'CONNECTION', 'New Mentor/Peer Connection', `Connected with ${connection.receiver.fullName}.`, 25);
        res.status(200).json({ message: 'Connection accepted successfully', connection });
    }
    catch (error) {
        if (error.code === 'P2025') {
            return res.status(404).json({ message: 'Connection record not found' });
        }
        res.status(500).json({ message: 'Error accepting request', error: error.message });
    }
};
export const getUserConnections = async (req, res) => {
    try {
        const userId = req.params['userId'];
        const connections = await prisma.connection.findMany({
            where: {
                OR: [
                    { senderId: userId },
                    { receiverId: userId }
                ]
            },
            include: {
                sender: { select: { id: true, fullName: true, avatarUrl: true, role: true } },
                receiver: { select: { id: true, fullName: true, avatarUrl: true, role: true } }
            }
        });
        res.status(200).json(connections);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching connections', error: error.message });
    }
};
//# sourceMappingURL=connectionController.js.map