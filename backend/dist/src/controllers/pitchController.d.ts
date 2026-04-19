import type { Request, Response } from 'express';
export declare const createPitch: (req: Request, res: Response) => Promise<void>;
export declare const getAllPitches: (req: Request, res: Response) => Promise<void>;
export declare const backPitch: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=pitchController.d.ts.map