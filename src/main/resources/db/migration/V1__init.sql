CREATE TABLE IF NOT EXISTS users
(
email TEXT UNIQUE NOT NULL,
login TEXT PRIMARY KEY NOT NULL,
password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS applications
(
app_name TEXT PRIMARY KEY NOT NULL,
creator_login TEXT REFERENCES users(login),
config jsonb
);
