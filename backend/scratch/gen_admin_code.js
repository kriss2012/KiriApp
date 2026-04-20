import { PrismaClient } from '@prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import pg from 'pg';
import dotenv from 'dotenv';
import crypto from 'crypto';

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
    const code = crypto.randomBytes(4).toString('hex').toUpperCase();
    const expiresAt = new Date();
    expiresAt.setDate(expiresAt.getDate() + 30); // 30 days

    const invite = await prisma.inviteCode.create({
      data: {
        code,
        targetRole: 'ADMIN',
        expiresAt
      }
    });

    console.log('--- NEW ADMIN INVITE CODE ---');
    console.log('Code: ' + invite.code);
    console.log('Role: ' + invite.targetRole);
    console.log('Expires: ' + invite.expiresAt);
    console.log('-----------------------------');
    console.log('Use this code in the app registration screen to create your admin account.');
  } catch (e) {
    console.error('DB ERROR:', e.message);
  } finally {
    await prisma.$disconnect();
    await pool.end();
  }
}

run();
