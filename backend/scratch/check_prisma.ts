import prisma from '../src/utils/prisma';

console.log("Prisma keys:", Object.keys(prisma).filter(k => !k.startsWith('_')));
if ('badge' in prisma) {
  console.log("badge model exists!");
} else {
  console.log("badge model DOES NOT exist!");
}

if ('mentorSession' in prisma) {
  console.log("mentorSession model exists!");
} else {
  console.log("mentorSession model DOES NOT exist!");
}
process.exit(0);
