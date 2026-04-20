import { Router } from 'express';
import { getMatchingSuggestions } from '../controllers/matchController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.get('/suggestions', getMatchingSuggestions);
export default router;
//# sourceMappingURL=matchRoutes.js.map