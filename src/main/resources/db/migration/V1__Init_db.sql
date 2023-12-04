CREATE TABLE author
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(100) UNIQUE NOT NULL,
    biography TEXT
);

CREATE TABLE book
(
    id               SERIAL PRIMARY KEY,
    title            VARCHAR(255) UNIQUE NOT NULL,
    author_id        INTEGER REFERENCES author (id),
    publication_year SMALLINT     NOT NULL,
    isbn             VARCHAR(13) UNIQUE ,
    level            VARCHAR(12)  NOT NULL DEFAULT 'BEGINNER',
    description      TEXT,
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
    book_id INTEGER REFERENCES book (id),
    tag_id  INTEGER REFERENCES tag (id),
    PRIMARY KEY (book_id, tag_id)
);

CREATE TABLE user_
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE location
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(25) UNIQUE NOT NULL
);

CREATE TABLE book_owner
(
    book_id  INTEGER REFERENCES book (id),
    owner_id INTEGER REFERENCES user_ (id),
    language CHAR(2) NOT NULL DEFAULT 'EN',
    location INTEGER REFERENCES location(id)
);

CREATE TABLE read_book
(
    user_id   INTEGER REFERENCES user_ (id),
    book_id   INTEGER REFERENCES book (id),
    read_date DATE NOT NULL DEFAULT CURRENT_DATE,
    rating    SMALLINT,
    PRIMARY KEY (user_id, book_id)
);


