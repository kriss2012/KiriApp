import { Router } from 'express';
import { getOnboarding, updateMindsetScore, submitActivity, getActivities, verifyActivity } from '../controllers/aalController.js';
const router = Router();
router.get('/onboarding/:userId', getOnboarding);
router.post('/mindset', updateMindsetScore);
router.post('/activity', submitActivity);
router.get('/activities/:userId', getActivities);
router.patch('/activity/verify/:activityId', verifyActivity);
export default router;
//# sourceMappingURL=aalRoutes.js.map