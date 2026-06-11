import jwt from 'jsonwebtoken';
const JWT_SECRET = (process.env.JWT_SECRET || 'secret_key');
export const authenticate = (req, res, next) => {
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
        const secret = process.env.JWT_SECRET || 'secret_key';
        const decoded = jwt.verify(token, secret);
        if (!decoded || !decoded.userId) {
            res.setHeader('WWW-Authenticate', 'Bearer error="invalid_token"');
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
        res.setHeader('WWW-Authenticate', 'Bearer error="invalid_token"');
        return res.status(401).json({ message: 'Authentication failed' });
    }
};
export const checkRole = (roles) => {
    return (req, res, next) => {
        const userRole = req.user?.role;
        if (!userRole || !roles.includes(userRole)) {
            return res.status(403).json({ message: 'Access denied: Insufficient permissions' });
        }
        next();
    };
};
//# sourceMappingURL=auth.js.map