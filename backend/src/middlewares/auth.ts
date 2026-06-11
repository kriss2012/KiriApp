import type { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import type { Secret } from 'jsonwebtoken';

const JWT_SECRET: Secret = (process.env.JWT_SECRET || 'secret_key') as string;

export const authenticate = (req: Request, res: Response, next: NextFunction) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      res.setHeader('WWW-Authenticate', 'Bearer');
      return res.status(401).json({ message: 'Authentication required' });
    }

    const token = authHeader.split(' ')[1];
    if (!token) {
      res.setHeader('WWW-Authenticate', 'Bearer error="invalid_token"');
      return res.status(401).json({ message: 'No token provided' });
    }

    const secret: string = process.env.JWT_SECRET || 'secret_key';
    const decoded = jwt.verify(token, secret) as any;

    if (!decoded || !decoded.userId) {
      res.setHeader('WWW-Authenticate', 'Bearer error="invalid_token"');
      return res.status(401).json({ message: 'Invalid token' });
    }

    // Attach user to request
    (req as any).user = {
      id: decoded.userId,
      role: decoded.role
    };

    next();
  } catch (error) {
    console.error('Auth Middleware Error:', error);
    res.setHeader('WWW-Authenticate', 'Bearer error="invalid_token"');
    return res.status(401).json({ message: 'Authentication failed' });
  }
};

export const checkRole = (roles: string[]) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const userRole = (req as any).user?.role;
    if (!userRole || !roles.includes(userRole)) {
      return res.status(403).json({ message: 'Access denied: Insufficient permissions' });
    }
    next();
  };
};
