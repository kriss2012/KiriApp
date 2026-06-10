import type { Request, Response } from 'express';
export declare const getAiHistory: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const chatWithKiri: (req: Request, res: Response) => Promise<void>;
export declare const updateSpecialization: (req: Request, res: Response) => Promise<void>;
export declare const createActivity: (userId: string, type: string, title: string, content: string, points: number) => Promise<void>;
export declare const generateResume: (req: Request, res: Response) => Promise<void>;
export declare const mockInterview: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=aiController.d.ts.map