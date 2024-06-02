CREATE TABLE tb_user_external_project
(
    id BIGINT NOT NULL IDENTITY,
    userId BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_userproject FOREIGN KEY (userId) REFERENCES tb_user(id),
    CONSTRAINT AK_userId_name UNIQUE(userId, name)
);