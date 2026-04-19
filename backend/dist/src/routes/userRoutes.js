import { Router } from 'express';
import { getProfile, updateProfile, getAllVerifiedUsers, toggleEventAccess, searchUsers } from '../controllers/userController.js';
const router = Router();
router.get('/', searchUsers);
router.get('/profile/:userId', getProfile);
router.put('/profile/:userId', updateProfile);
router.put('/toggle-access/:userId', toggleEventAccess);
router.get('/verified', getAllVerifiedUsers);
export default router;
//# sourceMappingURL=userRoutes.js.map