import { Router } from 'express';
import { sendMessage, getChatHistory, getConversations, searchUsers } from '../controllers/chatController.js';
import { authenticate } from '../middlewares/auth.js';

const router = Router();

router.post('/send', authenticate, sendMessage);
router.get('/history/:user1/:user2', authenticate, getChatHistory);
router.get('/list', authenticate, getConversations);
router.get('/search', authenticate, searchUsers);

export default router;
