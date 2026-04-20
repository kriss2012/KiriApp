import { Router } from 'express';
import { sendMessage, getChatHistory, getConversations, searchUsers } from '../controllers/chatController.js';
import { authenticateToken } from '../middleware/auth.js';

const router = Router();

router.post('/send', authenticateToken, sendMessage);
router.get('/history/:user1/:user2', authenticateToken, getChatHistory);
router.get('/list', authenticateToken, getConversations);
router.get('/search', authenticateToken, searchUsers);

export default router;
