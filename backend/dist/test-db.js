import prisma from './utils/prisma.js';
async function main() {
    console.log("Starting DB diagnostics...");
    try {
        const userCount = await prisma.user.count();
        console.log(`Success: User count is ${userCount}`);
    }
    catch (err) {
        console.error("Error querying User table:", err.message);
    }
    try {
        const convCount = await prisma.conversation.count();
        console.log(`Success: Conversation count is ${convCount}`);
    }
    catch (err) {
        console.error("Error querying Conversation table:", err.message);
    }
    try {
        const msgCount = await prisma.aiMessage.count();
        console.log(`Success: AiMessage count is ${msgCount}`);
    }
    catch (err) {
        console.error("Error querying AiMessage table:", err.message);
    }
}
main()
    .catch((e) => {
    console.error("Unhandled error during diagnostics:", e);
})
    .finally(async () => {
    await prisma.$disconnect();
});
//# sourceMappingURL=test-db.js.map