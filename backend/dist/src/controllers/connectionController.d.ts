import type { Request, Response } from 'express';
export declare const sendRequest: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const acceptRequest: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getUserConnections: (req: Request, res: Response) => Promise<void>;
//# sourceMappingURL=connectionController.d.ts.map