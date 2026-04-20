import type { Request, Response } from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import prisma from '../utils/prisma.js';

export const register = async (req: Request, res: Response) => {
  try {
    const { email, password, fullName, role, studentLevel, department, college, year, section, inviteCode, phoneNumber, website, services } = req.body;

    // 0. Mandatory Field Validation
    if (!email || !password || !fullName || !role || !department) {
        return res.status(400).json({ message: 'Missing mandatory fields: Name, Email, Password, Role, and Department are required.' });
    }

    // 1. Role Security Check
    const protectedRoles = ['ADMIN', 'SPOC', 'MENTOR', 'INVESTOR'];
    if (protectedRoles.includes(role)) {
      // PERMANENT FIX: Master Codes for development and easy initialization
      const masterCodes: Record<string, string> = {
          'ADMIN': 'ASG_ADMIN_2025',
          'SPOC': 'ASG_SPOC_2025'
      };

      // Check if it's the master code first
      if (inviteCode && inviteCode === masterCodes[role]) {
          // Master code used correctly, proceed
      } else {
          // If no master code, check database
          // SPECIAL CASE: If it's the FIRST user ever, allow ADMIN registration freely
          const userCount = await prisma.user.count();
          if (userCount === 0 && role === 'ADMIN') {
              // Allow first admin
          } else {
              if (!inviteCode) {
                return res.status(403).json({ message: `Invite code required for registration as ${role}` });
              }

              const invite = await prisma.inviteCode.findFirst({
                where: {
                  code: inviteCode,
                  targetRole: role as any,
                  isUsed: false,
                  expiresAt: { gt: new Date() }
                }
              });

              if (!invite) {
                return res.status(403).json({ message: 'Invalid or expired invite code' });
              }

              // Mark code as used
              await prisma.inviteCode.update({
                where: { id: invite.id },
                data: { isUsed: true }
              });
          }
      }
    }

    // 2. Check if user already exists
    const existingUser = await prisma.user.findUnique({ where: { email } });
    if (existingUser) {
      return res.status(400).json({ message: 'User already exists' });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 12);

    // Create user
    const user = await prisma.user.create({
      data: {
        email,
        password: hashedPassword,
        fullName,
        role,
        studentLevel,
        department,
        college,
        year,
        section,
        phoneNumber,
        website,
        services: services || [],
        isVerified: true // Auto-verify for immediate discovery
      }
    });

    // Generate token
    const token = jwt.sign(
      { userId: user.id, role: user.role },
      process.env.JWT_SECRET || 'secret_key',
      { expiresIn: '7d' }
    );

    res.status(201).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } });
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
      { userId: user.id, role: user.role },
      process.env.JWT_SECRET || 'secret_key',
      { expiresIn: '7d' }
    );

    res.status(200).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } });
  } catch (error: any) {
    console.error('Login Error:', error);
    if (error.message.includes('timed out')) {
      return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
    }
    res.status(500).json({ message: 'Login failed', error: error.message });
  }
};
