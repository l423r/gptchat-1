CREATE TABLE chat (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL
);

CREATE TABLE message (
                         id BIGSERIAL PRIMARY KEY,
                         chat_id BIGINT NOT NULL,
                         sender VARCHAR(50) NOT NULL,
                         content TEXT NOT NULL,
                         FOREIGN KEY (chat_id) REFERENCES chat(id)
);
