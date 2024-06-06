CREATE TABLE conversation
(
    id              SERIAL PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    messages        TEXT[]
);
