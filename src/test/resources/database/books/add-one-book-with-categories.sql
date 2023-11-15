INSERT INTO books(id, title, author, isbn, price)
VALUES (1, "Effective Java", "Joshua Bloch", "1234567890", 12.99);

INSERT INTO books_categories(book_id, category_id)
VALUES (1, 1),
       (1, 2);
