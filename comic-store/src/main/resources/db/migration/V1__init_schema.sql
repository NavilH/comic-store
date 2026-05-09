CREATE TABLE authors (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE publishers (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(255)
);

CREATE TABLE comics (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255)   NOT NULL,
    issue_number INTEGER        NOT NULL,
    publisher_id BIGINT         NOT NULL REFERENCES publishers (id),
    genre        VARCHAR(50),
    price        NUMERIC(19, 2) NOT NULL,
    description  TEXT
);

CREATE TABLE comic_authors (
    comic_id  BIGINT NOT NULL REFERENCES comics (id),
    author_id BIGINT NOT NULL REFERENCES authors (id),
    PRIMARY KEY (comic_id, author_id)
);

CREATE TABLE inventory (
    id             BIGSERIAL PRIMARY KEY,
    comic_id       BIGINT  NOT NULL UNIQUE REFERENCES comics (id),
    quantity       INTEGER NOT NULL,
    last_restocked TIMESTAMP
);

CREATE TABLE sales (
    id           BIGSERIAL PRIMARY KEY,
    sale_date    TIMESTAMP      NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL
);

CREATE TABLE sale_items (
    id         BIGSERIAL PRIMARY KEY,
    sale_id    BIGINT         NOT NULL REFERENCES sales (id),
    comic_id   BIGINT         NOT NULL REFERENCES comics (id),
    quantity   INTEGER        NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL
);
