const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

async function test() {
    try {
        const user = await prisma.user.findFirst();
        if (!user) {
            console.log("No user found in DB. Please register first.");
            return;
        }

        console.log(`Found Test User: ${user.fullName} (${user.id})`);

        // 1. Test Profile Update
        console.log("\n--- Testing Profile Update ---");
        const updatedUser = await prisma.user.update({
            where: { id: user.id },
            data: {
                bio: "Test Bio Updated at " + new Date().toISOString(),
                skills: ["TypeScript", "Testing", "Offline-First"],
                phoneNumber: "1234567890"
            }
        });
        console.log("SUCCESS: Profile updated in DB.");
        console.log("New Bio:", updatedUser.bio);

        // 2. Test Add Event
        console.log("\n--- Testing Add Event ---");
        const event = await prisma.event.create({
            data: {
                title: "Test Local Event",
                description: "Verifying the definitive reliability logic locally.",
                date: new Date(),
                location: "Jalgaon Local Lab",
                ownerId: user.id,
                type: "GENERAL"
            }
        });
        console.log(`SUCCESS: Event '${event.title}' created in DB.`);
        console.log(`Event ID: ${event.id}`);

    } catch (e) {
        console.error("Test Failed:", e.message);
    } finally {
        await prisma.$disconnect();
    }
}

test();
