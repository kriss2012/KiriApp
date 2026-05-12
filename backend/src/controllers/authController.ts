import type { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import prisma from '../utils/prisma.js';

export const register = async (req: Request, res: Response) => {
  try {
    const {
      email,
      password,
      fullName,
      userCategory,
      phoneNumber,
      role,
      department,
      college,
      year,
      section,
      website,
      githubUrl,
      linkedInUrl,
      portfolioUrl,
      rollNo,
      achievements,
      services,
      bio
    } = req.body;

    // 0. Mandatory Field Validation
    if (!email || !password || !fullName) {
        return res.status(400).json({ message: 'Missing mandatory fields: Name, Email, and Password are required.' });
    }

    // 1. Check if user already exists
    const existingUser = await prisma.user.findUnique({ where: { email } });
    if (existingUser) {
      return res.status(400).json({ message: 'User already exists' });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 12);

    // Map role string to UserCategory enum if applicable
    const categoryMapping: Record<string, any> = {
      'STUDENT': 'STUDENT',
      'FOUNDER': 'NON_STUDENT',
      'MENTOR': 'NON_STUDENT',
      'SPOC': 'FACULTY',
      'ALUMNI': 'ALUMNI'
    };

    const createData: any = {
      email,
      password: hashedPassword,
      fullName,
      userCategory: userCategory || (role ? categoryMapping[role] : 'STUDENT'),
      phoneNumber,
      department,
      college,
      year,
      section,
      website,
      githubUrl,
      linkedInUrl,
      portfolioUrl,
      rollNo,
      achievements,
      services: services || [],
      bio
    };

    // Only create StakeholderRole if it's a valid StakeholderType (FOUNDER, MENTOR, etc.)
    const validStakeholderTypes = ['FOUNDER', 'SERVICE_PROVIDER', 'MENTOR', 'INVESTOR', 'INCUBATOR', 'GUEST'];
    if (role && validStakeholderTypes.includes(role)) {
      createData.stakeholderRoles = {
        create: {
          roleName: role
        }
      };
    }

    // Create user
    const user = await prisma.user.create({
      data: createData,
      include: {
        stakeholderRoles: true
      }
    });

    // Generate token
    const token = jwt.sign(
      { userId: user.id },
      process.env.JWT_SECRET || 'secret_key',
      { expiresIn: '30d' }
    );

    res.status(201).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, userCategory: user.userCategory } });
  } catch (error: any) {
    console.error('Registration Error:', error);
    if (error.message.includes('timed out')) {
      return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
    }
    res.status(500).json({ message: 'Registration failed', error: error.message });
  }
};

export const login = async (req: Request, res: Response) => {
  try {
    const { email, password } = req.body;

    // Find user
    const user = await prisma.user.findUnique({ where: { email } });
    if (!user) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }

    // Check password
    const isPasswordCorrect = await bcrypt.compare(password, user.password);
    if (!isPasswordCorrect) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }

    // Generate token
    const token = jwt.sign(
      { userId: user.id },
      process.env.JWT_SECRET || 'secret_key',
      { expiresIn: '30d' }
    );

    res.status(200).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, userCategory: user.userCategory } });
  } catch (error: any) {
    console.error('Login Error:', error);
    if (error.message.includes('timed out')) {
      return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
    }
    res.status(500).json({ message: 'Login failed', error: error.message });
  }
};
