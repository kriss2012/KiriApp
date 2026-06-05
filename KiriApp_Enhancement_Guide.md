# KiriApp v2 — Complete Professional Enhancement Guide

> **Scope:** Performance · API Reliability · UI/UX Overhaul · High-Load Scaling · Multi-Tap Bug Fixes · Feature Completeness
> **Branch:** v2 | **Date:** June 2026

---

## Table of Contents

1. [Critical API Timeout & Error Handling](#1-critical-api-timeout--error-handling)
2. [High-User-Load & Scalability](#2-high-user-load--scalability)
3. [Multiple-Tap & Double-Submission Bugs](#3-multiple-tap--double-submission-bugs)
4. [Performance Optimization](#4-performance-optimization)
5. [UI/UX Complete Overhaul](#5-uiux-complete-overhaul)
6. [Feature Enhancements](#6-feature-enhancements)
7. [Error Monitoring & Observability](#7-error-monitoring--observability)
8. [Security Hardening](#8-security-hardening)
9. [Implementation Roadmap](#9-implementation-roadmap)

---

## 1. Critical API Timeout & Error Handling

### The Problem
- API requests hang indefinitely → UI freezes
- No retry logic → single network blip = hard failure
- Errors surface as raw JSON or blank screens
- No abort/cancel mechanism when user navigates away

---

### 1.1 Implement a Centralized API Client (`apiClient.js`)

Replace all bare `fetch()` / `axios` calls with one hardened wrapper:

```js
// src/lib/apiClient.js

const BASE_URL = process.env.REACT_APP_API_BASE_URL;
const DEFAULT_TIMEOUT_MS = 10_000;   // 10 seconds
const MAX_RETRIES = 3;
const RETRY_DELAY_BASE_MS = 500;     // exponential back-off base

class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.status  = status;
    this.data    = data;
    this.name    = 'ApiError';
  }
}

async function sleep(ms) {
  return new Promise(r => setTimeout(r, ms));
}

export async function apiRequest(endpoint, options = {}, retries = MAX_RETRIES) {
  const controller  = new AbortController();
  const timeoutId   = setTimeout(() => controller.abort(), DEFAULT_TIMEOUT_MS);

  const config = {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
      ...options.headers,
    },
    signal: controller.signal,
    ...options,
  };

  try {
    const response = await fetch(`${BASE_URL}${endpoint}`, config);
    clearTimeout(timeoutId);

    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      throw new ApiError(
        errData.message || `HTTP ${response.status}`,
        response.status,
        errData
      );
    }

    return await response.json();

  } catch (err) {
    clearTimeout(timeoutId);

    const isNetworkError = err.name === 'AbortError' || !navigator.onLine;
    const isRetryable    = isNetworkError || err.status >= 500;

    if (retries > 0 && isRetryable) {
      const delay = RETRY_DELAY_BASE_MS * (MAX_RETRIES - retries + 1); // 500ms, 1000ms, 1500ms
      console.warn(`[API] Retrying ${endpoint} in ${delay}ms... (${retries} left)`);
      await sleep(delay);
      return apiRequest(endpoint, options, retries - 1);
    }

    throw err;
  }
}
```

**Key points:**
- `AbortController` kills the request after 10 s automatically
- Exponential back-off on 5xx errors AND network failures
- 401 / 403 never retried — fail fast for auth errors
- Single place to update auth headers, base URL, timeouts

---

### 1.2 React Query / SWR — Declarative Data Fetching

Stop managing `loading / error / data` state by hand. Use **React Query (TanStack Query)** or **SWR**:

```bash
npm install @tanstack/react-query
```

```jsx
// src/hooks/useUserData.js
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiRequest } from '../lib/apiClient';

export function useUserData(userId) {
  return useQuery({
    queryKey: ['user', userId],
    queryFn:  () => apiRequest(`/users/${userId}`),
    staleTime:        60_000,    // re-use cache for 60 s
    cacheTime:       300_000,    // keep in memory 5 min
    retry:                  2,   // built-in retry on top of our client
    retryDelay: attempt => Math.min(1000 * 2 ** attempt, 30_000),
    onError: err => console.error('[useUserData]', err.message),
  });
}
```

```jsx
// Usage in a component
function ProfilePage() {
  const { data, isLoading, isError, error } = useUserData('me');

  if (isLoading) return <LoadingSkeleton />;
  if (isError)   return <ErrorBanner message={error.message} />;
  return <ProfileCard user={data} />;
}
```

Benefits:
- Automatic background refetch on window focus
- Cache deduplication — 10 components mounting = 1 actual request
- Optimistic updates for mutations
- Offline / stale-while-revalidate support out of the box

---

### 1.3 Backend: Request Timeout + Circuit Breaker

**Node.js / Express example:**

```js
// middleware/timeout.js
import timeout from 'connect-timeout';

app.use(timeout('15s'));  // kill request before client gives up

app.use((req, res, next) => {
  if (!req.timedout) next();
});
```

**Circuit Breaker with `opossum`:**

```bash
npm install opossum
```

```js
import CircuitBreaker from 'opossum';
import { callExternalService } from './externalService';

const breaker = new CircuitBreaker(callExternalService, {
  timeout:          5000,   // 5 s per call
  errorThresholdPercentage: 50,   // trip when 50 % fail
  resetTimeout:    30_000,  // retry after 30 s
});

breaker.fallback(() => ({ cached: true, data: getCachedFallback() }));

breaker.on('open',     () => console.error('Circuit OPEN - external service unavailable'));
breaker.on('halfOpen', () => console.warn('Circuit HALF-OPEN - testing recovery'));
breaker.on('close',    () => console.log('Circuit CLOSED - service restored'));

export async function safeExternalCall(params) {
  return breaker.fire(params);
}
```

---

### 1.4 Global Error Boundary (React)

```jsx
// src/components/ErrorBoundary.jsx
import React from 'react';

class ErrorBoundary extends React.Component {
  state = { hasError: false, error: null };

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    // Send to Sentry / LogRocket
    reportError(error, info.componentStack);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <h2>Something went wrong</h2>
          <p>{this.state.error?.message}</p>
          <button onClick={() => this.setState({ hasError: false })}>
            Try again
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

// Wrap entire app
<ErrorBoundary>
  <App />
</ErrorBoundary>
```

---

## 2. High-User-Load & Scalability

### 2.1 Backend — Rate Limiting

```bash
npm install express-rate-limit redis ioredis rate-limit-redis
```

```js
import rateLimit from 'express-rate-limit';
import RedisStore from 'rate-limit-redis';
import { redisClient } from './redis';

// Per-IP general limit
export const generalLimiter = rateLimit({
  windowMs:  15 * 60 * 1000,   // 15 min
  max:               200,
  standardHeaders:  true,
  legacyHeaders:    false,
  store: new RedisStore({ sendCommand: (...args) => redisClient.sendCommand(args) }),
  message: { error: 'Too many requests, please slow down.' },
});

// Stricter limit for auth endpoints
export const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 10,  // 10 login attempts per 15 min
  skipSuccessfulRequests: true,
});

app.use('/api/',      generalLimiter);
app.use('/api/auth/', authLimiter);
```

---

### 2.2 Redis Caching Layer

```js
// src/cache/redis.js
import Redis from 'ioredis';

const redis = new Redis({
  host: process.env.REDIS_HOST,
  port: process.env.REDIS_PORT,
  enableAutoPipelining: true,
  maxRetriesPerRequest: 3,
});

export async function cacheGet(key) {
  const val = await redis.get(key);
  return val ? JSON.parse(val) : null;
}

export async function cacheSet(key, value, ttlSeconds = 300) {
  await redis.set(key, JSON.stringify(value), 'EX', ttlSeconds);
}

// Usage in a route handler
app.get('/api/products', async (req, res) => {
  const cacheKey = `products:${JSON.stringify(req.query)}`;
  const cached   = await cacheGet(cacheKey);

  if (cached) {
    return res.json({ ...cached, fromCache: true });
  }

  const data = await db.query.products.findMany({ /* … */ });
  await cacheSet(cacheKey, data, 60);   // cache 60 s
  res.json(data);
});
```

---

### 2.3 Database — Connection Pooling & Query Optimization

```js
// PostgreSQL with pg-pool
import { Pool } from 'pg';

const pool = new Pool({
  max:              20,     // max connections in pool
  idleTimeoutMillis: 30_000,
  connectionTimeoutMillis: 2_000,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
});

// Always release connections
async function query(sql, params) {
  const client = await pool.connect();
  try {
    return await client.query(sql, params);
  } finally {
    client.release();
  }
}
```

**Add database indexes** for every field used in WHERE / ORDER BY:

```sql
-- Add these immediately
CREATE INDEX CONCURRENTLY idx_users_email       ON users(email);
CREATE INDEX CONCURRENTLY idx_sessions_user_id  ON sessions(user_id);
CREATE INDEX CONCURRENTLY idx_posts_created_at  ON posts(created_at DESC);
CREATE INDEX CONCURRENTLY idx_posts_user_id     ON posts(user_id, created_at DESC);
```

---

### 2.4 Horizontal Scaling with PM2 / Cluster

```js
// ecosystem.config.js
module.exports = {
  apps: [{
    name:      'kiri-api',
    script:    './dist/server.js',
    instances: 'max',      // one worker per CPU core
    exec_mode: 'cluster',
    max_memory_restart: '512M',
    env_production: {
      NODE_ENV:  'production',
      PORT:      3001,
    },
  }],
};
```

```bash
pm2 start ecosystem.config.js --env production
pm2 save
pm2 startup
```

---

### 2.5 Frontend — Request Deduplication & Cancellation

```js
// src/lib/requestManager.js
const pendingRequests = new Map();

export function dedupedRequest(key, requestFn) {
  if (pendingRequests.has(key)) {
    return pendingRequests.get(key);  // return the SAME promise
  }
  const promise = requestFn().finally(() => pendingRequests.delete(key));
  pendingRequests.set(key, promise);
  return promise;
}
```

---

## 3. Multiple-Tap & Double-Submission Bugs

This is one of the most damaging UX and data-integrity bugs. Here is the definitive, permanent fix.

### 3.1 Frontend — useAsyncAction Hook

```js
// src/hooks/useAsyncAction.js
import { useState, useCallback, useRef } from 'react';

export function useAsyncAction(asyncFn) {
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);
  const inFlight              = useRef(false);  // ref, NOT state — avoids re-render race

  const execute = useCallback(async (...args) => {
    if (inFlight.current) return;   // hard gate — second tap blocked
    inFlight.current = true;
    setLoading(true);
    setError(null);

    try {
      const result = await asyncFn(...args);
      return result;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setLoading(false);
      inFlight.current = false;
    }
  }, [asyncFn]);

  return { execute, loading, error };
}
```

### 3.2 SafeButton Component

```jsx
// src/components/SafeButton.jsx
import { useRef } from 'react';

export function SafeButton({ onClick, children, debounceMs = 300, ...props }) {
  const lastClick = useRef(0);

  function handleClick(e) {
    const now = Date.now();
    if (now - lastClick.current < debounceMs) return;  // debounce
    lastClick.current = now;
    onClick(e);
  }

  return (
    <button
      {...props}
      onClick={handleClick}
      disabled={props.disabled}
      style={{ pointerEvents: props.disabled ? 'none' : 'auto', ...props.style }}
    >
      {children}
    </button>
  );
}
```

### 3.3 Full Usage Example

```jsx
function SubmitOrderButton({ order }) {
  const { execute, loading } = useAsyncAction(submitOrder);

  return (
    <SafeButton
      onClick={() => execute(order)}
      disabled={loading}
      className={loading ? 'btn-loading' : 'btn-primary'}
    >
      {loading ? <Spinner size="sm" /> : 'Submit Order'}
    </SafeButton>
  );
}
```

### 3.4 Backend — Idempotency Keys (the permanent server-side fix)

Client sends a unique key; server ignores duplicate requests entirely:

```js
// middleware/idempotency.js
import { cacheGet, cacheSet } from '../cache/redis';

export async function idempotencyMiddleware(req, res, next) {
  const key = req.headers['x-idempotency-key'];
  if (!key) return next();

  const existing = await cacheGet(`idempotency:${key}`);
  if (existing) {
    // Return the EXACT same response as the original request
    return res.status(existing.status).json(existing.body);
  }

  // Intercept response and store it
  const originalJson = res.json.bind(res);
  res.json = async (body) => {
    await cacheSet(`idempotency:${key}`, { status: res.statusCode, body }, 86400);
    originalJson(body);
  };

  next();
}

app.use('/api/orders',   idempotencyMiddleware);
app.use('/api/payments', idempotencyMiddleware);
```

```js
// Frontend — generate idempotency key per action attempt
import { v4 as uuidv4 } from 'uuid';

async function submitOrder(order) {
  return apiRequest('/orders', {
    method: 'POST',
    headers: { 'x-idempotency-key': uuidv4() },
    body: JSON.stringify(order),
  });
}
```

---

## 4. Performance Optimization

### 4.1 Frontend Bundle Size

```bash
# Analyze your bundle first
npx vite-bundle-visualizer   # Vite
# or
npx webpack-bundle-analyzer  # Webpack / CRA
```

**Code-split every route:**

```jsx
// src/App.jsx
import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import PageSkeleton from './components/PageSkeleton';

const Home     = lazy(() => import('./pages/Home'));
const Profile  = lazy(() => import('./pages/Profile'));
const Settings = lazy(() => import('./pages/Settings'));
const Dashboard= lazy(() => import('./pages/Dashboard'));

export default function App() {
  return (
    <Suspense fallback={<PageSkeleton />}>
      <Routes>
        <Route path="/"          element={<Home />}      />
        <Route path="/profile"   element={<Profile />}   />
        <Route path="/settings"  element={<Settings />}  />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </Suspense>
  );
}
```

### 4.2 React Rendering Performance

```jsx
// Memoize expensive components
import { memo, useMemo, useCallback } from 'react';

const ItemCard = memo(function ItemCard({ item, onSelect }) {
  return (
    <div className="card" onClick={() => onSelect(item.id)}>
      <h3>{item.title}</h3>
    </div>
  );
});

function ItemList({ items, filter }) {
  // Memoize filtered/sorted list — avoid recomputing on every render
  const filtered = useMemo(
    () => items.filter(i => i.category === filter).sort((a, b) => b.createdAt - a.createdAt),
    [items, filter]
  );

  // Stable callback reference — prevents ItemCard re-renders
  const handleSelect = useCallback((id) => { /* … */ }, []);

  return filtered.map(item => (
    <ItemCard key={item.id} item={item} onSelect={handleSelect} />
  ));
}
```

### 4.3 Virtual List for Long Lists

```bash
npm install @tanstack/react-virtual
```

```jsx
import { useVirtualizer } from '@tanstack/react-virtual';
import { useRef } from 'react';

function VirtualFeed({ items }) {
  const parentRef  = useRef(null);
  const virtualizer = useVirtualizer({
    count:           items.length,
    getScrollElement: () => parentRef.current,
    estimateSize:    () => 80,        // estimated row height px
    overscan:              5,
  });

  return (
    <div ref={parentRef} style={{ height: '100vh', overflow: 'auto' }}>
      <div style={{ height: virtualizer.getTotalSize(), position: 'relative' }}>
        {virtualizer.getVirtualItems().map(vItem => (
          <div
            key={vItem.key}
            style={{
              position: 'absolute',
              top: 0,
              transform: `translateY(${vItem.start}px)`,
              width: '100%',
            }}
          >
            <FeedItem item={items[vItem.index]} />
          </div>
        ))}
      </div>
    </div>
  );
}
```

### 4.4 Image Optimization

```jsx
// Use next/image or lazy loading
function OptimizedImage({ src, alt, width, height }) {
  return (
    <img
      src={src}
      alt={alt}
      width={width}
      height={height}
      loading="lazy"
      decoding="async"
      style={{ contentVisibility: 'auto' }}
    />
  );
}
```

```nginx
# nginx.conf — Enable compression and caching
gzip on;
gzip_types text/plain text/css application/json application/javascript;
gzip_min_length 1024;

location ~* \.(js|css|png|jpg|svg|woff2)$ {
  expires 1y;
  add_header Cache-Control "public, immutable";
}
```

### 4.5 Service Worker — Offline Cache

```js
// public/sw.js
const CACHE_NAME = 'kiri-v2-cache';
const STATIC_ASSETS = ['/', '/index.html', '/static/css/main.css'];

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
  );
});

self.addEventListener('fetch', e => {
  if (e.request.method !== 'GET') return;
  e.respondWith(
    caches.match(e.request).then(cached => cached || fetch(e.request))
  );
});
```

---

## 5. UI/UX Complete Overhaul

### 5.1 Design System — CSS Variables

```css
/* src/styles/tokens.css */
:root {
  /* Colors */
  --color-primary:     #6366f1;    /* Indigo */
  --color-primary-dk:  #4f46e5;
  --color-primary-lt:  #e0e7ff;
  --color-danger:      #ef4444;
  --color-success:     #22c55e;
  --color-warning:     #f59e0b;
  --color-surface:     #ffffff;
  --color-bg:          #f8fafc;
  --color-text:        #1e293b;
  --color-text-muted:  #64748b;
  --color-border:      #e2e8f0;

  /* Spacing (8-pt grid) */
  --space-1: 4px;   --space-2: 8px;   --space-3: 12px;
  --space-4: 16px;  --space-5: 20px;  --space-6: 24px;
  --space-8: 32px;  --space-10: 40px; --space-12: 48px;

  /* Typography */
  --font-sans: 'Inter', system-ui, -apple-system, sans-serif;
  --text-xs:   0.75rem;  /* 12px */
  --text-sm:   0.875rem; /* 14px */
  --text-base: 1rem;     /* 16px */
  --text-lg:   1.125rem; /* 18px */
  --text-xl:   1.25rem;  /* 20px */
  --text-2xl:  1.5rem;   /* 24px */
  --text-3xl:  1.875rem; /* 30px */

  /* Shadows */
  --shadow-sm: 0 1px 2px rgba(0,0,0,.05);
  --shadow-md: 0 4px 6px rgba(0,0,0,.07);
  --shadow-lg: 0 10px 15px rgba(0,0,0,.1);

  /* Radius */
  --radius-sm: 4px;  --radius-md: 8px;
  --radius-lg: 12px; --radius-full: 9999px;

  /* Transitions */
  --transition-fast: 150ms ease;
  --transition-base: 250ms ease;
}

/* Dark mode */
@media (prefers-color-scheme: dark) {
  :root {
    --color-surface:    #1e293b;
    --color-bg:         #0f172a;
    --color-text:       #f1f5f9;
    --color-text-muted: #94a3b8;
    --color-border:     #334155;
  }
}
```

### 5.2 Loading Skeleton Components

Replace spinners with content-shaped skeletons — dramatically improves perceived speed:

```jsx
// src/components/Skeleton.jsx
export function Skeleton({ width = '100%', height = '16px', borderRadius = '4px' }) {
  return (
    <div
      style={{ width, height, borderRadius }}
      className="skeleton-shimmer"
    />
  );
}

// CSS for shimmer animation
/*
.skeleton-shimmer {
  background: linear-gradient(
    90deg,
    var(--color-border) 25%,
    rgba(255,255,255,.4) 50%,
    var(--color-border) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
}

@keyframes shimmer {
  0%   { background-position:  200% 0; }
  100% { background-position: -200% 0; }
}
*/

// Card skeleton
export function CardSkeleton() {
  return (
    <div className="card" style={{ padding: 16 }}>
      <Skeleton height="12px" width="60%" />
      <Skeleton height="10px" width="40%" style={{ marginTop: 8 }} />
      <Skeleton height="80px" style={{ marginTop: 12 }} />
    </div>
  );
}
```

### 5.3 Toast Notification System

```bash
npm install react-hot-toast
```

```jsx
// In App root
import { Toaster } from 'react-hot-toast';

<Toaster
  position="top-right"
  toastOptions={{
    duration: 4000,
    style: { background: 'var(--color-surface)', color: 'var(--color-text)' },
    success: { iconTheme: { primary: 'var(--color-success)', secondary: '#fff' } },
    error:   { iconTheme: { primary: 'var(--color-danger)',  secondary: '#fff' } },
  }}
/>

// Usage
import toast from 'react-hot-toast';

toast.success('Saved successfully!');
toast.error('Network error — please retry.');
toast.loading('Uploading…');
```

### 5.4 Micro-Interactions & Transitions

```css
/* Buttons */
.btn-primary {
  background: var(--color-primary);
  color: white;
  padding: var(--space-3) var(--space-5);
  border-radius: var(--radius-md);
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: transform var(--transition-fast),
              box-shadow var(--transition-fast),
              background var(--transition-fast);
}

.btn-primary:hover  { background: var(--color-primary-dk); transform: translateY(-1px); box-shadow: var(--shadow-md); }
.btn-primary:active { transform: scale(.97); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; pointer-events: none; }

/* Cards */
.card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  transition: box-shadow var(--transition-base), transform var(--transition-base);
}
.card:hover { box-shadow: var(--shadow-lg); transform: translateY(-2px); }

/* Page transitions */
.page-enter        { opacity: 0; transform: translateY(8px); }
.page-enter-active { opacity: 1; transform: translateY(0); transition: all 200ms ease; }
.page-exit         { opacity: 1; }
.page-exit-active  { opacity: 0; transition: opacity 150ms ease; }
```

### 5.5 Empty States

Every list / data view must have a designed empty state:

```jsx
function EmptyState({ icon = '📭', title, description, action }) {
  return (
    <div style={{ textAlign: 'center', padding: '3rem 1rem' }}>
      <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>{icon}</div>
      <h3 style={{ color: 'var(--color-text)',      marginBottom: '.5rem' }}>{title}</h3>
      <p  style={{ color: 'var(--color-text-muted)', marginBottom: '1.5rem' }}>{description}</p>
      {action && <button className="btn-primary" onClick={action.onClick}>{action.label}</button>}
    </div>
  );
}
```

### 5.6 Responsive Layout

```css
/* Mobile-first grid */
.container { max-width: 1200px; margin: 0 auto; padding: 0 var(--space-4); }

.grid-2 { display: grid; grid-template-columns: 1fr; gap: var(--space-4); }
@media (min-width: 640px)  { .grid-2 { grid-template-columns: repeat(2, 1fr); } }
@media (min-width: 1024px) { .grid-2 { grid-template-columns: repeat(2, 1fr); gap: var(--space-6); } }

.grid-3 { display: grid; grid-template-columns: 1fr; gap: var(--space-4); }
@media (min-width: 768px)  { .grid-3 { grid-template-columns: repeat(2, 1fr); } }
@media (min-width: 1024px) { .grid-3 { grid-template-columns: repeat(3, 1fr); } }

/* Touch targets — minimum 44×44px on mobile */
@media (max-width: 768px) {
  button, a, [role="button"] { min-height: 44px; min-width: 44px; }
}
```

---

## 6. Feature Enhancements

### 6.1 Offline Support & Optimistic Updates

```jsx
// Optimistic update pattern with React Query
const queryClient = useQueryClient();

const likeMutation = useMutation({
  mutationFn: (postId) => apiRequest(`/posts/${postId}/like`, { method: 'POST' }),

  // INSTANT feedback — update cache before server confirms
  onMutate: async (postId) => {
    await queryClient.cancelQueries(['posts']);
    const previous = queryClient.getQueryData(['posts']);

    queryClient.setQueryData(['posts'], old =>
      old.map(p => p.id === postId ? { ...p, likes: p.likes + 1, liked: true } : p)
    );
    return { previous };   // snapshot for rollback
  },

  // Rollback on failure
  onError: (err, postId, context) => {
    queryClient.setQueryData(['posts'], context.previous);
    toast.error('Could not like post — please retry.');
  },

  onSettled: () => queryClient.invalidateQueries(['posts']),
});
```

### 6.2 Real-time Updates with WebSocket

```js
// src/lib/socket.js
import { io } from 'socket.io-client';

let socket = null;

export function connectSocket(token) {
  socket = io(process.env.REACT_APP_WS_URL, {
    auth: { token },
    reconnectionAttempts:  5,
    reconnectionDelay:  2000,
    transports: ['websocket'],
  });

  socket.on('connect_error', (err) => {
    console.error('[Socket] Connect error:', err.message);
  });

  return socket;
}

export function getSocket() { return socket; }
```

```jsx
// Hook for real-time notifications
function useRealTimeNotifications() {
  const queryClient = useQueryClient();

  useEffect(() => {
    const socket = getSocket();
    if (!socket) return;

    socket.on('notification:new', (data) => {
      queryClient.setQueryData(['notifications'], old => [data, ...(old || [])]);
      toast(data.message);
    });

    return () => socket.off('notification:new');
  }, [queryClient]);
}
```

### 6.3 Search with Debounce

```jsx
// src/hooks/useDebounce.js
import { useState, useEffect } from 'react';

export function useDebounce(value, delay = 350) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return debounced;
}

// Usage
function SearchBar() {
  const [query,    setQuery]    = useState('');
  const debouncedQ = useDebounce(query, 350);
  const { data }   = useQuery({
    queryKey: ['search', debouncedQ],
    queryFn:  () => apiRequest(`/search?q=${encodeURIComponent(debouncedQ)}`),
    enabled:  debouncedQ.length > 1,
  });
  // …
}
```

### 6.4 Infinite Scroll / Pagination

```jsx
import { useInfiniteQuery } from '@tanstack/react-query';
import { useIntersectionObserver } from './useIntersectionObserver';
import { useRef } from 'react';

function Feed() {
  const loadMoreRef = useRef(null);

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } = useInfiniteQuery({
    queryKey: ['feed'],
    queryFn:  ({ pageParam = 0 }) =>
      apiRequest(`/feed?cursor=${pageParam}&limit=20`),
    getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined,
  });

  useIntersectionObserver({
    target:     loadMoreRef,
    onIntersect: fetchNextPage,
    enabled:    hasNextPage,
  });

  const items = data?.pages.flatMap(p => p.items) ?? [];

  return (
    <>
      <VirtualFeed items={items} />
      <div ref={loadMoreRef}>
        {isFetchingNextPage && <CardSkeleton />}
      </div>
    </>
  );
}
```

### 6.5 Persistent User Preferences

```js
// src/hooks/useLocalStorage.js
import { useState } from 'react';

export function useLocalStorage(key, initialValue) {
  const [stored, setStored] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch { return initialValue; }
  });

  const setValue = (value) => {
    try {
      setStored(value);
      window.localStorage.setItem(key, JSON.stringify(value));
    } catch (err) { console.error(err); }
  };

  return [stored, setValue];
}
```

---

## 7. Error Monitoring & Observability

### 7.1 Sentry Integration

```bash
npm install @sentry/react @sentry/tracing
```

```js
// src/index.js
import * as Sentry from '@sentry/react';
import { BrowserTracing } from '@sentry/tracing';

Sentry.init({
  dsn: process.env.REACT_APP_SENTRY_DSN,
  environment:         process.env.NODE_ENV,
  tracesSampleRate:    0.2,       // sample 20% of transactions
  replaysSessionSampleRate: 0.1,
  integrations: [
    new BrowserTracing(),
    new Sentry.Replay(),
  ],
});
```

### 7.2 Backend Structured Logging

```bash
npm install winston winston-daily-rotate-file
```

```js
// src/lib/logger.js
import winston from 'winston';

export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console(),
    new winston.transports.DailyRotateFile({
      filename:    'logs/error-%DATE%.log',
      datePattern: 'YYYY-MM-DD',
      level:       'error',
      maxFiles:    '14d',
    }),
  ],
});

// Usage
logger.info('Server started', { port: 3001, env: process.env.NODE_ENV });
logger.error('API failure', { endpoint: '/users', error: err.message, stack: err.stack });
```

### 7.3 Health Check Endpoint

```js
app.get('/health', async (req, res) => {
  const checks = {
    uptime:    process.uptime(),
    timestamp: new Date().toISOString(),
    database:  await checkDbConnection(),
    redis:     await checkRedisConnection(),
    memory:    process.memoryUsage(),
  };
  const healthy = checks.database && checks.redis;
  res.status(healthy ? 200 : 503).json({ status: healthy ? 'ok' : 'degraded', checks });
});
```

---

## 8. Security Hardening

### 8.1 HTTP Security Headers (Helmet)

```bash
npm install helmet
```

```js
import helmet from 'helmet';

app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      scriptSrc:  ["'self'", "'strict-dynamic'"],
      styleSrc:   ["'self'", "'unsafe-inline'"],
      imgSrc:     ["'self'", "data:", "https:"],
      connectSrc: ["'self'", process.env.API_URL],
    },
  },
  hsts: { maxAge: 31536000, includeSubDomains: true, preload: true },
}));
```

### 8.2 Input Validation

```bash
npm install zod
```

```js
import { z } from 'zod';

const CreateUserSchema = z.object({
  email:    z.string().email(),
  password: z.string().min(8).max(128),
  name:     z.string().min(1).max(100).trim(),
});

app.post('/api/users', async (req, res) => {
  const result = CreateUserSchema.safeParse(req.body);
  if (!result.success) {
    return res.status(400).json({ errors: result.error.flatten() });
  }
  // Proceed with validated result.data
});
```

---

## 9. Implementation Roadmap

| Priority | Task | Effort | Impact |
|----------|------|--------|--------|
| 🔴 P0 | Centralized API client with timeouts + retry | 1 day | Critical |
| 🔴 P0 | useAsyncAction + SafeButton (multi-tap fix) | 0.5 day | Critical |
| 🔴 P0 | Idempotency keys on POST endpoints | 1 day | Critical |
| 🔴 P0 | React Query / SWR migration | 2–3 days | High |
| 🟠 P1 | Redis caching layer | 1 day | High |
| 🟠 P1 | Rate limiting middleware | 0.5 day | High |
| 🟠 P1 | Global Error Boundary | 0.5 day | High |
| 🟠 P1 | Circuit Breaker for external services | 1 day | High |
| 🟡 P2 | Design system (CSS tokens + Skeleton loaders) | 2 days | Medium |
| 🟡 P2 | Route code-splitting + lazy loading | 1 day | Medium |
| 🟡 P2 | Virtual list for long feeds | 1 day | Medium |
| 🟡 P2 | Toast notification system | 0.5 day | Medium |
| 🟢 P3 | Sentry + structured logging | 1 day | Medium |
| 🟢 P3 | WebSocket real-time updates | 2 days | Medium |
| 🟢 P3 | Infinite scroll / pagination | 1 day | Medium |
| 🟢 P3 | Service Worker offline cache | 1 day | Low |
| 🟢 P3 | PM2 cluster + DB indexing | 0.5 day | High |

**Total estimated effort: ~2–3 sprints (4–6 weeks) for a small team**

---

## Quick Wins (do these today — < 2 hours each)

1. Add `AbortController` + 10s timeout to every existing `fetch()` call
2. Wrap every submit button with `disabled={loading}` + `inFlight` ref guard
3. Add `CREATE INDEX CONCURRENTLY` for `email`, `user_id`, `created_at` columns
4. Install `express-rate-limit` with a 200 req/15 min limit
5. Add `helmet()` to Express — one line, instant security uplift
6. Add `loading="lazy"` to all `<img>` tags

---

*This guide was generated for KiriApp v2. Apply changes branch by branch, test with load testing tools (k6 / Artillery) before deploying to production.*
