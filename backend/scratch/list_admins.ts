import prisma from '../src/utils/prisma.js';

async function listAdmins() {
  try {
    const admins = await prisma.user.findMany({
      where: { role: 'ADMIN' },
      select: { email: true, fullName: true, userCategory: true }
    });
    console.log('Admin Users:', JSON.stringify(admins, null, 2));
  } catch (error) {
    console.error('Error fetching admins:', error);
  } finally {
    await prisma.$disconnect();
  }
}

listAdmins();
