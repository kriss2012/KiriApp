import { Router } from 'express';
import { getAiHistory, chatWithKiri } from '../controllers/aiController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.get('/history', authenticate, getAiHistory);
router.post('/chat', authenticate, chatWithKiri);
export default router;
//# sourceMappingURL=aiRoutes.js.map