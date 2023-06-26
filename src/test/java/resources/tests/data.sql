INSERT INTO users (name, email) values ('Vasya', 'Vasya@gmail.com');
INSERT INTO users (name, email) values ('Pupkin', 'semyon@gmail.com');
INSERT INTO users (name, email) values ('zalupkin', 'ttt@yandex.ru');

INSERT INTO items (name, description, is_available, owner_id) VALUES
            ('game', 'game zelda', true, 1);
INSERT INTO items (name, description, is_available, owner_id) VALUES
            ('keyboard', 'keyboard', true, 2);

INSERT INTO item_requests (description, requestor_id, date_created) VALUES
            ('i need game', 2, '2023-06-20T15:39:00');
INSERT INTO item_requests (description, requestor_id, date_created) VALUES
            ('i need keyboard', 3, '2023-06-20T17:40:00');

INSERT INTO items (name, description, is_available, owner_id, request_id) VALUES
            ('Учебник', 'учебник по математике', true, 2, 1);

INSERT INTO comments (text, item_id, author_id, created) VALUES
    ('Учебник помог мне сдать егэ', 2, 3, '2023-06-21T15:15:00');