import { Router } from 'express';
import { createJob, getAllJobs } from '../controllers/jobController.js';

const router = Router();

router.post('/', createJob);
router.get('/', getAllJobs);

export default router;
