import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

async function main() {
    console.log("Searching for empty users...");
    const emptyUsers = await prisma.user.findMany({
        where: {
            OR: [
                { fullName: "" },
                { fullName: "founder" },
                { fullName: { equals: null } }
            ]
        }
    });

    console.log(`Found ${emptyUsers.length} empty users.`);
    for (const user of emptyUsers) {
        console.log(`Deleting user: ${user.fullName} (${user.email})`);
        // Delete related data first to avoid foreign key constraints
        await prisma.activity.deleteMany({ where: { userId: user.id } });
        await prisma.notification.deleteMany({ where: { userId: user.id } });
        await prisma.connection.deleteMany({
            where: {
                OR: [{ senderId: user.id }, { receiverId: user.id }]
            }
        });
        await prisma.user.delete({ where: { id: user.id } });
    }
    console.log("Cleanup complete.");
}

main().catch(e => {
    console.error(e);
    process.exit(1);
}).finally(async () => {
    await prisma.$disconnect();
});
