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
        // Create Notification for receiver (Async - Point 4)
        createNotification(receiverId, 'New Connection Request', `${connection.sender.fullName} wants to connect with you.`, 'REQUEST', connection.id);
        res.status(201).json(connection);
    }
    catch (error) {
        res.status(500).json({ message: 'Error sending request', error: error.message });
    }
};
import { emitToUser } from '../utils/socket.js';
export const acceptRequest = async (req, res) => {
    try {
        const { connectionId, requestId, id: bodyId } = req.body;
        const id = connectionId || requestId || bodyId;
        console.log('Accepting Connection Request:', { id, body: req.body });
        if (!id) {
            return res.status(400).json({ message: 'connectionId, requestId, or id is required' });
        }
        const connection = await prisma.connection.update({
            where: { id: id },
            data: { status: 'ACCEPTED' },
            include: { sender: true, receiver: true }
        });
        // SIDE EFFECTS (Async - Point 4 Kitchen Analogy)
        // Notify sender via real-time and DB
        createNotification(connection.senderId, 'Connection Accepted!', `${connection.receiver.fullName} accepted your connection request.`, 'REQUEST', connection.id);
        // REAL-TIME emission
        emitToUser(connection.senderId, 'connection_accepted', connection);
        // Create System Message in Chat
        prisma.message.create({
            data: {
                senderId: connection.receiverId,
                receiverId: connection.senderId,
                content: `🤝 Connection accepted! You can now see each other's full profile and chat freely.`
            }
        }).catch(e => console.error("Async Message Error:", e));
        // Log Activity for NAAC
        createActivity(connection.receiverId, 'CONNECTION', 'New Mentor/Peer Connection', `Connected with ${connection.sender.fullName}.`, 25);
        createActivity(connection.senderId, 'CONNECTION', 'New Mentor/Peer Connection', `Connected with ${connection.receiver.fullName}.`, 25);
        res.status(200).json({ message: 'Connection accepted successfully', connection });
    }
    catch (error) {
        console.error('Accept Connection Error:', error);
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
                sender: { select: { id: true, fullName: true, avatarUrl: true, userCategory: true } },
                receiver: { select: { id: true, fullName: true, avatarUrl: true, userCategory: true } }
            }
        });
        res.status(200).json(connections);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching connections', error: error.message });
    }
};
//# sourceMappingURL=connectionController.js.map