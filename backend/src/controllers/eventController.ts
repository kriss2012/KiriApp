import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';
import { createActivity } from './aiController.js';

export const createEvent = async (req: Request, res: Response) => {
  try {
    const { 
      title, description, date, location, type, 
      ownerId, registrationLink, prize, 
      coordinatorName, coordinatorPhone, hostInstitutionId 
    } = req.body;

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
      allUsers.forEach(u => 
        createNotification(u.id, 'New Event Added', `Check out "${title}" happening at ${location}.`, 'EVENT', event.id)
      );
    }).catch(e => console.error("Async Event Notification Error:", e));

    // Record Innovation Activity for NAAC (Async - Point 4)
    createActivity(ownerId, 'EVENT_JOIN', 'Organized Event', `Organized "${title}" on ${date}.`, 50);

    res.status(201).json(event);
  } catch (error: any) {
    res.status(500).json({ message: 'Error creating event', error: error.message });
  }
};

export const getAllEvents = async (req: Request, res: Response) => {
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
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching events', error: error.message });
  }
};

export const getEventById = async (req: Request, res: Response) => {
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
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching event', error: error.message });
  }
};

export const updateEvent = async (req: Request, res: Response) => {
  try {
    const eventId = req.params['id'];
    if (typeof eventId !== 'string') {
      return res.status(400).json({ message: 'Invalid Event ID' });
    }
    const { 
      title, description, date, location, type, 
      imageUrl, registrationLink, prize,
      coordinatorName, coordinatorPhone, hostInstitutionId
    } = req.body;

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
  } catch (error: any) {
    res.status(500).json({ message: 'Error updating event', error: error.message });
  }
};

export const deleteEvent = async (req: Request, res: Response) => {
  try {
    const eventId = req.params['id'];
    if (typeof eventId !== 'string') {
      return res.status(400).json({ message: 'Invalid Event ID' });
    }
    await prisma.event.delete({
      where: { id: eventId }
    });
    res.status(200).json({ message: 'Event deleted successfully' });
  } catch (error: any) {
    res.status(500).json({ message: 'Error deleting event', error: error.message });
  }
};

export const saveFormSchema = async (req: Request, res: Response) => {
  try {
    const eventId = req.params['id'];
    const { formSchema } = req.body;
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    if (typeof eventId !== 'string') {
      return res.status(400).json({ message: 'Invalid Event ID' });
    }

    const event = await prisma.event.findUnique({ where: { id: eventId } });
    if (!event) {
      return res.status(404).json({ message: 'Event not found' });
    }

    if (event.ownerId !== requesterId) {
      return res.status(403).json({ message: 'Unauthorized: Only the event creator can set the form schema.' });
    }

    const updatedEvent = await prisma.event.update({
      where: { id: eventId },
      data: { formSchema }
    });

    res.status(200).json(updatedEvent);
  } catch (error: any) {
    res.status(500).json({ message: 'Error saving form schema', error: error.message });
  }
};

export const downloadRegistrations = async (req: Request, res: Response) => {
  try {
    const eventId = req.params['id'];
    const requesterId = (req as any).user?.id || (req as any).user?.userId;

    if (typeof eventId !== 'string') {
      return res.status(400).json({ message: 'Invalid Event ID' });
    }

    const event = await prisma.event.findUnique({ where: { id: eventId } });
    if (!event) {
      return res.status(404).json({ message: 'Event not found' });
    }

    if (event.ownerId !== requesterId) {
      return res.status(403).json({ message: 'Unauthorized: Only the event creator can download registrations.' });
    }

    const registrations = await prisma.eventRegistration.findMany({
      where: { eventId },
      include: {
        user: {
          select: {
            fullName: true,
            email: true,
            rollNo: true,
            college: true,
            department: true
          }
        }
      }
    });

    // Generate CSV
    const headers = ['Full Name', 'Email', 'Roll No', 'College', 'Department', 'Status', 'Form Data'];
    const rows = registrations.map(r => [
      r.user.fullName,
      r.user.email,
      r.user.rollNo || '',
      r.user.college || '',
      r.user.department || '',
      r.status,
      JSON.stringify(r.formData)
    ]);

    const csvContent = [headers.join(','), ...rows.map(row => row.map(cell => `"${cell}"`).join(','))].join('\n');

    res.setHeader('Content-Type', 'text/csv');
    res.setHeader('Content-Disposition', `attachment; filename=registrations_event_${eventId}.csv`);
    res.status(200).send(csvContent);
  } catch (error: any) {
    res.status(500).json({ message: 'Error downloading registrations', error: error.message });
  }
};
