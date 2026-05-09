-- Publishers
INSERT INTO publishers (id, name, country) VALUES
(1, 'Marvel Comics',   'USA'),
(2, 'DC Comics',       'USA'),
(3, 'Image Comics',    'USA'),
(4, 'Dark Horse',      'USA'),
(5, 'IDW Publishing',  'USA'),
(6, 'Viz Media',       'Japan');

-- Authors
INSERT INTO authors (id, name) VALUES
(1, 'Stan Lee'),
(2, 'Jack Kirby'),
(3, 'Alan Moore'),
(4, 'Neil Gaiman'),
(5, 'Brian K. Vaughan'),
(6, 'Frank Miller'),
(7, 'Todd McFarlane'),
(8, 'Chris Claremont');

-- Comics
INSERT INTO comics (id, title, issue_number, publisher_id, genre, price, description) VALUES
(1,  'The Amazing Spider-Man',          1, 1, 'SUPERHERO', 4.99, 'The first issue of the iconic web-slinger''s solo series.'),
(2,  'X-Men',                           1, 1, 'SUPERHERO', 3.99, 'The original mutant team in their debut adventure.'),
(3,  'The Avengers',                    1, 1, 'ACTION',    3.99, 'Earth''s mightiest heroes unite for the first time.'),
(4,  'Watchmen',                        1, 2, 'ACTION',    5.99, 'A groundbreaking deconstruction of the superhero genre.'),
(5,  'Batman: The Dark Knight Returns', 1, 2, 'SUPERHERO', 4.99, 'An aging Batman returns to fight crime in a dystopian Gotham.'),
(6,  'The Sandman',                     1, 2, 'FANTASY',   4.99, 'Morpheus, the lord of dreams, begins his journey.'),
(7,  'Saga',                            1, 3, 'SCI_FI',    3.99, 'An epic space opera following two soldiers from warring species.'),
(8,  'Spawn',                           1, 3, 'ACTION',    2.99, 'A soldier killed in battle returns as a hellspawn.'),
(9,  'Sin City',                        1, 4, 'ACTION',    3.99, 'A noir crime story set in the corrupt Basin City.'),
(10, 'Daredevil',                       1, 1, 'ACTION',    3.99, 'The Man Without Fear protects Hell''s Kitchen.'),
(11, 'Teenage Mutant Ninja Turtles',    1, 5, 'ACTION',    3.99, 'Four mutant turtles trained as ninjas fight evil.'),
(12, 'Blade of the Immortal',           1, 4, 'ACTION',    3.99, 'An immortal samurai seeks redemption through a blood debt.');

-- Comic-Author relationships
INSERT INTO comic_authors (comic_id, author_id) VALUES
(1,  1), (1,  2),
(2,  8), (2,  2),
(3,  1), (3,  2),
(4,  3),
(5,  6),
(6,  4),
(7,  5),
(8,  7),
(9,  6),
(10, 1), (10, 6),
(11, 5),
(12, 4);

-- Inventory
INSERT INTO inventory (id, comic_id, quantity, last_restocked) VALUES
(1,  1,  10, '2026-04-01 10:00:00'),
(2,  2,  3,  '2026-04-05 10:00:00'),
(3,  3,  8,  '2026-04-02 10:00:00'),
(4,  4,  15, '2026-04-01 10:00:00'),
(5,  5,  4,  '2026-04-10 10:00:00'),
(6,  6,  7,  '2026-04-03 10:00:00'),
(7,  7,  12, '2026-04-01 10:00:00'),
(8,  8,  2,  '2026-04-15 10:00:00'),
(9,  9,  6,  '2026-04-04 10:00:00'),
(10, 10, 9,  '2026-04-02 10:00:00'),
(11, 11, 11, '2026-04-01 10:00:00'),
(12, 12, 1,  '2026-04-20 10:00:00');

-- Sales
INSERT INTO sales (id, sale_date, total_amount) VALUES
(1, '2026-04-20 14:30:00', 15.97),
(2, '2026-04-22 11:15:00', 11.97),
(3, '2026-04-24 16:45:00', 11.97),
(4, '2026-04-26 10:20:00',  9.98),
(5, '2026-04-28 13:00:00', 11.97);

-- Sale Items
INSERT INTO sale_items (id, sale_id, comic_id, quantity, unit_price) VALUES
(1, 1, 1, 2, 4.99),
(2, 1, 4, 1, 5.99),
(3, 2, 7, 3, 3.99),
(4, 3, 6, 1, 5.99),
(5, 3, 8, 2, 2.99),
(6, 4, 5, 2, 4.99),
(7, 5, 2, 1, 3.99),
(8, 5, 3, 1, 3.99),
(9, 5, 10, 1, 3.99);

-- Reset sequences so new inserts don't collide with seeded IDs
SELECT setval('publishers_id_seq',  (SELECT MAX(id) FROM publishers));
SELECT setval('authors_id_seq',     (SELECT MAX(id) FROM authors));
SELECT setval('comics_id_seq',      (SELECT MAX(id) FROM comics));
SELECT setval('inventory_id_seq',   (SELECT MAX(id) FROM inventory));
SELECT setval('sales_id_seq',       (SELECT MAX(id) FROM sales));
SELECT setval('sale_items_id_seq',  (SELECT MAX(id) FROM sale_items));
