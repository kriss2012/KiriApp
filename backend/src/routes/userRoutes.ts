import { Router } from 'express';
import { 
    getAllVerifiedUsers, 
    getProfile, 
    searchUsers, 
    updateProfile,
    getActivities,
    getUserStats,
    verifyActivity,
    getGitHubStats,
    getLeaderboard,
    referUser,
    redeemPoints
} from '../controllers/userController.js';

import { authenticate } from '../middlewares/auth.js';

const router = Router();

router.get('/', searchUsers);
router.get('/leaderboard', authenticate, getLeaderboard);
router.post('/refer', authenticate, referUser);
router.post('/redeem', authenticate, redeemPoints);
router.get('/profile/:userId', authenticate, getProfile);
router.put('/profile/:userId', authenticate, updateProfile);
router.get('/verified', getAllVerifiedUsers);
router.get('/activities/:userId', getActivities);
router.get('/stats/:userId', getUserStats);
router.patch('/activity/:activityId/verify', verifyActivity);
router.get('/github-stats/:username', getGitHubStats);

export default router;
