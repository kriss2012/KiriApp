import type { Request, Response } from 'express';
import prisma from '../utils/prisma.js';

export const getProjects = async (req: Request, res: Response) => {
  try {
    const { studentId, featured } = req.query;

    const where: any = {};
    if (studentId) {
      where.studentId = studentId as string;
    }
    if (featured === 'true') {
      where.isFeatured = true;
    }

    // Seed dummy projects with mock peer reviews if database projects count is 0
    const count = await prisma.projectShowcase.count();
    if (count === 0) {
      // Find a user or use current user
      const users = await prisma.user.findMany({ take: 2 });
      if (users.length > 0 && users[0]) {
        const student1 = users[0].id;
        const reviewerId = users[1]?.id || users[0].id;

        const p1 = await prisma.projectShowcase.create({
          data: {
            studentId: student1,
            title: "AI Resume Matcher",
            description: "An automated service matching resumes against real JDs using BERT embeddings.",
            githubUrl: "https://github.com/kiriapp/ai-resume-matcher",
            techStack: ["Kotlin", "Python", "FastAPI", "PostgreSQL"],
            isFeatured: true
          }
        });

        await prisma.projectReview.create({
          data: {
            projectId: p1.id,
            reviewerId,
            codeQuality: 5,
            documentation: 4,
            architecture: 4,
            innovation: 5,
            comment: "Exceptional architecture and very clean codebase! Adding a Swagger doc would make it perfect."
          }
        });

        if (users.length > 1 && users[1]) {
          const student2 = users[1].id;
          const p2 = await prisma.projectShowcase.create({
            data: {
              studentId: student2,
              title: "EmployAI Mobile Client",
              description: "Native Jetpack Compose client for dashboard metrics and session coordination.",
              githubUrl: "https://github.com/kiriapp/employai-android",
              techStack: ["Kotlin", "Jetpack Compose", "Hilt", "Retrofit"],
              isFeatured: true
            }
          });

          await prisma.projectReview.create({
            data: {
              projectId: p2.id,
              reviewerId: student1,
              codeQuality: 4,
              documentation: 5,
              architecture: 5,
              innovation: 4,
              comment: "Very impressive UI component design. The state management is highly responsive."
            }
          });
        }
      }
    }

    const projects = await prisma.projectShowcase.findMany({
      where,
      include: {
        student: {
          select: {
            id: true,
            fullName: true,
            email: true,
            avatarUrl: true
          }
        },
        reviews: {
          include: {
            reviewer: {
              select: {
                id: true,
                fullName: true,
                avatarUrl: true
              }
            }
          },
          orderBy: { createdAt: 'desc' }
        }
      },
      orderBy: { createdAt: 'desc' }
    });

    res.status(200).json({ success: true, projects });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const submitProject = async (req: Request, res: Response) => {
  try {
    const studentId = (req as any).user.id;
    const { title, description, githubUrl, techStack } = req.body;

    if (!title || !description || !githubUrl) {
      return res.status(400).json({ success: false, error: 'title, description, and githubUrl are required' });
    }

    const project = await prisma.projectShowcase.create({
      data: {
        studentId,
        title,
        description,
        githubUrl,
        techStack: Array.isArray(techStack) ? techStack : []
      }
    });

    res.status(201).json({ success: true, project });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const addProjectReview = async (req: Request, res: Response) => {
  try {
    const reviewerId = (req as any).user.id;
    const { projectId } = req.params;
    const { codeQuality, documentation, architecture, innovation, comment } = req.body;

    if (!projectId) {
      return res.status(400).json({ success: false, error: 'projectId parameter is required' });
    }

    if (codeQuality === undefined || documentation === undefined || architecture === undefined || innovation === undefined || !comment) {
      return res.status(400).json({ success: false, error: 'All rating metrics (1-5) and a review comment are required' });
    }

    // Check if project exists
    const project = await prisma.projectShowcase.findUnique({
      where: { id: projectId as string }
    });

    if (!project) {
      return res.status(404).json({ success: false, error: 'Project not found' });
    }

    const review = await prisma.projectReview.create({
      data: {
        projectId: projectId as string,
        reviewerId,
        codeQuality: Number(codeQuality),
        documentation: Number(documentation),
        architecture: Number(architecture),
        innovation: Number(innovation),
        comment
      }
    });

    res.status(201).json({ success: true, review });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const toggleFeaturedProject = async (req: Request, res: Response) => {
  try {
    const { projectId } = req.params;
    const { isFeatured } = req.body;

    if (!projectId || isFeatured === undefined) {
      return res.status(400).json({ success: false, error: 'projectId parameter and isFeatured body value are required' });
    }

    const updated = await prisma.projectShowcase.update({
      where: { id: projectId as string },
      data: { isFeatured: Boolean(isFeatured) }
    });

    res.status(200).json({ success: true, project: updated });
  } catch (error: any) {
    res.status(500).json({ success: false, error: error.message });
  }
};
