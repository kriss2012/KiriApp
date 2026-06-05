import { Router } from 'express';
import { createEvent, getAllEvents, getEventById, updateEvent, deleteEvent, saveFormSchema, downloadRegistrations } from '../controllers/eventController.js';
import { authenticate } from '../middlewares/auth.js';
const router = Router();
router.post('/', createEvent);
router.get('/', getAllEvents);
router.get('/:id', getEventById);
router.put('/:id', updateEvent);
router.delete('/:id', deleteEvent);
// New features
router.put('/:id/form-schema', authenticate, saveFormSchema);
router.get('/:id/download-registrations', authenticate, downloadRegistrations);
export default router;
//# sourceMappingURL=eventRoutes.js.map