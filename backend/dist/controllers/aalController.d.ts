import type { Request, Response } from 'express';
/**
 * Controller for APEX AI Launchpad (AAL) activities and onboarding.
 */
export declare const getOnboarding: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const updateMindsetScore: (req: Request, res: Response) => Promise<void>;
export declare const submitActivity: (req: Request, res: Response) => Promise<void>;
export declare const getActivities: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const verifyActivity: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getInstitutions: (req: Request, res: Response) => Promise<void>;
export declare const registerForEvent: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const submitLiveInput: (req: Request, res: Response) => Promise<void>;
export declare const getAiMatches: (req: Request, res: Response) => Promise<void>;
//# sourceMappingURL=aalController.d.ts.map