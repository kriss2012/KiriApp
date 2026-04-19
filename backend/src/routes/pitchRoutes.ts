import { Router } from 'express';
import { createPitch, getAllPitches, backPitch } from '../controllers/pitchController.js';
import { authenticate } from '../middlewares/auth.js';

const router = Router();

router.use(authenticate);
router.get('/', getAllPitches);
router.post('/', createPitch);
router.post('/back', backPitch);

export default router;
