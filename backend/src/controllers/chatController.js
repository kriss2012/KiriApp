import prisma from '../utils/prisma.js';
export const sendMessage = async (req, res) => {
    try {
        const { senderId, receiverId, content } = req.body;
        const message = await prisma.message.create({
            data: { senderId, receiverId, content }
        });
        res.status(201).json(message);
    }
    catch (error) {
        res.status(500).json({ message: 'Error sending message', error: error.message });
    }
};
export const getChatHistory = async (req, res) => {
    try {
        const { user1, user2 } = req.params;
        const messages = await prisma.message.findMany({
            where: {
                OR: [
                    { senderId: user1, receiverId: user2 },
                    { senderId: user2, receiverId: user1 }
                ]
            },
            orderBy: { createdAt: 'asc' }
        });
        res.status(200).json(messages);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching chat history', error: error.message });
    }
};
//# sourceMappingURL=chatController.js.map