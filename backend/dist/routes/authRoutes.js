import { Router } from 'express';
import { register, login, refresh } from '../controllers/authController.js';
import { githubCallback } from '../controllers/userController.js';
const router = Router();
router.post('/register', register);
router.post('/login', login);
router.post('/refresh', refresh);
router.get('/github/callback', githubCallback);
export default router;
//# sourceMappingURL=authRoutes.js.map