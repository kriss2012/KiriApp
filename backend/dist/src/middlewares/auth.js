import jwt from 'jsonwebtoken';
const JWT_SECRET = (process.env.JWT_SECRET || 'secret_key');
export const authenticate = (req, res, next) => {
    try {
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ message: 'Authentication required' });
        }
        const token = authHeader.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'No token provided' });
        }
        const secret = process.env.JWT_SECRET || 'secret_key';
        const decoded = jwt.verify(token, secret);
        if (!decoded || !decoded.userId) {
            return res.status(401).json({ message: 'Invalid token' });
        }
        // Attach user to request
        req.user = {
            id: decoded.userId,
            role: decoded.role
        };
        next();
    }
    catch (error) {
        console.error('Auth Middleware Error:', error);
        return res.status(401).json({ message: 'Authentication failed' });
    }
};
//# sourceMappingURL=auth.js.map