# EmployAI Connect — Complete Implementation & Enhancement Guide

> **Project:** EmployAI Connect · AXA GBS Hackathon 2026 — Kiri Squad  
> **Team:** Lokesh Chaudhari · Krishna Patil · Bhagyesh Sapkale  
> **Stack:** Android (Kotlin) · Node.js + Express · PostgreSQL · Redis · AWS · Gemini API  
> **Base Repo:** https://github.com/kriss2012/KiriApp/tree/v2

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Tech Stack & Dependencies](#3-tech-stack--dependencies)
4. [Module-by-Module Requirements](#4-module-by-module-requirements)
   - 4.1 Auth & Onboarding
   - 4.2 AI Career Intelligence Engine
   - 4.3 Smart Opportunity Hub
   - 4.4 Mentor & Community Network
   - 4.5 AI Team Formation Engine
   - 4.6 Dashboard & Analytics
5. [Database Schema](#5-database-schema)
6. [REST API Specification](#6-rest-api-specification)
7. [AI / ML Layer Design](#7-ai--ml-layer-design)
8. [Enhancement Features (Employability Boosters)](#8-enhancement-features-employability-boosters)
9. [Non-Functional Requirements](#9-non-functional-requirements)
10. [Phase-wise Implementation Roadmap](#10-phase-wise-implementation-roadmap)
11. [Testing Strategy](#11-testing-strategy)
12. [Security Checklist](#12-security-checklist)
13. [Deployment Guide (AWS)](#13-deployment-guide-aws)

---

## 1. Project Overview

### Problem Statement

India produces 1.5 Crore+ graduates every year, yet 47% remain unemployed within 6 months of graduation. The root causes are:

- Academic curriculum lags 3–5 years behind industry requirements
- Students have no personalised career guidance or mentorship access
- Internship/project opportunities are concentrated among top-college students
- Recruiters struggle to identify genuinely job-ready candidates from noise

### What EmployAI Connect Solves

EmployAI is a **three-sided platform** (Student · Recruiter · Institution) that uses AI to:

1. Assess current employability with a 0–100% score
2. Generate a personalised learning roadmap to close skill gaps
3. Match students to industry mentors with 94%+ compatibility
4. Auto-form high-performance project/hackathon teams
5. Curate and surface verified opportunities (jobs, internships, hackathons)

### Core Differentiator

> "Existing platforms help students *find* jobs. EmployAI helps students *become* employable."

---

## 2. System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER                             │
│   Android App (Kotlin/XML)  ·  Future: Web PWA              │
└─────────────────────────┬────────────────────────────────────┘
                          │ HTTPS / REST
┌─────────────────────────▼────────────────────────────────────┐
│                    API GATEWAY (AWS ALB)                      │
│              Rate Limiting · SSL Termination                  │
└──────┬──────────────────────────────────────┬────────────────┘
       │                                      │
┌──────▼──────┐                    ┌──────────▼──────────┐
│  Auth Service│                    │   Core API Service   │
│  (JWT/OAuth2)│                    │   Node.js + Express  │
└──────┬──────┘                    └──────────┬──────────┘
       │                                      │
       └──────────────┬───────────────────────┘
                      │
         ┌────────────▼─────────────┐
         │     AI / ML Service      │
         │  Gemini API · NLP · BERT │
         │  Recommendation Engine   │
         └────────────┬─────────────┘
                      │
         ┌────────────▼─────────────┐
         │      Data Layer           │
         │  PostgreSQL (primary DB)  │
         │  Redis (cache/sessions)   │
         │  AWS S3 (files/resumes)   │
         └──────────────────────────┘
```

### Microservice Breakdown

| Service | Responsibility | Port |
|---|---|---|
| `auth-service` | Registration, login, JWT/OAuth2, refresh tokens | 3001 |
| `profile-service` | User profiles, resume upload, skills CRUD | 3002 |
| `ai-service` | Resume analysis, scoring, gap detection, roadmap gen | 3003 |
| `mentor-service` | Mentor registry, matching algorithm, session booking | 3004 |
| `team-service` | Team formation, GitHub analysis, matching | 3005 |
| `opportunity-service` | Jobs/internships CRUD, AI candidate matching | 3006 |
| `notification-service` | Push notifications (FCM), emails (SES) | 3007 |
| `analytics-service` | Institution dashboard, recruiter analytics | 3008 |

---

## 3. Tech Stack & Dependencies

### Android Client

```
Language         : Kotlin 1.9+
Min SDK          : 26 (Android 8.0)
Target SDK       : 34 (Android 14)
UI               : XML Layouts + Material Design 3
Architecture     : MVVM + Clean Architecture
DI               : Hilt
Networking       : Retrofit 2 + OkHttp3
Async            : Kotlin Coroutines + Flow
Image Loading    : Coil
Local Storage    : Room DB (offline cache)
Charts           : MPAndroidChart
PDF Viewer       : PdfRenderer (built-in)
Auth             : Google Sign-In SDK
Push             : Firebase Cloud Messaging (FCM)
Crash Reporting  : Firebase Crashlytics
Analytics        : Firebase Analytics
```

**Gradle Dependencies (app/build.gradle)**

```groovy
dependencies {
    // Core
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'

    // Hilt DI
    implementation 'com.google.dagger:hilt-android:2.50'
    kapt 'com.google.dagger:hilt-compiler:2.50'

    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

    // Room
    implementation 'androidx.room:room-runtime:2.6.1'
    implementation 'androidx.room:room-ktx:2.6.1'
    kapt 'androidx.room:room-compiler:2.6.1'

    // UI
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'io.coil-kt:coil:2.5.0'
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-messaging-ktx'
    implementation 'com.google.firebase:firebase-crashlytics-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
}
```

### Backend (Node.js)

```
Runtime          : Node.js 20 LTS
Framework        : Express.js 4.x
ORM              : Prisma 5.x
Database         : PostgreSQL 15
Cache            : Redis 7 (ioredis)
Auth             : jsonwebtoken + bcrypt + Passport.js
File Storage     : AWS S3 (multer-s3)
Email            : AWS SES (nodemailer)
Push Notif       : Firebase Admin SDK
Job Queue        : Bull (Redis-backed)
Logging          : Winston + Morgan
API Docs         : Swagger / OpenAPI 3.0
Testing          : Jest + Supertest
Containerisation : Docker + Docker Compose
```

**package.json (key dependencies)**

```json
{
  "dependencies": {
    "express": "^4.18.2",
    "@prisma/client": "^5.7.0",
    "bcrypt": "^5.1.1",
    "jsonwebtoken": "^9.0.2",
    "ioredis": "^5.3.2",
    "multer": "^1.4.5-lts.1",
    "@aws-sdk/client-s3": "^3.490.0",
    "@aws-sdk/client-ses": "^3.490.0",
    "firebase-admin": "^12.0.0",
    "bull": "^4.12.2",
    "axios": "^1.6.5",
    "winston": "^3.11.0",
    "swagger-ui-express": "^5.0.0",
    "zod": "^3.22.4",
    "cors": "^2.8.5",
    "helmet": "^7.1.0",
    "express-rate-limit": "^7.1.5"
  }
}
```

### AI / ML Layer

```
Primary LLM      : Google Gemini 1.5 Pro (via REST API)
Resume Parsing   : Python microservice (spaCy + HuggingFace BERT)
Recommendation   : Collaborative Filtering (Python / scikit-learn)
Team Matching    : Multi-dimensional cosine similarity (Python / NumPy)
Embeddings       : sentence-transformers (all-MiniLM-L6-v2)
GitHub Analysis  : PyGithub + GitHub REST API v3
Serving          : FastAPI (Python 3.11)
```

---

## 4. Module-by-Module Requirements

---

### 4.1 Auth & Onboarding

#### Functional Requirements

- **FR-AUTH-01** Users can register with email + password or Google OAuth2
- **FR-AUTH-02** Email OTP verification before account activation (6-digit, 10-min expiry)
- **FR-AUTH-03** JWT access token (15-min TTL) + refresh token (30-day TTL) in HTTP-only cookie
- **FR-AUTH-04** Forgot password via email reset link (UUID token, 1-hr expiry)
- **FR-AUTH-05** Role selection during onboarding: Student | Mentor | Recruiter | Institution Admin
- **FR-AUTH-06** Profile setup wizard (5 steps): Basic Info → Education → Skills → Resume Upload → Career Goals
- **FR-AUTH-07** Resume parsed automatically on upload; extracted data pre-fills profile fields

#### Onboarding Flow (Student)

```
Step 1: Basic Info
  - Full name, profile photo, phone number
  - College name (autocomplete from institution list)
  - Graduation year, degree, branch/stream

Step 2: Skills
  - Tag-based multi-select (Technical + Soft skills)
  - Self-rated proficiency: Beginner / Intermediate / Advanced / Expert
  - GitHub profile link (optional but recommended)
  - LinkedIn URL

Step 3: Resume Upload
  - PDF only, max 5 MB
  - Triggers AI parsing job in background
  - Extracted skills shown for confirmation/correction

Step 4: Career Goals
  - Target role (dropdown: 200+ pre-defined roles)
  - Preferred domain (Web Dev / Data / AI-ML / DevOps / Design / PM / Finance etc.)
  - Open to: Internship | Full-time | Remote | Relocation

Step 5: Review & Launch
  - Preview computed employability score
  - CTA: "View My Roadmap"
```

---

### 4.2 AI Career Intelligence Engine

This is the **core differentiation module**.

#### Functional Requirements

- **FR-AI-01** Employability Score (0–100%) computed from a weighted multi-factor model
- **FR-AI-02** Skill Gap Detection comparing user profile vs. target role benchmark
- **FR-AI-03** Personalised Learning Roadmap with curated resources and milestones
- **FR-AI-04** Resume Score with actionable ATS improvement suggestions
- **FR-AI-05** Score recalculates automatically when user completes a course, adds a project, or earns a certification

#### Employability Score Formula

```
Employability Score = Weighted sum of:

  Technical Skills Match    → 30%
    (matched_skills / required_skills × 100)

  Project Portfolio         → 20%
    (0 projects=0, 1=40, 2=70, 3+=100; bonus for GitHub stars/forks)

  Resume Quality Score      → 15%
    (ATS parse rate, keyword density, action verbs, quantified achievements)

  Certifications            → 10%
    (industry-recognised certs carry higher weight)

  Mock Interview Score      → 10%
    (avg. score across last 3 mock interviews)

  Communication/Soft Skills → 10%
    (self-assessed + peer-validated via endorsements)

  GitHub Activity           → 5%
    (commit frequency, repo count, contribution graph)
```

#### Skill Gap Detection Logic

```
1. User selects Target Role (e.g., "Data Analyst")
2. System fetches Role Skill Benchmark:
   - Required Skills (Critical / High / Medium priority)
   - Source: aggregated from 10,000+ real job descriptions (NLP-extracted)
3. Compare user.skills vs. benchmark.required_skills
4. Gap = benchmark.required_skills - user.skills
5. Each gap skill tagged: Critical | High | Medium
6. Output: Ordered list of skills to acquire with priority
```

#### Learning Roadmap Generation

```
Input: Gap skills list + user's available study hours/week
Output: Week-by-week plan

For each gap skill:
  1. Identify best free resource (Coursera / YouTube / freeCodeCamp / docs)
  2. Estimate hours to completion
  3. Assign recommended week
  4. Link to practice project idea
  5. Milestone: Mini-assessment quiz at end

Roadmap adjusts dynamically when:
  - User marks a resource as complete
  - User adds a new skill
  - User changes target role
```

#### Resume Analysis Requirements

```
NLP Pipeline (BERT-based):

1. Section Detection: Contact | Summary | Education | Experience |
                      Skills | Projects | Certifications | Awards
2. Skill Extraction: Named Entity Recognition for tech skills
3. Experience Parsing: Company · Role · Duration · Responsibilities
4. ATS Score:
   - Keyword match against target JD (user-provided or role-default)
   - Formatting issues (tables/columns break ATS)
   - Missing sections
   - Action verb strength scoring
4. Suggestions Output:
   - "Add quantified achievement in Experience section"
   - "Your resume lacks a Summary/Objective section"
   - "Missing keywords for Data Analyst: Power BI, SQL, Tableau"
```

---

### 4.3 Smart Opportunity Hub

#### Functional Requirements

- **FR-OPP-01** Students can browse and apply to internships, jobs, research projects, hackathons
- **FR-OPP-02** AI auto-recommends top 5 opportunities daily based on profile
- **FR-OPP-03** Recruiters can post opportunities and receive AI-ranked candidate shortlist
- **FR-OPP-04** Startups can post co-founder matching requests
- **FR-OPP-05** All company-posted opportunities are verified (email domain + LinkedIn cross-check)
- **FR-OPP-06** One-click apply with auto-filled profile data
- **FR-OPP-07** Application tracker: Applied → Under Review → Shortlisted → Interview → Offer/Rejected

#### Opportunity Object (Employer Side)

```
{
  title: string
  company: string (verified)
  type: "INTERNSHIP" | "FULL_TIME" | "PART_TIME" | "CONTRACT" | "HACKATHON" | "RESEARCH"
  location: string
  remote: boolean
  stipend/salary: { min, max, currency }
  duration: string  (for internships)
  skills_required: string[]
  experience_level: "FRESHER" | "0-1yr" | "1-3yr"
  deadline: date
  description: string
  perks: string[]
  posted_by: recruiter_id
  ai_match_score: float  (computed per student)
}
```

#### AI Candidate Matching (Recruiter Side)

```
For each job posting, rank all student applicants by:

Match Score =
  skill_overlap_score × 0.40
  + employability_score × 0.25
  + experience_relevance × 0.20
  + location_preference_match × 0.10
  + availability_match × 0.05

Output: Ranked list with match %, key strengths, gaps
```

---

### 4.4 Mentor & Community Network

#### Functional Requirements

- **FR-MEN-01** Industry professionals can register as mentors with verified LinkedIn profiles
- **FR-MEN-02** AI matches student to top 5 mentor suggestions with compatibility score
- **FR-MEN-03** Student can request a mentorship session (30/60 min slots)
- **FR-MEN-04** In-app video/audio call integration (Daily.co or Jitsi SDK)
- **FR-MEN-05** Mentor can upload resume review as annotated PDF
- **FR-MEN-06** AI-powered mock interview: generates role-specific questions, records answers, provides feedback
- **FR-MEN-07** Community feed: posts, comments, resource sharing, polls
- **FR-MEN-08** Study groups: private groups of up to 10 students with shared resources

#### Mentor Matching Algorithm

```
Compatibility Score = 
  career_goal_alignment × 0.35
    (target role of student vs. mentor's domain)
  + skill_relevance × 0.30
    (mentor's expertise overlaps with student's gap skills)
  + industry_match × 0.20
    (student's preferred industry vs. mentor's industry)
  + availability_overlap × 0.10
    (timezone + weekly availability slots)
  + language_preference × 0.05

Minimum compatibility threshold to be shown: 70%
```

#### Mock Interview System

```
Flow:
1. Student selects role and difficulty (Easy / Medium / Hard)
2. AI generates 10 questions:
   - 3 Technical (role-specific)
   - 3 Behavioral (STAR format expected)
   - 2 Situational
   - 1 "Why this company?" (if company selected)
   - 1 Salary/timeline negotiation (Hard mode only)
3. Student answers via audio or text
4. AI evaluates:
   - Completeness of answer
   - Technical accuracy
   - Clarity and structure
   - STAR adherence (for behavioral)
   - Filler word count
5. Detailed feedback report + score out of 100
6. Comparison with previous attempts (trend)
```

---

### 4.5 AI Team Formation Engine

#### Functional Requirements

- **FR-TEAM-01** Student can post a team request for a hackathon/project with required roles
- **FR-TEAM-02** AI suggests best-match candidates for open slots
- **FR-TEAM-03** Team synergy score shown before finalisation
- **FR-TEAM-04** GitHub integration: fetch language proficiency from commit history
- **FR-TEAM-05** Teams can have a shared workspace (task board + file sharing)
- **FR-TEAM-06** Team rating/review after project completion

#### Matching Dimensions

```
For each candidate considered for a team slot:

Dimension 1: Technical Skill Complementarity (40%)
  - Required skills for the slot vs. candidate skills
  - No duplication with existing team members

Dimension 2: GitHub Activity Score (20%)
  - Recent commits, stars received, project diversity
  - Language stack fit

Dimension 3: Certification & Education (15%)
  - Relevant certifications match
  - Course completions in the required domain

Dimension 4: Interest & Passion Alignment (15%)
  - Domain interest tags match project domain
  - Past hackathon participation

Dimension 5: Experience Balance (10%)
  - Mix of experience levels preferred
  - Avoid teams where all members are beginners
```

---

### 4.6 Dashboard & Analytics

#### Student Dashboard

```
Widgets (in order of priority):
  1. Employability Score ring (0-100%) + delta from last week
  2. Today's Action Items (3 items from roadmap)
  3. Top 3 Recommended Opportunities (AI-picked)
  4. Skill Progress Bar (skills in progress)
  5. Upcoming mentor session (countdown)
  6. Recent community activity
  7. Quick stats: Projects | Certifications | Applications | Mock Interviews
```

#### Institution Admin Dashboard

```
Metrics:
  - Average employability score across all students
  - Top 10 skill gaps in current batch (curriculum insight)
  - Placement conversion funnel (applied → offered → joined)
  - Company-wise hiring breakdown
  - Month-over-month score improvement trend
  - At-risk students (score < 40, flag for intervention)
```

#### Recruiter Dashboard

```
Metrics:
  - Active job postings + applicant counts
  - Top-matched candidates per posting
  - Pipeline stage breakdown
  - Time-to-shortlist analytics
  - Campus-wise talent availability heatmap
```

---

## 5. Database Schema

### Core Tables

```sql
-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash TEXT,
  role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT','MENTOR','RECRUITER','ADMIN')),
  google_id VARCHAR(255),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Student Profiles
CREATE TABLE student_profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id) ON DELETE CASCADE,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  college_id UUID REFERENCES institutions(id),
  degree VARCHAR(100),
  branch VARCHAR(100),
  graduation_year SMALLINT,
  github_url VARCHAR(255),
  linkedin_url VARCHAR(255),
  portfolio_url VARCHAR(255),
  resume_s3_key TEXT,
  profile_photo_s3_key TEXT,
  target_role VARCHAR(100),
  preferred_domain VARCHAR(100),
  open_to VARCHAR(50)[],
  bio TEXT,
  employability_score DECIMAL(5,2) DEFAULT 0,
  score_updated_at TIMESTAMP
);

-- Skills
CREATE TABLE skills (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) UNIQUE NOT NULL,
  category VARCHAR(50),  -- TECHNICAL | SOFT | TOOL | LANGUAGE
  domain VARCHAR(50)
);

CREATE TABLE student_skills (
  student_id UUID REFERENCES student_profiles(id),
  skill_id UUID REFERENCES skills(id),
  proficiency VARCHAR(20) CHECK (proficiency IN ('BEGINNER','INTERMEDIATE','ADVANCED','EXPERT')),
  verified BOOLEAN DEFAULT false,
  PRIMARY KEY (student_id, skill_id)
);

-- Role Skill Benchmarks
CREATE TABLE role_benchmarks (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  role_name VARCHAR(100) NOT NULL,
  skill_id UUID REFERENCES skills(id),
  priority VARCHAR(20) CHECK (priority IN ('CRITICAL','HIGH','MEDIUM','NICE_TO_HAVE')),
  UNIQUE(role_name, skill_id)
);

-- Opportunities
CREATE TABLE opportunities (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(255) NOT NULL,
  company_id UUID REFERENCES companies(id),
  type VARCHAR(30) NOT NULL,
  location VARCHAR(255),
  is_remote BOOLEAN DEFAULT false,
  stipend_min INTEGER,
  stipend_max INTEGER,
  currency VARCHAR(10) DEFAULT 'INR',
  duration VARCHAR(100),
  description TEXT,
  skills_required UUID[],
  deadline DATE,
  is_verified BOOLEAN DEFAULT false,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Applications
CREATE TABLE applications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_id UUID REFERENCES student_profiles(id),
  opportunity_id UUID REFERENCES opportunities(id),
  status VARCHAR(30) DEFAULT 'APPLIED',
  ai_match_score DECIMAL(5,2),
  applied_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  UNIQUE(student_id, opportunity_id)
);

-- Mentors
CREATE TABLE mentor_profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id),
  full_name VARCHAR(255),
  company VARCHAR(255),
  designation VARCHAR(255),
  years_experience SMALLINT,
  domains VARCHAR(50)[],
  availability JSONB,  -- {weekday: [slot1, slot2], weekend: [slot1]}
  linkedin_url VARCHAR(255),
  is_verified BOOLEAN DEFAULT false,
  bio TEXT,
  rating DECIMAL(3,2),
  total_mentees INTEGER DEFAULT 0
);

-- Mentor Sessions
CREATE TABLE mentor_sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mentor_id UUID REFERENCES mentor_profiles(id),
  student_id UUID REFERENCES student_profiles(id),
  scheduled_at TIMESTAMP,
  duration_minutes SMALLINT,
  type VARCHAR(30),  -- RESUME_REVIEW | MOCK_INTERVIEW | CAREER_ADVICE | PROJECT_REVIEW
  status VARCHAR(20) DEFAULT 'PENDING',
  meeting_url TEXT,
  notes TEXT,
  student_rating SMALLINT,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Teams
CREATE TABLE teams (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255),
  project_type VARCHAR(100),
  description TEXT,
  hackathon_id UUID,
  required_skills UUID[],
  max_members SMALLINT DEFAULT 4,
  created_by UUID REFERENCES student_profiles(id),
  synergy_score DECIMAL(5,2),
  status VARCHAR(20) DEFAULT 'FORMING',
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE team_members (
  team_id UUID REFERENCES teams(id),
  student_id UUID REFERENCES student_profiles(id),
  role VARCHAR(100),
  joined_at TIMESTAMP DEFAULT NOW(),
  PRIMARY KEY (team_id, student_id)
);

-- Learning Roadmap
CREATE TABLE learning_roadmaps (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_id UUID REFERENCES student_profiles(id),
  target_role VARCHAR(100),
  generated_at TIMESTAMP DEFAULT NOW(),
  is_active BOOLEAN DEFAULT true
);

CREATE TABLE roadmap_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  roadmap_id UUID REFERENCES learning_roadmaps(id),
  skill_id UUID REFERENCES skills(id),
  priority VARCHAR(20),
  resource_title VARCHAR(255),
  resource_url TEXT,
  resource_type VARCHAR(30),  -- COURSE | VIDEO | DOCS | BOOK
  estimated_hours SMALLINT,
  week_number SMALLINT,
  is_completed BOOLEAN DEFAULT false,
  completed_at TIMESTAMP
);

-- Mock Interviews
CREATE TABLE mock_interviews (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  student_id UUID REFERENCES student_profiles(id),
  target_role VARCHAR(100),
  difficulty VARCHAR(20),
  score DECIMAL(5,2),
  feedback JSONB,
  questions JSONB,
  answers JSONB,
  conducted_at TIMESTAMP DEFAULT NOW()
);

-- Institutions
CREATE TABLE institutions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  city VARCHAR(100),
  state VARCHAR(100),
  type VARCHAR(50),  -- ENGINEERING | MANAGEMENT | ARTS | POLYTECHNIC
  is_partner BOOLEAN DEFAULT false
);
```

---

## 6. REST API Specification

### Base URL: `https://api.employai.connect/v1`

### Auth Endpoints

```
POST   /auth/register               Register new user
POST   /auth/login                  Email/password login
POST   /auth/google                 Google OAuth2 callback
POST   /auth/verify-email           Verify OTP
POST   /auth/forgot-password        Send reset email
POST   /auth/reset-password         Reset with token
POST   /auth/refresh                Refresh JWT
DELETE /auth/logout                 Invalidate refresh token
```

### Profile Endpoints

```
GET    /profile/me                  Get current user profile
PUT    /profile/me                  Update profile
POST   /profile/resume              Upload resume (multipart/form-data)
GET    /profile/resume/parse        Trigger / get NLP parse result
PUT    /profile/skills              Update skills list
GET    /profile/:id                 Get public profile (other users)
```

### AI / Scoring Endpoints

```
GET    /ai/score                    Get current employability score
POST   /ai/score/refresh            Trigger recalculation
GET    /ai/skill-gaps               Get skill gap analysis for target role
GET    /ai/roadmap                  Get active learning roadmap
POST   /ai/roadmap/generate         Generate new roadmap
PUT    /ai/roadmap/items/:id        Mark roadmap item complete
GET    /ai/resume/analysis          Get latest resume analysis
POST   /ai/mock-interview/start     Generate interview questions
POST   /ai/mock-interview/submit    Submit answers, get feedback
GET    /ai/mock-interview/history   List past mock interviews
```

### Opportunity Endpoints

```
GET    /opportunities               List with filters (type, location, domain, etc.)
GET    /opportunities/:id           Get single opportunity details
POST   /opportunities/:id/apply     Apply (student)
GET    /opportunities/recommended   AI-recommended for current user
POST   /opportunities               Create opportunity (recruiter)
PUT    /opportunities/:id           Update opportunity (recruiter)
GET    /opportunities/my/postings   Recruiter's own postings
GET    /opportunities/:id/applicants AI-ranked applicants (recruiter)
GET    /applications/my             Student's application tracker
PUT    /applications/:id/status     Update status (recruiter)
```

### Mentor Endpoints

```
GET    /mentors                     List mentors (with filters)
GET    /mentors/recommended         AI-matched top 5 for me
GET    /mentors/:id                 Mentor profile
POST   /sessions/book               Book a session
GET    /sessions/my                 My sessions (as student or mentor)
PUT    /sessions/:id                Update session (reschedule/cancel)
POST   /sessions/:id/rating         Rate a completed session
```

### Team Endpoints

```
GET    /teams                       Browse open teams
POST   /teams                       Create a team
GET    /teams/:id                   Team details
POST   /teams/:id/join              Request to join
POST   /teams/:id/invite/:userId    Invite a user
GET    /teams/ai-suggest            AI suggests best teammates for a role
GET    /teams/my                    My teams
```

---

## 7. AI / ML Layer Design

### Python AI Microservice (FastAPI)

```
Endpoints:
  POST /parse-resume          → Returns structured JSON from PDF
  POST /compute-score         → Returns employability score breakdown
  POST /skill-gap             → Returns gap list with priorities
  POST /generate-roadmap      → Returns week-by-week plan
  POST /match-mentor          → Returns ranked mentor list
  POST /form-team             → Returns candidate ranking for team slot
  POST /mock-interview/gen    → Returns question set
  POST /mock-interview/eval   → Returns answer evaluation + feedback
  POST /match-opportunity     → Returns match score for student-opportunity pair
```

### Resume Parser (BERT-based NLP)

```python
# Key libraries
spacy >= 3.7
transformers >= 4.36     # HuggingFace BERT
sentence-transformers >= 2.3
pdfplumber >= 0.10
```

**Sections detected:** Contact, Summary, Education, Experience, Skills, Projects, Certifications, Awards, Languages, Hobbies

**Skills extraction:** Uses NER + custom skill entity list (seeded from 5000+ tech terms)

### Recommendation Engine

```
Algorithm: Item-based Collaborative Filtering + Content-based Hybrid

For opportunities:
  Content signals: skill overlap, location, stipend, domain
  Collaborative signals: "students like you applied to these"

For mentors:
  Content signals: domain match, career goal alignment, availability
  No collaborative signal (to preserve mentor privacy)

For learning resources:
  Collaborative signals: "students with your profile found these most useful"
  Content signals: skill relevance, resource type preference
```

### Gemini API Integration

```javascript
// Node.js wrapper (for dynamic AI features)
const { GoogleGenerativeAI } = require('@google/generative-ai');

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// Use cases:
// 1. Generate personalised resume improvement tips
// 2. Generate conversational mock interview follow-ups
// 3. Explain skill gap in plain language
// 4. Generate project ideas for gap skills
// 5. Summarise mentor notes for student
```

---

## 8. Enhancement Features (Employability Boosters)

These features go **beyond the base PPT** and are designed to significantly increase the employability ratio of students.

---

### E1 — Live Job Market Intelligence Feed

**What:** Real-time dashboard showing which skills are trending in the job market this week, salary trends by role and city, and which companies are hiring most.

**Why it matters:** Students make better learning decisions when they see live market demand.

**Implementation:**
- Scrape job postings from Naukri/LinkedIn APIs weekly (via official partner integrations)
- NLP-extract skills from new postings
- Maintain a "Trending Skills" index updated weekly
- Widget on student dashboard: "Power BI demand up 23% this month in Bangalore"

---

### E2 — AI Resume Builder (Not just Analyser)

**What:** From scratch AI-generated resume based on the student's profile data. Produces ATS-optimised, role-targeted PDF.

**Why it matters:** 70% of students don't know how to write a strong resume; analysis alone isn't enough.

**Implementation:**
- Template library (3 styles: Clean, Modern, Creative)
- Gemini API generates bullet points from raw experience input
- Auto-fills all sections from profile DB
- ATS score shown in real-time as student edits
- Export to PDF / DOCX

---

### E3 — Micro-Credential & Badge System

**What:** Students earn verifiable digital badges for completing roadmap milestones, passing mock interviews, completing projects.

**Why it matters:** Creates visible proof of progress; recruiters can filter by badge level.

**Implementation:**
- Badge types: Skill Badges (e.g., "SQL Certified"), Achievement Badges (e.g., "5 Mock Interviews Passed"), Milestone Badges
- Badges stored with metadata (date, criteria met) 
- Public profile shows badge wall
- Recruiter filter: "Only candidates with Power BI badge"

---

### E4 — Peer Code Review & Project Showcase

**What:** Students submit GitHub project links; peers and mentors can leave structured code reviews. Top projects are featured in the Opportunity Hub.

**Why it matters:** Real portfolio work is the #1 signal recruiters look for in freshers.

**Implementation:**
- Project submission form: GitHub URL + description + tech stack
- Review rubric: Code Quality | Documentation | Architecture | Innovation
- "Featured Project" tag for top-rated projects
- Auto-notify matched recruiters about featured projects

---

### E5 — Campus Ambassador Program

**What:** Top-performing students in each college earn "Campus Lead" status and can host webinars, run study groups, and refer peers for a points reward.

**Why it matters:** Viral campus growth + peer-to-peer engagement dramatically increases platform stickiness.

**Implementation:**
- Leaderboard per institution (top 3 become Campus Lead)
- Campus Lead perks: Priority mentor access, verified badge, referral points
- Referral tracking with point-to-premium-feature redemption

---

### E6 — Company-Specific Preparation Packs

**What:** Curated preparation packages for top 50 companies (TCS, Infosys, Amazon, Google, etc.) including: past interview questions, expected skills, selection process, salary benchmark.

**Why it matters:** Students applying to specific companies need targeted prep, not generic advice.

**Implementation:**
- "I'm targeting Amazon SDE" → system shows Amazon-specific gap skills, question bank (250+ questions), compensation data
- Questions crowd-sourced + AI-verified from student interview experiences
- Separate from general mock interview (company mode)

---

### E7 — Offline Mode with Smart Sync

**What:** Core features (roadmap, saved opportunities, downloaded resources) work without internet. Syncs when connected.

**Why it matters:** Many students in Tier-2/3 cities have inconsistent connectivity.

**Implementation:**
- Room DB caches: roadmap items, last 20 opportunities, saved mentors
- Offline indicator in UI
- Background sync with WorkManager (Android) when connection restores

---

### E8 — Vernacular Language Support

**What:** UI and AI responses available in Hindi, Marathi, Tamil, Telugu, Bengali, Kannada.

**Why it matters:** Huge population of employable students in Tier-2/3 cities are more comfortable in regional languages.

**Implementation:**
- Android string resources per locale
- Gemini API supports Hindi/regional prompts natively
- Resume tips and interview feedback generated in user's preferred language

---

### E9 — Mental Health & Burnout Detection

**What:** Subtle engagement pattern analysis detects burnout or anxiety spikes (e.g., dropping off the platform after rejection streaks). Surfaces supportive resources and suggests mentor check-in.

**Why it matters:** Mental health is a hidden employability blocker. Proactive nudges prevent dropout.

**Implementation:**
- Track: login frequency, roadmap completion rate, application activity
- If 7-day drop detected: soft nudge notification "It's okay to take a break. Here's what others in your situation found helpful."
- Resource links: college counsellor, mental health apps, motivational success stories from the platform

---

### E10 — Employer Branding & Campus Connect

**What:** Companies can create a branded profile page visible to students. Students can "follow" companies. Companies can post blogs, job previews, and host Q&A sessions.

**Why it matters:** Awareness of company culture is a top factor in application decision; it also keeps companies engaged on the platform.

**Implementation:**
- Company profile: Culture video, employee stories, benefits, office photos
- "Follow Company" → notifications when they post
- Live Q&A sessions hosted by recruiters (30-min slots, 50-student capacity)

---

### E11 — AI Career Counsellor Chatbot

**What:** Always-on conversational AI assistant accessible from every screen. Answers questions like "What should I learn next?", "Am I ready to apply for this job?", "How do I negotiate salary?"

**Why it matters:** Asynchronous mentorship gap filler; not every student can afford human mentor time.

**Implementation:**
- Gemini API with system prompt context (user's profile, score, roadmap)
- Persistent chat history per user
- Escalation: "Would you like me to find a human mentor for this topic?"
- Pre-defined intents for common queries (faster, cheaper responses)

---

### E12 — Alumni Network Integration

**What:** College alumni who are already employed can be onboarded as lightweight mentors (not requiring time commitment — just answering occasional questions in their domain).

**Why it matters:** Peer mentorship from alumni with 2-5 years experience is highly relatable and scales better than senior professional mentors.

**Implementation:**
- Alumni registration with college verification (graduation year + degree)
- Async Q&A format (not live sessions)
- Alumni earn recognition points for answers
- Institution admin can invite alumni via bulk email

---

## 9. Non-Functional Requirements

### Performance

| Metric | Target |
|---|---|
| API P95 response time | < 300ms |
| AI score computation | < 5s (async, show loader) |
| Resume parse completion | < 30s (background job) |
| App cold start time | < 3s |
| Dashboard load time | < 2s |
| Search results | < 1s |

### Scalability

- Backend services horizontally scalable behind AWS ALB
- PostgreSQL read replicas for analytics queries
- Redis cluster for session and scoring cache
- S3 CDN (CloudFront) for resume and media files
- Bull queue for all async AI jobs (with retry + dead-letter queue)

### Availability

- Target uptime: 99.5% (excludes planned maintenance)
- Maintenance windows: Sunday 02:00–04:00 IST
- Automatic failover: Multi-AZ RDS setup

### Data Retention & Privacy

- Resume files: retained for 2 years after account deletion request
- User data: DPDP Act 2023 compliant (India's data protection law)
- Right to erasure: 30-day processing SLA
- No selling of user data to third parties
- Anonymised data only used for aggregate ML model training

---

## 10. Phase-wise Implementation Roadmap

### Phase 1 — MVP (0–3 Months)

**Goal:** Core value loop working end-to-end for students.

- [ ] Android app skeleton: navigation, auth screens, dashboard
- [ ] User registration + Google OAuth
- [ ] Profile setup wizard (all 5 steps)
- [ ] Resume upload to S3
- [ ] Basic employability score (rule-based, no ML yet)
- [ ] Manual skill gap view (vs. hardcoded role benchmarks for 10 roles)
- [ ] Static learning roadmap (curated links, not AI-generated)
- [ ] Opportunity listing (manually seeded 100 opportunities)
- [ ] Basic application tracker
- [ ] Push notifications (FCM)
- [ ] Backend: all CRUD APIs, auth, PostgreSQL schema

**Deliverable:** Working APK installable by students for basic profile + opportunity discovery

---

### Phase 2 — AI Core (3–6 Months)

**Goal:** Differentiated AI features live.

- [ ] BERT-based resume parser (Python microservice)
- [ ] ML-based employability scoring model (trained on synthetic + crowdsourced data)
- [ ] AI skill gap detection (NLP from 1000+ JDs)
- [ ] Gemini-powered roadmap generation
- [ ] AI-powered mock interview (text-based)
- [ ] Mentor registration + basic matching
- [ ] AI team formation (initial version)
- [ ] E2: AI Resume Builder (Phase 2 enhancement)
- [ ] E11: AI Career Counsellor Chatbot

**Deliverable:** Full AI loop: score → gap → roadmap → mock interview

---

### Phase 3 — Scale & Ecosystem (6–12 Months)

**Goal:** Platform network effects and institutional adoption.

- [ ] Recruiter portal (web app)
- [ ] Institution admin dashboard
- [ ] Company-specific prep packs (E6)
- [ ] Micro-credential badge system (E3)
- [ ] Peer project showcase (E4)
- [ ] Campus ambassador program (E5)
- [ ] Live job market intelligence (E1)
- [ ] Vernacular language support (E8)
- [ ] Offline mode (E7)
- [ ] College management system integrations (via API)
- [ ] Blockchain credential pilot (select partner colleges)

**Deliverable:** 50+ college partnerships, 500+ company recruiters, 10,000+ students

---

### Phase 4 — Global & Enterprise (12–24 Months)

- [ ] Web PWA (React/Next.js) parallel to Android
- [ ] Employer branding pages (E10)
- [ ] Alumni network (E12)
- [ ] Mental health module (E9)
- [ ] International market entry (SEA: Indonesia, Philippines)
- [ ] Enterprise API for HR tools (Workday, SAP SuccessFactors)
- [ ] Investor dashboard for startup co-founder matching
- [ ] Series A fundraise support materials & analytics

---

## 11. Testing Strategy

### Unit Tests

- All service-layer functions
- AI scoring formula components
- Skill gap computation logic
- Tools: Jest (Node.js), JUnit5 (Android), Pytest (Python)

### Integration Tests

- API endpoint tests with database (Supertest + test DB)
- AI service integration (mock Gemini, test NLP pipeline)

### End-to-End Tests

- Critical flows: Register → Upload Resume → View Score → Apply to Job
- Mock interview complete flow
- Team formation flow
- Tools: Appium (Android), REST-assured

### Load Tests

- Simulate 1000 concurrent users on core endpoints
- AI score computation under queue load
- Tools: k6 or Apache JMeter

### AI Model Quality Tests

- Resume parser: precision/recall on 200 hand-labelled resumes
- Employability score: correlation with 6-month placement outcomes (retrospective validation)
- Mentor matching: A/B test satisfaction scores of AI-matched vs. self-selected mentors

---

## 12. Security Checklist

- [ ] All API endpoints require JWT auth (except /auth/*)
- [ ] Input validation with Zod on all request bodies
- [ ] SQL injection prevention via Prisma ORM (parameterised queries)
- [ ] Rate limiting: 100 req/min per IP, 1000 req/min per authenticated user
- [ ] Helmet.js security headers on all responses
- [ ] Resume files: signed S3 URLs (1-hour expiry), never direct public URLs
- [ ] Passwords: bcrypt with cost factor 12
- [ ] Refresh tokens stored hashed in DB; device binding optional
- [ ] CORS: whitelist only android app domain + recruiter web domain
- [ ] OAuth2 state parameter validated to prevent CSRF
- [ ] AI prompt injection defence: sanitise user inputs before Gemini calls
- [ ] PII in logs redacted (email, phone, resume content)
- [ ] Dependency vulnerability scanning: npm audit, Snyk in CI

---

## 13. Deployment Guide (AWS)

### Infrastructure (Terraform recommended)

```
VPC
├── Public Subnet
│   ├── Application Load Balancer
│   └── NAT Gateway
└── Private Subnet
    ├── EC2 Auto Scaling Group (Node.js services)
    ├── EC2 (Python AI microservice)
    ├── RDS PostgreSQL (Multi-AZ)
    ├── ElastiCache Redis Cluster
    └── S3 Bucket (resumes + media)

CloudFront CDN → S3 (static assets)
SES → Email notifications
SNS + FCM → Push notifications
ECR → Docker image registry
```

### Environment Variables

```env
# Core
NODE_ENV=production
PORT=3000
DATABASE_URL=postgresql://user:pass@rds-endpoint:5432/employai
REDIS_URL=redis://elasticache-endpoint:6379
JWT_SECRET=<256-bit random>
JWT_REFRESH_SECRET=<256-bit random>

# AWS
AWS_REGION=ap-south-1
AWS_ACCESS_KEY_ID=<key>
AWS_SECRET_ACCESS_KEY=<secret>
S3_BUCKET_NAME=employai-files
SES_FROM_EMAIL=no-reply@employai.connect

# AI
GEMINI_API_KEY=<key>
AI_SERVICE_URL=http://ai-service:8000

# Firebase
FIREBASE_PROJECT_ID=<id>
FIREBASE_PRIVATE_KEY=<key>

# OAuth
GOOGLE_CLIENT_ID=<id>
GOOGLE_CLIENT_SECRET=<secret>
```

### CI/CD Pipeline (GitHub Actions)

```yaml
# On PR to main:
  - Lint (ESLint + Kotlin lint)
  - Unit tests
  - Integration tests
  - Security scan (npm audit + Snyk)
  - Build Docker image

# On merge to main:
  - All above
  - Deploy to Staging (ECS)
  - Run E2E tests against Staging
  - Manual approval gate

# On tag vX.Y.Z:
  - Deploy to Production (ECS Blue/Green)
  - Smoke tests
  - Rollback trigger if error rate > 1%
```

---

## Quick Start (Local Development)

```bash
# 1. Clone repo
git clone https://github.com/kriss2012/KiriApp.git -b v2
cd KiriApp

# 2. Backend setup
cd backend
cp .env.example .env   # fill in values
docker-compose up -d   # starts PostgreSQL + Redis locally
npm install
npx prisma migrate dev
npm run dev

# 3. AI service setup
cd ../ai-service
python -m venv venv && source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000

# 4. Android app
# Open /android in Android Studio
# Set BASE_URL in local.properties to http://10.0.2.2:3000/v1
# Run on emulator or device
```

---

*Document version: 1.0 | June 2026 | EmployAI Connect — Kiri Squad*
