INSERT INTO `users`
(`id`,
 `is_moderator`,
 `reg_time`,
 `name`,
 `email`,
 `password`,
 `code`,
 `photo`)
VALUES (1,
        1,
        DATE('2021-11-14 06:40:26'),
        'Sergey',
        'world_org@gmail.com',
        'qwerty',
        null,
        null),
       (2,
        1,
        DATE('2021-04-14 06:40:26'),
        'Mariya',
        'maria_org@gmail.com',
        'ytrewq',
        null,
        null),
       (3,
        0,
        DATE('2018-11-14 06:40:26'),
        'Makar',
        'makar_org@gmail.com',
        'psw',
        null,
        null);

INSERT INTO `posts`
(`id`,
 `is_active`,
 `moderation_status`,
 `moderator_id`,
 `user_id`,
 `time`,
 `title`,
 `text`,
 `view_count`)
VALUES (1,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-14 06:40:26'),
        'Серверная моей мечты',
        'Многие из вас уже были в серверной, заглядывали туда краем глаза или в крайнем случае видели в фильмах. В кино зачастую серверная выглядит, как кабина пилота космического челнока, но в жизни всё намного прозаичней – под IT-инфраструктуру переоборудуют старую кладовку, ставят туда пару стоек и кондиционер. Но по уму надо проектировать комнату или строить отдельное здание.',
        1),
       (2,
        1,
        'ACCEPTED',
        3,
        2,
        DATE('2021-11-15 06:40:26'),
        'Подробное руководство по инверсии зависимостей. Часть 2. Application модуль',
        'Я, Mariya, приветствую вас и приглашаю продолжить обсуждение темы инверсии зависимостей. В рамках данной финальной части будет рассмотрен application модуль. Будут определена его ответственность и будет рассмотрено то, как он взаимодействует со всеми остальными модулями.Примеры, как и раньше, приведены на языке программирования java, но используются исключительно простые конструкции, чтобы любой читатель, понимающий на самом базовом уровне синтаксис java, смог понять данную статью.',
        1),
       (3,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-19 06:40:26'),
        'Как оптимизировать проект Data Science с помощью Prefect',
        'Почему вас как специалиста по анализу данных должна волновать оптимизация рабочего процесса? Начнём с примера базового проекта в области науки о данных. Представьте, что вы работаете с набором данных Iris и начали с функций обработки данных.',
        1),
       (4,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-14 06:40:26'),
        'Тест 4',
        'Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4Тест 4',
        1),
       (5,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-14 06:40:26'),
        'Тест 5',
        'Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5Тест 5',
        1),
       (6,
        1,
        'ACCEPTED',
        3,
        2,
        DATE('2021-11-16 06:40:26'),
        'Тест 6',
        'Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6Тест 6',
        1),
       (7,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-18 06:40:26'),
        'Тест 7',
        'Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7Тест 7',
        1),
       (8,
        0,
        'NEW',
        3,
        2,
        DATE('2021-11-23 06:40:26'),
        'Тест 8',
        'Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8Тест 8',
        0),
       (9,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-22 06:40:26'),
        'Тест 9',
        'Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9Тест 9',
        1),
       (10,
        1,
        'ACCEPTED',
        3,
        2,
        DATE('2021-11-22 06:40:26'),
        'Тест 10',
        'Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10Тест 10',
        1),
       (11,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-14 06:40:26'),
        'Тест 11',
        'Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11Тест 11',
        1),
       (12,
        1,
        'ACCEPTED',
        3,
        1,
        DATE('2021-11-16 06:40:26'),
        'Тест 12',
        'Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12Тест 12',
        1);

INSERT INTO `post_votes`
(`id`,
 `user_id`,
 `post_id`,
 `time`,
 `value`)
VALUES (1,
        1,
        3,
        DATE('2021-11-16 06:40:26'),
        1),
       (2,
        2,
        8,
        DATE('2021-11-17 06:40:26'),
        -1),
       (3,
        2,
        4,
        DATE('2021-11-18 06:40:26'),
        1),
       (4,
        1,
        5,
        DATE('2021-11-18 06:40:26'),
        1),
       (5,
        2,
        5,
        DATE('2021-11-18 06:40:26'),
        1),
       (6,
        1,
        9,
        DATE('2021-11-18 06:40:26'),
        1),
       (7,
        2,
        3,
        DATE('2021-11-20 06:40:26'),
        -1);

INSERT INTO `tags`
(`id`,
 `name`)
VALUES (1,
        'JAVA'),
       (2,
        'SQL'),
       (3,
        'Basic'),
       (4,
        'HIBERNATE'),
       (5,
        'DataScience');

INSERT INTO `post_comments`
(`id`,
 `parent_id`,
 `post_id`,
 `user_id`,
 `time`,
 `text`)
VALUES (1,
        null,
        1,
        2,
        DATE('2021-11-20 06:40:26'),
        'Ничего непонятно, но очень интересно'),
       (2,
        null,
        1,
        2,
        DATE('2021-11-21 06:40:26'),
        'Интересно....)'),
       (3,
        1,
        1,
        1,
        DATE('2021-11-21 06:40:26'),
        'Да уж'),
       (4,
        null,
        5,
        2,
        DATE('2021-11-21 06:40:26'),
        'Тест Тест Тест тест');