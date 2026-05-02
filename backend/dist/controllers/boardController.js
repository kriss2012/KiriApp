import prisma from '../utils/prisma.js';
export const createBoardPost = async (req, res) => {
    try {
        const { authorUserId, postType, title, description, mediaUrl } = req.body;
        const post = await prisma.ecosystemBoard.create({
            data: {
                authorUserId,
                postType,
                title,
                description,
                mediaUrl
            }
        });
        res.status(201).json(post);
    }
    catch (error) {
        res.status(500).json({ message: 'Error creating board post', error: error.message });
    }
};
export const getBoardPosts = async (req, res) => {
    try {
        const posts = await prisma.ecosystemBoard.findMany({
            include: {
                author: {
                    select: {
                        fullName: true,
                        avatarUrl: true
                    }
                }
            },
            orderBy: { createdAt: 'desc' }
        });
        res.status(200).json(posts);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching board posts', error: error.message });
    }
};
//# sourceMappingURL=boardController.js.map