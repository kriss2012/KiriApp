import type { Request, Response } from 'express';
export declare const getProjects: (req: Request, res: Response) => Promise<void>;
export declare const submitProject: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const addProjectReview: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const toggleFeaturedProject: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=projectController.d.ts.map