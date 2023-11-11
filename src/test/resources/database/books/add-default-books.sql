INSERT INTO books(id, title, author, isbn, price)
VALUES (1, "Effective Java", "Joshua Bloch", "1234567890", 12.99),
       (2, "Clean Code", "Robert Martin", "1234567891", 45.99),
       (3, "Modern Java in Action", "Raoul-Gabriel Urma", "1234567892", 24.99),
       (4, "Microservices Patterns", "Chris Richardson", "1234567893", 39.99),
       (5, "Java in a Nutshell", "Benjamin Evans", "1234567894", 19.99);

INSERT INTO books_categories(book_id, category_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (2, 1),
       (3, 2),
       (4, 2),
       (5, 1);
