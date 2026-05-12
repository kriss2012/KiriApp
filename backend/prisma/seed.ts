import 'dotenv/config';
import { UserCategory, StakeholderType, RepoCategory, PostType, InputFormat, InputContext, MatchStatus } from '@prisma/client';
import prisma from '../src/utils/prisma.ts';

async function main() {
  console.log('Cleaning up database...');
  
  // Delete in reverse order of relations
  await prisma.aiResourceMatch.deleteMany({});
  await prisma.liveInput.deleteMany({});
  await prisma.job.deleteMany({});
  await prisma.ecosystemBoard.deleteMany({});
  await prisma.eventRegistration.deleteMany({});
  await prisma.event.deleteMany({});
  await prisma.aalActivity.deleteMany({});
  await prisma.aalOnboarding.deleteMany({});
  await prisma.userRepositoryMapping.deleteMany({});
  await prisma.stakeholderRole.deleteMany({});
  await prisma.institutionalCommittee.deleteMany({});
  
  // Add missing cleanups for relations
  await prisma.message.deleteMany({});
  await prisma.notification.deleteMany({});
  await prisma.aiMessage.deleteMany({});
  await prisma.backer.deleteMany({});
  await prisma.pitch.deleteMany({});
  await prisma.connection.deleteMany({});
  await prisma.conversation.deleteMany({});
  
  await prisma.user.deleteMany({});
  await prisma.institution.deleteMany({});

  console.log('Seeding database...');

  // 1. Create Institution
  const institution = await prisma.institution.create({
    data: {
      name: 'Apex Institute of Technology',
    },
  });

  // 2. Create Users
  const founder = await prisma.user.create({
    data: {
      email: 'founder@example.com',
      password: 'password_hash_here',
      fullName: 'Alice Founder',
      userCategory: UserCategory.NON_STUDENT,
      phoneNumber: '1234567890',
      bio: 'Building the future of AI.',
      stakeholderRoles: {
        create: {
          roleName: StakeholderType.FOUNDER,
        },
      },
    },
  });

  const student = await prisma.user.create({
    data: {
      email: 'student@example.com',
      password: 'password_hash_here',
      fullName: 'Bob Student',
      userCategory: UserCategory.STUDENT,
      phoneNumber: '0987654321',
      college: 'Apex Institute of Technology',
      year: '3rd Year',
      department: 'Computer Science',
      rollNo: 'CS101',
      repositories: {
        create: {
          institutionId: institution.id,
          repoCategory: RepoCategory.R1,
          approvalStatus: 'APPROVED',
        },
      },
    },
  });

  const faculty = await prisma.user.create({
    data: {
      email: 'faculty@example.com',
      password: 'password_hash_here',
      fullName: 'Dr. Smith',
      userCategory: UserCategory.FACULTY,
      phoneNumber: '5555555555',
      department: 'Computer Science',
      committees: {
        create: {
          institutionId: institution.id,
          department: 'Computer Science',
          roleType: 'FACULTY_REP',
        },
      },
    },
  });

  // Update Institution with SPOC
  await prisma.institution.update({
    where: { id: institution.id },
    data: { spocUserId: faculty.id },
  });

  // 3. AAL Onboarding & Activities
  const aalOnboarding = await prisma.aalOnboarding.create({
    data: {
      userId: student.id,
      mindsetScore: { score: 85, grit: 90, vision: 80 },
      lmsStatus: 'ENROLLED',
      interviewStatus: 'PASSED',
    },
  });

  await prisma.aalActivity.createMany({
    data: [
      { userId: student.id, activityNumber: 1, submissionUrl: 'http://example.com/sub1', status: 'VERIFIED' },
      { userId: student.id, activityNumber: 2, submissionUrl: 'http://example.com/sub2', status: 'VERIFIED' },
      { userId: student.id, activityNumber: 3, submissionUrl: 'http://example.com/sub3', status: 'SUBMITTED' },
    ],
  });

  // 4. Events
  const event = await prisma.event.create({
    data: {
      title: 'National AI Hackathon',
      description: 'A 24-hour hackathon to build AI solutions.',
      date: new Date('2026-06-01'),
      location: 'Main Auditorium',
      type: 'HACKATHON',
      ownerId: faculty.id,
      hostInstitutionId: institution.id,
    },
  });

  await prisma.eventRegistration.create({
    data: {
      eventId: event.id,
      userId: student.id,
      status: 'REGISTERED',
      formData: { teamName: 'Neural Builders', projectIdea: 'AI for Good' },
    },
  });

  // 5. Ecosystem Board & Jobs
  await prisma.ecosystemBoard.create({
    data: {
      authorUserId: founder.id,
      postType: PostType.NEWS,
      title: 'Announcing our Seed Round!',
      description: 'We have raised $1M to build the next generation of AI tools.',
    },
  });

  await prisma.job.create({
    data: {
      title: 'AI Intern',
      description: 'Looking for a passionate AI intern to join our team.',
      posterId: founder.id,
      type: 'INTERNSHIP',
      status: 'OPEN',
    },
  });

  // 6. AI Core (Live Inputs & Matches)
  const liveInput = await prisma.liveInput.create({
    data: {
      userId: student.id,
      formatType: InputFormat.TEXT,
      contentUrl: 'I need help with prompt engineering for my project.',
      context: InputContext.STUCK,
      aiProcessed: true,
    },
  });

  await prisma.aiResourceMatch.create({
    data: {
      sourceUserId: student.id,
      targetUserId: founder.id,
      matchReason: 'Bob is stuck on prompt engineering, and Alice is an expert in AI.',
      status: MatchStatus.SUGGESTED,
    },
  });

  console.log('Seeding completed successfully!');
}

main()
  .catch((e) => {
    console.error('Error seeding database:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
