CREATE TABLE IF NOT EXISTS users
(
email text NOT NULL,
login text NOT NULL,
password text NOT NULL,
config jsonb,
CONSTRAINT unique_logitextn PRIMARY KEY (login),
CONSTRAINT unique_email UNIQUE (email)
);
