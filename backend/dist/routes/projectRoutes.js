import { Router } from 'express';
import { getProjects, submitProject, addProjectReview, toggleFeaturedProject } from '../controllers/projectController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.get('/', getProjects);
router.post('/', submitProject);
router.post('/:projectId/reviews', addProjectReview);
router.put('/:projectId/feature', toggleFeaturedProject);
export default router;
//# sourceMappingURL=projectRoutes.js.map