CREATE TABLE author
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(100) UNIQUE NOT NULL,
    biography TEXT NOT NULL
);

CREATE INDEX idx_author_name ON author (name);

CREATE TABLE book
(
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(255) UNIQUE NOT NULL,
    author_id        INTEGER REFERENCES author (id) ON DELETE CASCADE,
    publication_year SMALLINT     NOT NULL,
    isbn             VARCHAR(13) UNIQUE,
    level            VARCHAR(12)  NOT NULL DEFAULT 'BEGINNER',
    description      TEXT NOT NULL,
    available        BOOLEAN               DEFAULT false,
    created_at       timestamptz           DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_book_title ON book (title);
CREATE INDEX idx_book_author_id ON book (author_id);
CREATE INDEX idx_book_isbn ON book (isbn);

CREATE TABLE tag
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL
);

CREATE TABLE book_tags
(
    book_id INTEGER REFERENCES book (id) ON DELETE CASCADE,
    tag_id  INTEGER REFERENCES tag (id) ON DELETE CASCADE,
    UNIQUE (book_id, tag_id)
);

CREATE TABLE user_
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_username ON user_ (username);

CREATE TABLE location
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL
);

CREATE TABLE book_owner
(
    book_id  INTEGER REFERENCES book (id) ON DELETE CASCADE,
    owner_id INTEGER REFERENCES user_ (id) ON DELETE CASCADE ,
    language CHAR(2) NOT NULL DEFAULT 'EN',
    location INTEGER REFERENCES location(id)
);

CREATE INDEX idx_book_owner_id ON book_owner (owner_id);
CREATE INDEX idx_book_owner_book_id ON book_owner (book_id);


CREATE TABLE read_book
(
    user_id   INTEGER REFERENCES user_ (id) ON DELETE CASCADE ,
    book_id   INTEGER REFERENCES book (id),
    read_date DATE NOT NULL DEFAULT CURRENT_DATE,
    rating    SMALLINT NOT NULL,
    PRIMARY KEY (user_id, book_id)
);

CREATE INDEX idx_read_book_user_id ON read_book (user_id);