import { Router } from 'express';
import { createBoardPost, getBoardPosts } from '../controllers/boardController.js';
const router = Router();
router.post('/', createBoardPost);
router.get('/', getBoardPosts);
export default router;
//# sourceMappingURL=boardRoutes.js.map