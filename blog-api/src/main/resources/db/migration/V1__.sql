CREATE TABLE if not exists captcha_codes
(
    id          INT AUTO_INCREMENT NOT NULL,
    time        datetime           NOT NULL,
    code        VARCHAR(255)       NOT NULL,
    secret_code VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_captcha_codes PRIMARY KEY (id)
);

CREATE TABLE if not exists global_settings
(
    id    INT AUTO_INCREMENT NOT NULL,
    code  VARCHAR(255)       NOT NULL,
    name  VARCHAR(255)       NOT NULL,
    value VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_global_settings PRIMARY KEY (id)
);

CREATE TABLE if not exists post_comments
(
    id        INT AUTO_INCREMENT NOT NULL,
    parent_id INT                NULL,
    post_id   INT                NOT NULL,
    user_id   INT                NOT NULL,
    time      datetime           NOT NULL,
    text      VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_post_comments PRIMARY KEY (id)
);

CREATE TABLE if not exists post_votes
(
    id      INT AUTO_INCREMENT NOT NULL,
    user_id INT                NOT NULL,
    post_id INT                NOT NULL,
    time    datetime           NOT NULL,
    value   SMALLINT           NOT NULL,
    CONSTRAINT pk_post_votes PRIMARY KEY (id)
);

CREATE TABLE if not exists posts
(
    id                INT AUTO_INCREMENT NOT NULL,
    is_active         SMALLINT           NOT NULL,
    moderation_status VARCHAR(255)       NOT NULL,
    moderator_id      INT                NULL,
    user_id           INT                NOT NULL,
    time              datetime           NOT NULL,
    title             VARCHAR(255)       NOT NULL,
    text              TEXT               NOT NULL,
    view_count        INT                NOT NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

CREATE TABLE if not exists tag2post
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    CONSTRAINT pk_tag2post PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE if not exists tags
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id)
);

CREATE TABLE if not exists users
(
    id           INT AUTO_INCREMENT NOT NULL,
    is_moderator BIT(1)             NOT NULL,
    reg_time     datetime           NOT NULL,
    name         VARCHAR(255)       NOT NULL,
    email        VARCHAR(255)       NOT NULL,
    password     VARCHAR(255)       NOT NULL,
    code         VARCHAR(255)       NULL,
    photo        VARCHAR(255)       NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);
