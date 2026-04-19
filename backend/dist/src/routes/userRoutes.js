import { Router } from 'express';
import { getAllVerifiedUsers, getProfile, searchUsers, toggleEventAccess, updateProfile, getActivities, getUserStats, getCollegeActivity, verifyActivity } from '../controllers/userController.js';
const router = Router();
router.get('/', searchUsers);
router.get('/profile/:userId', getProfile);
router.put('/profile/:userId', updateProfile);
router.put('/toggle-access/:userId', toggleEventAccess);
router.get('/verified', getAllVerifiedUsers);
router.get('/activities/:userId', getActivities);
router.get('/stats/:userId', getUserStats);
router.get('/college/:collegeName/activity', getCollegeActivity);
router.patch('/activity/:activityId/verify', verifyActivity);
export default router;
//# sourceMappingURL=userRoutes.js.map