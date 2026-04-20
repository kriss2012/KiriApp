import prisma from '../utils/prisma.js';
export const getNotifications = async (req, res) => {
    try {
        const userId = req.params['userId'];
        if (typeof userId !== 'string') {
            return res.status(400).json({ message: 'Invalid User ID' });
        }
        const notifications = await prisma.notification.findMany({
            where: { userId },
            orderBy: { createdAt: 'desc' }
        });
        res.status(200).json(notifications);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching notifications', error: error.message });
    }
};
export const markAsRead = async (req, res) => {
    try {
        const notificationId = req.params['id'];
        if (typeof notificationId !== 'string') {
            return res.status(400).json({ message: 'Invalid Notification ID' });
        }
        const notification = await prisma.notification.update({
            where: { id: notificationId },
            data: { isRead: true }
        });
        res.status(200).json(notification);
    }
    catch (error) {
        res.status(500).json({ message: 'Error marking notification as read', error: error.message });
    }
};
export const markAllAsRead = async (req, res) => {
    try {
        const userId = req.params['userId'];
        if (typeof userId !== 'string') {
            return res.status(400).json({ message: 'Invalid User ID' });
        }
        await prisma.notification.updateMany({
            where: { userId, isRead: false },
            data: { isRead: true }
        });
        res.status(200).json({ message: 'All notifications marked as read' });
    }
    catch (error) {
        res.status(500).json({ message: 'Error marking all as read', error: error.message });
    }
};
import { emitToUser } from '../utils/socket.js';
export const createNotification = async (userId, title, content, type, relatedId = null) => {
    try {
        const notification = await prisma.notification.create({
            data: {
                userId,
                title,
                content,
                type,
                relatedId: relatedId ?? null
            }
        });
        // PERMANENT FIX: Emit real-time notification
        emitToUser(userId, 'new_notification', notification);
        return notification;
    }
    catch (error) {
        console.error('Failed to create notification:', error);
    }
};
//# sourceMappingURL=notificationController.js.map