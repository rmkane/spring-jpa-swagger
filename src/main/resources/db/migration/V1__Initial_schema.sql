-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create authors table
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    bio TEXT,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP,
    CONSTRAINT fk_author_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_author_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Create books table
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) UNIQUE,
    publication_year INTEGER,
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP,
    CONSTRAINT fk_book_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_book_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Create book_authors join table
CREATE TABLE book_authors (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_author_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_book_author_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
    CONSTRAINT uk_book_author UNIQUE (book_id, author_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_authors_created_by ON authors(created_by);
CREATE INDEX idx_authors_updated_by ON authors(updated_by);
CREATE INDEX idx_books_created_by ON books(created_by);
CREATE INDEX idx_books_updated_by ON books(updated_by);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_book_authors_book_id ON book_authors(book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors(author_id);
