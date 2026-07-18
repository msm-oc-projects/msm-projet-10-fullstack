CREATE TABLE chat_message (
    id UUID PRIMARY KEY,
    client_message_id UUID NOT NULL UNIQUE,
    room_id VARCHAR(50) NOT NULL,
    author VARCHAR(40) NOT NULL,
    content VARCHAR(500) NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chat_message_author_not_blank CHECK (length(trim(author)) >= 2),
    CONSTRAINT chat_message_content_not_blank CHECK (length(trim(content)) >= 1)
);

CREATE INDEX idx_chat_message_room_sent_at
    ON chat_message (room_id, sent_at DESC);
