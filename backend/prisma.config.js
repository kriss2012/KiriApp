// Prisma config — load DATABASE_URL synchronously before defineConfig() runs
import { readFileSync, existsSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";
import { defineConfig } from "prisma/config";

// Manually parse .env for local dev (Render injects env vars directly)
const __dirname = dirname(fileURLToPath(import.meta.url));
const envPath = resolve(__dirname, ".env");
if (existsSync(envPath)) {
    const lines = readFileSync(envPath, "utf-8").split("\n");
    for (const line of lines) {
        const trimmed = line.trim();
        if (!trimmed || trimmed.startsWith("#")) continue;
        const eqIdx = trimmed.indexOf("=");
        if (eqIdx === -1) continue;
        const key = trimmed.slice(0, eqIdx).trim();
        const val = trimmed.slice(eqIdx + 1).trim().replace(/^["']|["']$/g, "");
        if (!process.env[key]) process.env[key] = val;
    }
}

const dbUrl = process.env["DATABASE_URL"];
if (!dbUrl) {
    throw new Error("DATABASE_URL is not set. Add it to Render Environment Variables or your local .env file.");
}

export default defineConfig({
    schema: "prisma/schema.prisma",
    migrations: {
        path: "prisma/migrations",
    },
    datasource: {
        url: dbUrl,
    },
});
//# sourceMappingURL=prisma.config.js.map