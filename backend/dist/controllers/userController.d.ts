import type { Request, Response } from 'express';
export declare const getProfile: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const updateProfile: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getAllVerifiedUsers: (req: Request, res: Response) => Promise<void>;
export declare const searchUsers: (req: Request, res: Response) => Promise<void>;
export declare const getActivities: (req: Request, res: Response) => Promise<void>;
export declare const getUserStats: (req: Request, res: Response) => Promise<void>;
export declare const verifyActivity: (req: Request, res: Response) => Promise<void>;
//# sourceMappingURL=userController.d.ts.map