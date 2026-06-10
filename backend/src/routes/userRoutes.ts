import { Router } from 'express';
import { 
    getAllVerifiedUsers, 
    getProfile, 
    searchUsers, 
    updateProfile,
    getActivities,
    getUserStats,
    verifyActivity,
    getGitHubStats
} from '../controllers/userController.js';

import { authenticate } from '../middlewares/auth.js';

const router = Router();

router.get('/', searchUsers);
router.get('/profile/:userId', authenticate, getProfile);
router.put('/profile/:userId', authenticate, updateProfile);
router.get('/verified', getAllVerifiedUsers);
router.get('/activities/:userId', getActivities);
router.get('/stats/:userId', getUserStats);
router.patch('/activity/:activityId/verify', verifyActivity);
router.get('/github-stats/:username', getGitHubStats);

export default router;
