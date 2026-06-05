import 'dotenv/config';
import { PrismaClient } from '@prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
declare const prisma: PrismaClient<{
    adapter: PrismaPg;
    log: ("error" | "warn")[];
}, "error" | "warn", import("@prisma/client/runtime/client").DefaultArgs>;
export default prisma;
//# sourceMappingURL=prisma.d.ts.map