import { Router } from 'express';
import { sendMessage, getChatHistory } from '../controllers/chatController.js';

const router = Router();

router.post('/send', sendMessage);
router.get('/history/:user1/:user2', getChatHistory);

export default router;
