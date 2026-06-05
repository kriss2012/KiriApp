// Prisma config — load DATABASE_URL synchronously before defineConfig() runs
import { readFileSync, existsSync } from "fs";
import { resolve, dirname } from "path";
import { fileURLToPath } from "url";
import { defineConfig } from "prisma/config";

// Manually parse .env for local dev (Render injects env vars directly)
const __dirnamePath = dirname(fileURLToPath(import.meta.url));
const envPath = resolve(__dirnamePath, ".env");
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

const cmd = process.argv.join(" ");
const isBuildTimeCmd = cmd.includes("generate") || cmd.includes("validate") || cmd.includes("format");

const dbUrl = process.env["DATABASE_URL"] ||
  (isBuildTimeCmd ? "postgresql://build:build@localhost:5432/build" : null);

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
