import { PrismaClient } from '@prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import pg from 'pg';
import dotenv from 'dotenv';

dotenv.config();

const pool = new pg.Pool({ 
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

const adapter = new PrismaPg(pool);
const prisma = new PrismaClient({ adapter });

async function run() {
  try {
    const admins = await prisma.user.findMany({
      where: { role: 'ADMIN' },
      select: { email: true, fullName: true }
    });
    console.log('--- ADMIN LIST ---');
    console.log(JSON.stringify(admins, null, 2));
    
    if (admins.length === 0) {
      console.log('No admins found. Checking for all users to see if any exist.');
      const allUsers = await prisma.user.findMany({
         take: 5,
         select: { email: true, role: true }
      });
      console.log('--- USER PREVIEW ---');
      console.log(JSON.stringify(allUsers, null, 2));
    }
  } catch (e) {
    console.error('DB ERROR:', e.message);
  } finally {
    await prisma.$disconnect();
    await pool.end();
  }
}

run();
