import { Router } from 'express';
import { getMarketTrends, getHighPotentialPitches, getInnovationHeatmap } from '../controllers/investorController.js';
import { authenticate, checkRole } from '../middlewares/auth.js';
const router = Router();
router.use(authenticate);
router.get('/trends', checkRole(['INVESTOR', 'ADMIN']), getMarketTrends);
router.get('/high-potential', checkRole(['INVESTOR', 'ADMIN']), getHighPotentialPitches);
router.get('/heatmap', checkRole(['ADMIN', 'SPOC']), getInnovationHeatmap);
export default router;
//# sourceMappingURL=investorRoutes.js.map