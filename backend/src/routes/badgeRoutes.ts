import { Router } from 'express';
import { getMyBadges, getUserBadges, awardBadge } from '../controllers/badgeController.js';
import { authenticate } from '../middlewares/auth.js';

const router = Router();

router.use(authenticate);
router.get('/', getMyBadges);
router.get('/user/:userId', getUserBadges);
router.post('/award', awardBadge);

export default router;
