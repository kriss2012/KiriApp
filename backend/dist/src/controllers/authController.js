import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import prisma from '../utils/prisma.js';
export const register = async (req, res) => {
    try {
        const { email, password, fullName, role, studentLevel, department, college, year, section, inviteCode } = req.body;
        // 1. Role Security Check
        const protectedRoles = ['ADMIN', 'SPOC', 'MENTOR', 'INVESTOR'];
        if (protectedRoles.includes(role)) {
            if (!inviteCode) {
                return res.status(403).json({ message: `Invite code required for registration as ${role}` });
            }
            const invite = await prisma.inviteCode.findFirst({
                where: {
                    code: inviteCode,
                    targetRole: role,
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
                isVerified: true // Auto-verify for immediate discovery
            }
        });
        // Generate token
        const token = jwt.sign({ userId: user.id, role: user.role }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '7d' });
        res.status(201).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } });
    }
    catch (error) {
        console.error('Registration Error:', error);
        if (error.message.includes('timed out')) {
            return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
        }
        res.status(500).json({ message: 'Registration failed', error: error.message });
    }
};
export const login = async (req, res) => {
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
        const token = jwt.sign({ userId: user.id, role: user.role }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '7d' });
        res.status(200).json({ token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } });
    }
    catch (error) {
        console.error('Login Error:', error);
        if (error.message.includes('timed out')) {
            return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
        }
        res.status(500).json({ message: 'Login failed', error: error.message });
    }
};
//# sourceMappingURL=authController.js.map