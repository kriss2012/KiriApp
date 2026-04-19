import { Router } from 'express';
import { chatWithKiri, getAiHistory, updateSpecialization } from '../controllers/aiController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.get('/history', getAiHistory);
router.post('/chat', chatWithKiri);
router.put('/specialization', updateSpecialization);
export default router;
//# sourceMappingURL=aiRoutes.js.map