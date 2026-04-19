import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';
import { createNotification } from './notificationController.js';
import { createActivity } from './aiController.js';

export const createEvent = async (req: Request, res: Response) => {
  try {
    const { title, description, date, location, type, ownerId } = req.body;

    // Check if the user has permission to create events
    const user = await prisma.user.findUnique({
      where: { id: ownerId }
    });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    if (!user.canCreateEvents && user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'You do not have permission to create events. Please contact an admin.' });
    }

    const event = await prisma.event.create({
      data: {
        title,
        description,
        date: new Date(date),
        location,
        type: type || 'GENERAL',
        ownerId
      }
    });

    // Notify all verified users about the new event
    const allUsers = await prisma.user.findMany({
      where: { isVerified: true },
      select: { id: true }
    });

    await Promise.all(
      allUsers.map(u => 
        createNotification(u.id, 'New Event Added', `Check out "${title}" happening at ${location}.`, 'EVENT')
      )
    );

    // Record Innovation Activity for NAAC
    await createActivity(ownerId, 'EVENT_JOIN', 'Organized Event', `Organized "${title}" on ${date}.`, 50);

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
    const { title, description, date, location, type } = req.body;

    const event = await prisma.event.update({
      where: { id: eventId },
      data: {
        title,
        description,
        ...(date ? { date: new Date(date) } : {}),
        location,
        type
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
