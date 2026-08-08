-- Seed the three existing portfolio projects.

INSERT INTO projects (title, role, status, problem, approach, engineering, source_url, display_order)
VALUES
('Bhaabi Gaming Club',
 'Solo Developer',
 'In development',
 'Mainstream mobile card games are effectively unplayable with a screen reader — visual-only state, unlabelled controls, and no audio feedback shut blind players out entirely.',
 'An Android card game platform built accessible-first, supporting Bhaabi, 99, UNO, and Hand Cricket. Every game state is announced through TTS and screen reader support, with online multiplayer and in-game voice chat so play stays social rather than isolated.',
 'Multi-game architecture sharing a common engine, real-time multiplayer state synchronisation over Firebase, and a full accessibility layer covering navigation, turn announcements, and card recognition.',
 'https://github.com/Gurindersingh716',
 1),

('FPMIS — Forensic Post-Mortem Intelligence System',
 'Lead Developer',
 'Shipped',
 'Forensic case work is fragmented across autopsy reporting, evidence handling, and missing-person records — so unidentified bodies and open cases that should connect often never do.',
 'A full-stack forensic case management platform spanning eight integrated modules: case management, digital autopsy reporting, time-of-death estimation, decomposition tracking, evidence handling, and cross-case alerts. Selected as a finalist and presented at HealthHack 2026, VIT Bhopal University.',
 'Led a team of five developers. Designed a 100-point weighted matching algorithm linking unidentified bodies to missing-person records across seven attributes, returning ranked results with confidence tiers. Built secure REST APIs with JWT authentication, role-based access control, and full audit logging over a normalised PostgreSQL schema.',
 NULL,
 2),

('Salesalizer',
 'Backend Developer',
 'Shipped',
 'Retailers plan inventory and staffing against guesswork, because historical sales data sitting in a database says nothing about next month on its own.',
 'An AI-powered sales prediction platform for retailers and small businesses, pairing time-series forecasting with an interactive dashboard for surfacing trends and insights.',
 'Integrated Prophet models for time-series forecasting, reaching up to 95% accuracy on sales prediction. Implemented role-based access control, authentication, and session management across a Node/Express API backed by MongoDB.',
 NULL,
 3);

INSERT INTO project_stack (project_id, tech)
SELECT id, unnest(ARRAY['Java', 'Android', 'Firebase', 'Realtime DB', 'TTS', 'Voice Chat'])
FROM projects WHERE display_order = 1;

INSERT INTO project_stack (project_id, tech)
SELECT id, unnest(ARRAY['FastAPI', 'Python', 'SQLAlchemy', 'PostgreSQL', 'JWT', 'Next.js 14', 'TypeScript', 'Tailwind'])
FROM projects WHERE display_order = 2;

INSERT INTO project_stack (project_id, tech)
SELECT id, unnest(ARRAY['Node.js', 'Express', 'MongoDB', 'React', 'Prophet', 'Nivo Charts'])
FROM projects WHERE display_order = 3;
