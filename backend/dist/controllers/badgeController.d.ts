import type { Request, Response } from 'express';
export declare const getMyBadges: (req: Request, res: Response) => Promise<void>;
export declare const getUserBadges: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const awardBadge: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=badgeController.d.ts.map