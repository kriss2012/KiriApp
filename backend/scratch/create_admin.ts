import prisma from '../src/utils/prisma.js';
import bcrypt from 'bcryptjs';

async function createAdmin() {
  try {
    const email = 'admin@apex.com';
    const rawPassword = 'admin_password_2024';
    const hashedPassword = await bcrypt.hash(rawPassword, 12);

    const user = await prisma.user.upsert({
      where: { email },
      update: {
        password: hashedPassword,
        role: 'ADMIN',
        fullName: 'System Administrator',
        department: 'CORE',
        isVerified: true
      },
      create: {
        email,
        password: hashedPassword,
        fullName: 'System Administrator',
        role: 'ADMIN',
        department: 'CORE',
        isVerified: true
      }
    });

    console.log('Admin account ready:');
    console.log('Email: ' + email);
    console.log('Password: ' + rawPassword);
  } catch (error) {
    console.error('Failed to create/reset admin:', error.message);
  } finally {
    await prisma.$disconnect();
  }
}

createAdmin();
