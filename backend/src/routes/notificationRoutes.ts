import { Router } from 'express';
import { getNotifications, markAsRead, markAllAsRead } from '../controllers/notificationController.js';

const router = Router();

router.get('/:userId', getNotifications);
router.patch('/:id/read', markAsRead);
router.patch('/mark-all-read/:userId', markAllAsRead);

export default router;
