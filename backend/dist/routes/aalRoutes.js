import { Router } from 'express';
import { getOnboarding, updateMindsetScore, submitActivity, getActivities, verifyActivity, getInstitutions, registerForEvent, submitLiveInput, getAiMatches } from '../controllers/aalController.js';
import { getBoardPosts } from '../controllers/boardController.js';
import { getAllJobs } from '../controllers/jobController.js';
import { getAllEvents } from '../controllers/eventController.js';
const router = Router();
router.get('/onboarding/:userId', getOnboarding);
router.post('/mindset', updateMindsetScore);
router.post('/activity', submitActivity);
router.post('/activities/submit', submitActivity); // Client support alias
router.get('/activities/:userId', getActivities);
router.patch('/activity/verify/:activityId', verifyActivity);
// Core Ecosystem board & Jobs-Projects mounts matching Android client expectations
router.get('/board', getBoardPosts);
router.get('/jobs-projects', getAllJobs);
router.get('/institutions', getInstitutions);
router.get('/events/aal', getAllEvents);
router.post('/events/register', registerForEvent);
router.post('/live-inputs', submitLiveInput);
router.get('/matches/:userId', getAiMatches);
export default router;
//# sourceMappingURL=aalRoutes.js.map