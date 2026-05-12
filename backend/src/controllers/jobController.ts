import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const createJob = async (req: Request, res: Response) => {
  try {
    const { title, description, location, type, posterId } = req.body;
    const job = await prisma.job.create({
      data: { title, description, location, type, posterId }
    });
    res.status(201).json(job);
  } catch (error: any) {
    res.status(500).json({ message: 'Error creating job', error: error.message });
  }
};

export const getAllJobs = async (req: Request, res: Response) => {
  try {
    const jobs = await prisma.job.findMany({
      include: { poster: { select: { fullName: true, userCategory: true } } },
      orderBy: { createdAt: 'desc' }
    });
    res.status(200).json(jobs);
  } catch (error: any) {
    res.status(500).json({ message: 'Error fetching jobs', error: error.message });
  }
};
