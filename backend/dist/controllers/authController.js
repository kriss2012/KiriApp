import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import prisma from '../utils/prisma.js';
export const register = async (req, res) => {
    try {
        const { email, password, fullName, userCategory, phoneNumber, role, department, college, year, section, website, githubUrl, linkedInUrl, portfolioUrl, rollNo, achievements, services, bio } = req.body;
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
        const categoryMapping = {
            'STUDENT': 'STUDENT',
            'FOUNDER': 'NON_STUDENT',
            'MENTOR': 'NON_STUDENT',
            'SPOC': 'FACULTY',
            'ALUMNI': 'ALUMNI'
        };
        const createData = {
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
        // Generate tokens
        const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '1d' } // Short-lived access token
        );
        const refreshToken = jwt.sign({ userId: user.id }, process.env.REFRESH_SECRET || 'refresh_secret_key', { expiresIn: '30d' } // Long-lived refresh token
        );
        // Save refresh token to user
        await prisma.user.update({
            where: { id: user.id },
            data: { refreshToken }
        });
        res.status(201).json({
            token,
            refreshToken,
            user: {
                id: user.id,
                email: user.email,
                fullName: user.fullName,
                userCategory: user.userCategory,
                role: role // Return the role sent during registration
            }
        });
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
        const user = await prisma.user.findUnique({
            where: { email },
            include: { stakeholderRoles: true }
        });
        if (!user) {
            return res.status(400).json({ message: 'Invalid credentials' });
        }
        // Check password
        const isPasswordCorrect = await bcrypt.compare(password, user.password);
        if (!isPasswordCorrect) {
            return res.status(400).json({ message: 'Invalid credentials' });
        }
        // Generate tokens
        const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '1d' });
        const refreshToken = jwt.sign({ userId: user.id }, process.env.REFRESH_SECRET || 'refresh_secret_key', { expiresIn: '30d' });
        // Update refresh token in DB
        await prisma.user.update({
            where: { id: user.id },
            data: { refreshToken }
        });
        // Derive role: prefer StakeholderRole, fallback to UserCategory
        const firstRole = user.stakeholderRoles[0];
        const role = firstRole ? firstRole.roleName : user.userCategory;
        res.status(200).json({
            token,
            refreshToken,
            user: {
                id: user.id,
                email: user.email,
                fullName: user.fullName,
                userCategory: user.userCategory,
                role: role
            }
        });
    }
    catch (error) {
        console.error('Login Error:', error);
        if (error.message.includes('timed out')) {
            return res.status(503).json({ message: 'Database connection timed out. Please check RDS Security Groups.', error: error.message });
        }
        res.status(500).json({ message: 'Login failed', error: error.message });
    }
};
export const refresh = async (req, res) => {
    try {
        const { refreshToken } = req.body;
        if (!refreshToken) {
            return res.status(401).json({ message: 'Refresh token required' });
        }
        const secret = process.env.REFRESH_SECRET || 'refresh_secret_key';
        const decoded = jwt.verify(refreshToken, secret);
        const user = await prisma.user.findUnique({
            where: { id: decoded.userId }
        });
        if (!user || user.refreshToken !== refreshToken) {
            return res.status(401).json({ message: 'Invalid refresh token' });
        }
        const newToken = jwt.sign({ userId: user.id }, process.env.JWT_SECRET || 'secret_key', { expiresIn: '1d' });
        res.json({ token: newToken });
    }
    catch (error) {
        res.status(401).json({ message: 'Session expired. Please log in again.' });
    }
};
//# sourceMappingURL=authController.js.map