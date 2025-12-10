-- Seed data migration
-- Insert sample users
INSERT INTO users (username, email, first_name, last_name, created_at, updated_at)
VALUES
    ('system', 'system@localhost', 'System', 'User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('jdoe', 'john.doe@example.com', 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('asmith', 'alice.smith@example.com', 'Alice', 'Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample authors
INSERT INTO authors (first_name, last_name, bio, created_by, updated_by, created_at, updated_at)
VALUES
    ('George', 'Orwell', 'English novelist, essayist, journalist and critic. Best known for his dystopian novel 1984.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('J.K.', 'Rowling', 'British author, best known for writing the Harry Potter fantasy series.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('J.R.R.', 'Tolkien', 'English writer, poet, philologist, and academic, best known as the author of The Hobbit and The Lord of the Rings.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Jane', 'Austen', 'English novelist known primarily for her six major novels, which interpret, critique and comment upon the British landed gentry.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample books
INSERT INTO books (title, isbn, publication_year, created_by, updated_by, created_at, updated_at)
VALUES
    ('1984', '978-0-452-28423-4', 1949, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Animal Farm', '978-0-452-28424-1', 1945, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Harry Potter and the Philosopher''s Stone', '978-0-7475-3269-6', 1997, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Harry Potter and the Chamber of Secrets', '978-0-7475-3849-0', 1998, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('The Hobbit', '978-0-544-10235-0', 1937, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('The Lord of the Rings', '978-0-544-10236-7', 1954, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Pride and Prejudice', '978-0-14-143951-8', 1813, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Sense and Sensibility', '978-0-14-143966-2', 1811, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Link books to authors (many-to-many relationship)
INSERT INTO book_authors (book_id, author_id, created_at)
VALUES
    -- George Orwell books
    ((SELECT id FROM books WHERE isbn = '978-0-452-28423-4'), (SELECT id FROM authors WHERE first_name = 'George' AND last_name = 'Orwell'), CURRENT_TIMESTAMP),
    ((SELECT id FROM books WHERE isbn = '978-0-452-28424-1'), (SELECT id FROM authors WHERE first_name = 'George' AND last_name = 'Orwell'), CURRENT_TIMESTAMP),
    -- J.K. Rowling books
    ((SELECT id FROM books WHERE isbn = '978-0-7475-3269-6'), (SELECT id FROM authors WHERE first_name = 'J.K.' AND last_name = 'Rowling'), CURRENT_TIMESTAMP),
    ((SELECT id FROM books WHERE isbn = '978-0-7475-3849-0'), (SELECT id FROM authors WHERE first_name = 'J.K.' AND last_name = 'Rowling'), CURRENT_TIMESTAMP),
    -- J.R.R. Tolkien books
    ((SELECT id FROM books WHERE isbn = '978-0-544-10235-0'), (SELECT id FROM authors WHERE first_name = 'J.R.R.' AND last_name = 'Tolkien'), CURRENT_TIMESTAMP),
    ((SELECT id FROM books WHERE isbn = '978-0-544-10236-7'), (SELECT id FROM authors WHERE first_name = 'J.R.R.' AND last_name = 'Tolkien'), CURRENT_TIMESTAMP),
    -- Jane Austen books
    ((SELECT id FROM books WHERE isbn = '978-0-14-143951-8'), (SELECT id FROM authors WHERE first_name = 'Jane' AND last_name = 'Austen'), CURRENT_TIMESTAMP),
    ((SELECT id FROM books WHERE isbn = '978-0-14-143966-2'), (SELECT id FROM authors WHERE first_name = 'Jane' AND last_name = 'Austen'), CURRENT_TIMESTAMP);
