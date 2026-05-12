import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';
import { createActivity } from './aiController.js';
export const createEvent = async (req, res) => {
    try {
        const { title, description, date, location, type, ownerId, registrationLink, prize, coordinatorName, coordinatorPhone, hostInstitutionId } = req.body;
        // Check if the user exists
        const user = await prisma.user.findUnique({
            where: { id: ownerId }
        });
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const { imageUrl } = req.body;
        const event = await prisma.event.create({
            data: {
                title,
                description,
                date: new Date(date),
                location,
                imageUrl,
                registrationLink,
                prize,
                coordinatorName,
                coordinatorPhone,
                hostInstitutionId,
                type: type || 'GENERAL',
                ownerId
            }
        });
        // Notify all users about the new event (Async)
        prisma.user.findMany({
            select: { id: true }
        }).then(allUsers => {
            allUsers.forEach(u => createNotification(u.id, 'New Event Added', `Check out "${title}" happening at ${location}.`, 'EVENT', event.id));
        }).catch(e => console.error("Async Event Notification Error:", e));
        // Record Innovation Activity for NAAC (Async - Point 4)
        createActivity(ownerId, 'EVENT_JOIN', 'Organized Event', `Organized "${title}" on ${date}.`, 50);
        res.status(201).json(event);
    }
    catch (error) {
        res.status(500).json({ message: 'Error creating event', error: error.message });
    }
};
export const getAllEvents = async (req, res) => {
    try {
        const events = await prisma.event.findMany({
            include: {
                owner: {
                    select: {
                        id: true,
                        fullName: true,
                        avatarUrl: true
                    }
                }
            },
            orderBy: {
                date: 'asc'
            }
        });
        res.status(200).json(events);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching events', error: error.message });
    }
};
export const getEventById = async (req, res) => {
    try {
        const eventId = req.params['id'];
        if (typeof eventId !== 'string') {
            return res.status(400).json({ message: 'Invalid Event ID' });
        }
        const event = await prisma.event.findUnique({
            where: { id: eventId },
            include: {
                owner: {
                    select: {
                        id: true,
                        fullName: true,
                        avatarUrl: true
                    }
                }
            }
        });
        if (!event) {
            return res.status(404).json({ message: 'Event not found' });
        }
        res.status(200).json(event);
    }
    catch (error) {
        res.status(500).json({ message: 'Error fetching event', error: error.message });
    }
};
export const updateEvent = async (req, res) => {
    try {
        const eventId = req.params['id'];
        if (typeof eventId !== 'string') {
            return res.status(400).json({ message: 'Invalid Event ID' });
        }
        const { title, description, date, location, type, imageUrl, registrationLink, prize, coordinatorName, coordinatorPhone, hostInstitutionId } = req.body;
        const event = await prisma.event.update({
            where: { id: eventId },
            data: {
                title,
                description,
                ...(date ? { date: new Date(date) } : {}),
                location,
                type,
                imageUrl,
                registrationLink,
                prize,
                coordinatorName,
                coordinatorPhone,
                hostInstitutionId
            }
        });
        res.status(200).json(event);
    }
    catch (error) {
        res.status(500).json({ message: 'Error updating event', error: error.message });
    }
};
export const deleteEvent = async (req, res) => {
    try {
        const eventId = req.params['id'];
        if (typeof eventId !== 'string') {
            return res.status(400).json({ message: 'Invalid Event ID' });
        }
        await prisma.event.delete({
            where: { id: eventId }
        });
        res.status(200).json({ message: 'Event deleted successfully' });
    }
    catch (error) {
        res.status(500).json({ message: 'Error deleting event', error: error.message });
    }
};
//# sourceMappingURL=eventController.js.map