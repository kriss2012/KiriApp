import { Router } from 'express';
import { getMarketTrends } from '../controllers/marketController.js';

const router = Router();

router.get('/trends', getMarketTrends);

export default router;
