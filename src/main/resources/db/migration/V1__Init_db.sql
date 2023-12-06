CREATE TABLE author
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(100) UNIQUE NOT NULL,
    biography TEXT NOT NULL
);

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

CREATE TABLE tag
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL
);

CREATE TABLE book_tags
(
    book_id INTEGER REFERENCES book (id) ON DELETE CASCADE ,
    tag_id  INTEGER REFERENCES tag (id) ON DELETE CASCADE ,
    PRIMARY KEY (book_id, tag_id)
);

CREATE TABLE user_
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);


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

CREATE TABLE read_book
(
    user_id   INTEGER REFERENCES user_ (id) ON DELETE CASCADE ,
    book_id   INTEGER REFERENCES book (id),
    read_date DATE NOT NULL DEFAULT CURRENT_DATE,
    rating    SMALLINT NOT NULL,
    PRIMARY KEY (user_id, book_id)
);


