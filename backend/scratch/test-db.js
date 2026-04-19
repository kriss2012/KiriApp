import pg from 'pg';
import dotenv from 'dotenv';
dotenv.config();

const connectionString = process.env.DATABASE_URL;

if (!connectionString) {
  console.error('ERROR: DATABASE_URL is not defined in .env');
  process.exit(1);
}

console.log('Testing connection to:', connectionString.split('@')[1]); // Log host only for security

const pool = new pg.Pool({
  connectionString,
  connectionTimeoutMillis: 10000,
});

async function testConnection() {
  const start = Date.now();
  try {
    console.log('Attempting to connect...');
    const client = await pool.connect();
    console.log(`Connected successfully in ${Date.now() - start}ms!`);
    
    const res = await client.query('SELECT current_database(), current_user, version();');
    console.log('Database Info:', res.rows[0]);
    
    client.release();
    console.log('Pool client released.');
  } catch (err) {
    console.error('!!! CONNECTION FAILED !!!');
    console.error('Error Code:', err.code);
    console.error('Error Message:', err.message);
    console.error('\nPOSSIBLE CAUSES:');
    console.error('1. AWS RDS Security Group is blocking your IP.');
    console.error('2. DATABASE_URL has a typo (incorrect password or host).');
    console.error('3. The database instance is stopped.');
  } finally {
    await pool.end();
    process.exit(0);
  }
}

testConnection();
