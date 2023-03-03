delete from users;
delete from categories;
delete from events;
delete from comments;

ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE categories ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE events ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN ID RESTART WITH 1;