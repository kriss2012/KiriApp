import type { Request, Response } from 'express';
export declare const requestSession: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getSessions: (req: Request, res: Response) => Promise<void>;
export declare const updateSessionStatus: (req: Request, res: Response) => Promise<void>;
//# sourceMappingURL=mentorController.d.ts.map