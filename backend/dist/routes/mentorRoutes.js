import { Router } from 'express';
import { requestSession, getSessions, updateSessionStatus } from '../controllers/mentorController.js';
import { authenticate, checkRole } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.post('/request', checkRole(['STUDENT', 'FOUNDER', 'ADMIN']), requestSession);
router.get('/history', getSessions);
router.patch('/:sessionId', checkRole(['MENTOR', 'ADMIN']), updateSessionStatus);
export default router;
//# sourceMappingURL=mentorRoutes.js.map