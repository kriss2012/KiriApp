// Lightweight in-memory Cache with automatic TTL eviction.
// Avoids external package installation overhead on Render container builds.
class MemoryCache {
    cache = new Map();
    constructor() {
        // Clean expired entries every 30 minutes
        setInterval(() => this.clean(), 30 * 60 * 1000);
    }
    get(key) {
        const entry = this.cache.get(key);
        if (!entry)
            return null;
        if (Date.now() > entry.expiry) {
            this.cache.delete(key);
            return null;
        }
        return entry.value;
    }
    set(key, value, ttlSeconds = 86400) {
        const expiry = Date.now() + ttlSeconds * 1000;
        this.cache.set(key, { value, expiry });
    }
    clean() {
        const now = Date.now();
        for (const [key, entry] of this.cache.entries()) {
            if (now > entry.expiry) {
                this.cache.delete(key);
            }
        }
    }
}
const idempotencyCache = new MemoryCache();
export function idempotencyMiddleware(req, res, next) {
    const key = req.headers['x-idempotency-key'];
    if (!key || typeof key !== 'string')
        return next();
    const cacheKey = `idempotency:${key}`;
    const cachedResponse = idempotencyCache.get(cacheKey);
    if (cachedResponse) {
        return res.status(cachedResponse.status).json(cachedResponse.body);
    }
    // Intercept the response JSON to store it in cache
    const originalJson = res.json.bind(res);
    res.json = (body) => {
        idempotencyCache.set(cacheKey, { status: res.statusCode, body }, 86400); // Cache for 24 hours
        return originalJson(body);
    };
    next();
}
//# sourceMappingURL=idempotency.js.map