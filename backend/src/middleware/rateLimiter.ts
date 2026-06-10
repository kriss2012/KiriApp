import rateLimit from 'express-rate-limit';

export const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 1000, // Increased from 100 to prevent false-positive timeouts on high-sync client dashboards
  message: {
    message: 'Too many requests from this IP, please try again after 15 minutes'
  },
  standardHeaders: true, // Return rate limit info in the `RateLimit-*` headers
  legacyHeaders: false, // Disable the `X-RateLimit-*` headers
});

// Stepped limit for auth routes to prevent brute-force (The "Counter Security" principle)
export const authLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 50, // Increased from 10 to allow seamless testing and multi-device auth demos
  message: {
    message: 'Too many login/register attempts, please try again after an hour'
  },
  standardHeaders: true,
  legacyHeaders: false,
});
