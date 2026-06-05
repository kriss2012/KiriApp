import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import dotenv from 'dotenv';
import { createServer } from 'http';
import { Server } from 'socket.io';
import authRoutes from './routes/authRoutes.js';
import userRoutes from './routes/userRoutes.js';
import jobRoutes from './routes/jobRoutes.js';
import chatRoutes from './routes/chatRoutes.js';
import eventRoutes from './routes/eventRoutes.js';
import aiRoutes from './routes/aiRoutes.js';
import notificationRoutes from './routes/notificationRoutes.js';
import connectionRoutes from './routes/connectionRoutes.js';
import pitchRoutes from './routes/pitchRoutes.js';
import matchRoutes from './routes/matchRoutes.js';
import investorRoutes from './routes/investorRoutes.js';
import mentorRoutes from './routes/mentorRoutes.js';
import inviteRoutes from './routes/inviteRoutes.js';
import aalRoutes from './routes/aalRoutes.js';
import boardRoutes from './routes/boardRoutes.js';

import { initSocket } from './utils/socket.js';
import { apiLimiter, authLimiter } from './middleware/rateLimiter.js';
import { idempotencyMiddleware } from './middleware/idempotency.js';

dotenv.config();

const app = express();

// Trust proxy for Render/Cloudflare/etc to let express-rate-limit see real IPs
app.set('trust proxy', 1);

const httpServer = createServer(app);
initSocket(httpServer);

// Middleware
app.use(cors());
app.use(helmet());
app.use(morgan('dev'));
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));

// Global Rate Limiter (Security Guard Principle)
app.use('/api', apiLimiter);

// Global Idempotency Guard (Multi-Tap protection for write/update operations)
app.use('/api', (req, res, next) => {
  if (['POST', 'PUT', 'DELETE'].includes(req.method)) {
    return idempotencyMiddleware(req, res, next);
  }
  next();
});

// Routes
app.use('/api/auth', authLimiter, authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/jobs', jobRoutes);
app.use('/api/chat', chatRoutes);
app.use('/api/events', eventRoutes);
app.use('/api/ai', aiRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/connections', connectionRoutes);
app.use('/api/pitch', pitchRoutes);
app.use('/api/match', matchRoutes);
app.use('/api/investor', investorRoutes);
app.use('/api/mentor', mentorRoutes);
app.use('/api/invite', inviteRoutes);
app.use('/api/aal', aalRoutes);
app.use('/api/board', boardRoutes);

app.get('/', (req, res) => {
  res.send('ASG Community API is running...');
});

app.get('/api', (req, res) => {
  res.send('ASG Community API is running...');
});

const PORT = process.env.PORT || 5000;

httpServer.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
