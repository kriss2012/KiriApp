import type { Request, Response } from 'express';
export declare const sendMessage: (req: Request, res: Response) => Promise<void>;
export declare const getChatHistory: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getConversations: (req: Request, res: Response) => Promise<void>;
export declare const searchUsers: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=chatController.d.ts.map