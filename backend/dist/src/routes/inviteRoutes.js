import { Router } from 'express';
import { generateInviteCode, getInvites } from '../controllers/inviteController.js';
import { authenticate, checkRole } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
// Only master admins can generate professional invites
router.post('/generate', checkRole(['ADMIN']), generateInviteCode);
router.get('/list', checkRole(['ADMIN']), getInvites);
export default router;
//# sourceMappingURL=inviteRoutes.js.map