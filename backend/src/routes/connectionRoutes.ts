import { Router } from 'express';
import { sendRequest, acceptRequest, getUserConnections } from '../controllers/connectionController.js';

const router = Router();

router.post('/send', sendRequest);
router.post('/accept', acceptRequest);
router.get('/:userId', getUserConnections);
router.get('/list/:userId', getUserConnections);

export default router;
