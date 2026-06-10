import { Router } from 'express';
import { chatWithKiri, getAiHistory, updateSpecialization, generateResume, mockInterview } from '../controllers/aiController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.get('/history', getAiHistory);
router.post('/chat', chatWithKiri);
router.put('/specialization', updateSpecialization);
router.post('/resume/generate', generateResume);
router.post('/mock-interview', mockInterview);
export default router;
//# sourceMappingURL=aiRoutes.js.map