CREATE TYPE state AS ENUM ('STOPPED', 'STARTED', 'STARTS');
ALTER TABLE applications
ADD app_state state DEFAULT 'STOPPED';
ALTER TABLE applications
ADD server_url TEXT REFERENCES servers(server_url);