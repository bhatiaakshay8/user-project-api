CREATE TABLE tb_user
(
    id BIGINT NOT NULL IDENTITY,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(129) NOT NULL,
    name VARCHAR(120) NULL ,
    PRIMARY KEY (id),
    CONSTRAINT AK_email UNIQUE(email)
);