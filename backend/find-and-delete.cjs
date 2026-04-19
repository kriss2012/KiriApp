const pg = require('pg');
const { Client } = pg;

const client = new Client({
    connectionString: "postgresql://postgres:ru5qB2LCkLCKlyggioQB@database-1.cv0a24au6afr.ap-south-1.rds.amazonaws.com:5432/postgres?sslmode=verify-full&sslrootcert=global-bundle.pem"
});

async function run() {
    await client.connect();
    console.log("Connected to database.");

    const res = await client.query('SELECT id, "fullName", email, role FROM "User" WHERE role = \'FOUNDER\'');
    console.log("Found Founders:", res.rows);

    const targetUser = res.rows.find(u => !u.fullName || u.fullName.trim() === "" || u.fullName.toLowerCase() === "founder");

    if (targetUser) {
        console.log("Deleting User:", targetUser);
        
        // Delete related data
        await client.query('DELETE FROM "Activity" WHERE "userId" = $1', [targetUser.id]);
        await client.query('DELETE FROM "Notification" WHERE "userId" = $1', [targetUser.id]);
        await client.query('DELETE FROM "Connection" WHERE "senderId" = $1 OR "receiverId" = $1', [targetUser.id]);
        
        // Final deletion
        await client.query('DELETE FROM "User" WHERE id = $1', [targetUser.id]);
        console.log("Deletion successful.");
    } else {
        console.log("No empty founder account found.");
    }

    await client.end();
}

run().catch(console.error);
