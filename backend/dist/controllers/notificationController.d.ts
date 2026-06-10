import type { Request, Response } from 'express';
export declare const getNotifications: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const markAsRead: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const markAllAsRead: (req: Request, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const createNotification: (userId: string, title: string, content: string, type: string, relatedId?: string | null) => Promise<{
    id: string;
    createdAt: Date;
    userId: string;
    title: string;
    content: string;
    type: string;
    relatedId: string | null;
    isRead: boolean;
} | undefined>;
//# sourceMappingURL=notificationController.d.ts.map