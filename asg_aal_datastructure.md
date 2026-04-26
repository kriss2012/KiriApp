# APEX STARTUP GROUP (ASG) & APEX AI Launchpad (AAL)
## Database Architecture & Data Structure Documentation

**Stack:** React (Frontend), Node.js (Backend), MySQL (Database)
**Last Updated:** 2026-04-22

This document outlines the highly normalized relational database schema required to power the ASG community platform and the AAL builder-centric internship program. This structure seamlessly handles the overlap between multiple user roles (e.g., a student who is both R1 and R2, or a Faculty member who is both R5 and an Investor) while providing dedicated touchpoints for the AI Agent to read inputs and write matches.

---

## 1. Core Users & Roles

Instead of creating disparate tables for Founders, Students, and Mentors, we utilize a single `Users` table coupled with a `Stakeholder_Roles` mapping table. This allows maximum flexibility for users with hybrid identities within the ecosystem.

### `Users`
The base identity table for every individual on the platform.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `user_id` | INT | PK, Auto Increment | Unique ID |
| `full_name` | VARCHAR(255) | NOT NULL | User's full name |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Contact & Login |
| `phone` | VARCHAR(20) | UNIQUE | Contact |
| `password_hash` | VARCHAR(255) | NOT NULL | Encrypted password |
| `user_category` | ENUM | NOT NULL | 'STUDENT', 'NON_STUDENT', 'ALUMNI', 'FACULTY' |
| `digital_persona` | TEXT | NULL | JSON/Text summary generated and updated by the AI Agent |
| `created_at` | TIMESTAMP | DEFAULT CURRENT | Auto timestamp |

### `Stakeholder_Roles`
Defines the functional ecosystem roles the user fulfills.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `role_mapping_id` | INT | PK, Auto Increment | Unique ID |
| `user_id` | INT | FK (Users) | The associated user |
| `role_name` | ENUM | NOT NULL | 'FOUNDER', 'SERVICE_PROVIDER', 'MENTOR', 'INVESTOR', 'INCUBATOR', 'GUEST' |

---

## 2. Institutions, Committees & Repositories (R1-R5)

These tables define the decentralized college-level hierarchies that feed into the broader district-level ASG initiatives.

### `Institutions`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `institution_id` | INT | PK, Auto Increment | Unique ID |
| `name` | VARCHAR(255) | NOT NULL | College/Institute Name |
| `spoc_user_id` | INT | FK (Users) | The Single Point of Contact (SPOC) |

### `Institutional_Committees`
Tracks faculty and student representatives for each department.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `committee_id` | INT | PK, Auto Increment | Unique ID |
| `institution_id` | INT | FK (Institutions) | Associated College ID |
| `user_id` | INT | FK (Users) | Committee Member ID |
| `department` | VARCHAR(100) | NOT NULL | e.g., 'Computer Science', 'Mechanical' |
| `role_type` | ENUM | NOT NULL | 'FACULTY_REP', 'STUDENT_REP' |

### `User_Repositories`
Allows a single student/user to be tagged in multiple institutional talent pools (e.g., both R1 and R3).
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `repo_mapping_id` | INT | PK, Auto Increment | Unique ID |
| `user_id` | INT | FK (Users) | The student/alumni/faculty |
| `institution_id` | INT | FK (Institutions) | Associated college |
| `repo_category` | ENUM | NOT NULL | 'R1', 'R2', 'R3', 'R4', 'R5' |
| `approval_status` | ENUM | DEFAULT 'PENDING'| 'PENDING', 'APPROVED', 'REJECTED' |
| `approved_by` | INT | FK (Users) | ID of R5 or SPOC who approved the mapping |

---

## 3. Apex AI Launchpad (AAL) & LMS

Tracks the intern's journey from onboarding through the 7 AI activities to final project assignment.

### `AAL_Onboarding`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `aal_id` | INT | PK, Auto Increment | Unique ID |
| `user_id` | INT | FK (Users) | The intern |
| `mindset_score` | JSON | NULL | Serialized questionnaire results |
| `lms_status` | ENUM | DEFAULT 'ENROLLED' | 'ENROLLED', 'COMPLETED' |
| `certificate_url` | VARCHAR(255) | NULL | Link to LMS certificate (S3/Cloud Storage) |
| `interview_status` | ENUM | DEFAULT 'PENDING' | 'PENDING', 'PASSED', 'FAILED' |

### `AAL_Activities`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `activity_id` | INT | PK, Auto Increment | Unique ID |
| `user_id` | INT | FK (Users) | The intern |
| `activity_number` | INT | NOT NULL | Represents activities 1 through 7 |
| `submission_url` | VARCHAR(255) | NOT NULL | Link to submitted work/document |
| `status` | ENUM | DEFAULT 'SUBMITTED'| 'SUBMITTED', 'VERIFIED' |

---

## 4. Events, Hackathons & Workflows

Designed to handle both temporary college hackathons and permanent, recurring ASG monthly meetups.

### `Events`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `event_id` | INT | PK, Auto Increment | Unique ID |
| `title` | VARCHAR(255) | NOT NULL | Event Name |
| `type` | ENUM | NOT NULL | 'MEETUP', 'HACKATHON', 'QUIZ', 'OPEN_DAY' |
| `host_institution_id`| INT | FK (Institutions) | NULL if it is an ASG city-wide event |
| `start_time` | DATETIME | NOT NULL | Event start timestamp |
| `location` | VARCHAR(255) | NOT NULL | Physical or virtual location |
| `qr_base_url` | VARCHAR(255) | NULL | Base string for dynamic QR flows and check-ins |

### `Event_Registrations`
Dynamic forms and waitlisting. Form answers are stored as JSON to accommodate varying question sets per event.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `registration_id` | INT | PK, Auto Increment | Unique ID |
| `event_id` | INT | FK (Events) | Associated Event |
| `user_id` | INT | FK (Users) | Participant |
| `status` | ENUM | DEFAULT 'REGISTERED'| 'REGISTERED', 'WAITLIST', 'ATTENDED' |
| `form_data` | JSON | NULL | Dynamic registration form answers |
| `qr_scanned_at` | DATETIME | NULL | Timestamp of physical QR entry |

### `Event_Teams`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `team_id` | INT | PK, Auto Increment | Unique ID |
| `event_id` | INT | FK (Events) | Associated Event |
| `team_name` | VARCHAR(255) | NOT NULL | Name of the team |
| `ai_generated` | BOOLEAN | DEFAULT FALSE | TRUE if the AI agent mixed-and-matched this team |

### `Event_Submissions_AntiCheat`
Used for checkpoints, NAAC data proofs, and AI anti-cheating analysis.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `submission_id` | INT | PK, Auto Increment | Unique ID |
| `event_id` | INT | FK (Events) | Associated Event |
| `team_id` | INT | FK (Event_Teams) | NULL if it is an individual submission |
| `document_url` | VARCHAR(255) | NOT NULL | The submission file/link |
| `geo_lat` | DECIMAL(10,8)| NULL | Latitude for NAAC geo-tagging |
| `geo_long` | DECIMAL(11,8)| NULL | Longitude for NAAC geo-tagging |
| `ai_authenticity_score`| FLOAT | NULL | Anti-cheat rating (e.g., 0.0 to 10.0) |

### `Judging_Portal`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `judge_record_id` | INT | PK, Auto Increment | Unique ID |
| `event_id` | INT | FK (Events) | Associated Event |
| `team_id` | INT | FK (Event_Teams) | Evaluated Team |
| `judge_user_id` | INT | FK (Users) | NULL if evaluated by AI Judge |
| `is_ai_judge` | BOOLEAN | DEFAULT FALSE | TRUE if scored by AI |
| `score` | FLOAT | NOT NULL | Evaluation score |
| `feedback` | TEXT | NULL | Qualitative feedback/notes |

---

## 5. The AI Core (Live Inputs & Mapping)

This section represents the system's "Brain," storing raw inputs from stakeholders and the resulting AI-generated connections.

### `Live_Inputs`
The core data feed that the AI reads to build and refine user personas.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `input_id` | INT | PK, Auto Increment | Unique ID |
| `user_id` | INT | FK (Users) | Who provided the input |
| `format_type` | ENUM | NOT NULL | 'TEXT', 'VOICE', 'VIDEO', 'IMAGE' |
| `content_url` | VARCHAR(255) | NOT NULL | Cloud link or raw text snippet |
| `context` | ENUM | NOT NULL | 'DAY_TO_DAY', 'ACHIEVEMENT', 'FAILURE', 'STUCK', 'ASK_FOR_HELP' |
| `ai_processed` | BOOLEAN | DEFAULT FALSE | Flips to TRUE after AI reads it |

### `AI_Resource_Matches`
The output of the AI agent mapping stakeholders together based on digital personas and needs.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `match_id` | INT | PK, Auto Increment | Unique ID |
| `source_user_id` | INT | FK (Users) | e.g., A Founder asking for help |
| `target_user_id` | INT | FK (Users) | e.g., A Visiting Expert or R4 Alumni |
| `match_reason` | TEXT | NOT NULL | AI's explanation of the synergy |
| `status` | ENUM | DEFAULT 'SUGGESTED'| 'SUGGESTED', 'ACCEPTED', 'REJECTED' |

---

## 6. Ecosystem Dashboard (News, Jobs, Asks)

### `Ecosystem_Board`
A unified table for Wall of Fame, News, Asks, and Products to keep the React frontend feed highly performant.
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `board_id` | INT | PK, Auto Increment | Unique ID |
| `author_user_id` | INT | FK (Users) | Who posted it |
| `post_type` | ENUM | NOT NULL | 'NEWS', 'WALL_OF_FAME', 'ASK', 'PRODUCT', 'ANNOUNCEMENT' |
| `title` | VARCHAR(255) | NOT NULL | Post title |
| `description` | TEXT | NOT NULL | Post body |
| `media_url` | VARCHAR(255) | NULL | Image/Video link |
| `created_at` | TIMESTAMP | DEFAULT CURRENT | - |

### `Jobs_Projects`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `listing_id` | INT | PK, Auto Increment | Unique ID |
| `posted_by` | INT | FK (Users) | Usually Founder or SPOC |
| `type` | ENUM | NOT NULL | 'JOB', 'INTERNSHIP', 'RESEARCH', 'CASE_STUDY' |
| `title` | VARCHAR(255) | NOT NULL | Position/Project Name |
| `description` | TEXT | NOT NULL | Details and requirements |
| `status` | ENUM | DEFAULT 'OPEN' | 'OPEN', 'CLOSED' |

---

## 7. Implementation & Architecture Guidelines

1. **AI Asynchronous Processing:** Because parsing voice/video inputs and recalculating the `digital_persona` is resource-intensive, do not execute this on the main Node.js event loop. 
   *Workflow:* When a user uploads a `Live_Input`, save it to MySQL with `ai_processed = FALSE`. Trigger a background worker (e.g., BullMQ, AWS SQS, or a Python microservice) to run the AI prompt, update the `digital_persona` in the `Users` table, generate any `AI_Resource_Matches`, and finally flip `ai_processed` to `TRUE`.
2. **Dynamic Forms (`JSON` Data Type):** For dynamic event registration, the `form_data` column in `Event_Registrations` utilizes MySQL's native JSON data type. This allows frontend React components to render diverse question types without requiring schema migrations for every new hackathon.
3. **Cross-Platform Matching:**
   The `AI_Resource_Matches` table should trigger push notifications via Firebase Cloud Messaging (FCM) or WebSockets when a match is 'SUGGESTED', bringing users back into the app.
