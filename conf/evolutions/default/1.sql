# --- First database schema
# --- !Ups
CREATE TABLE user (
id                            INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
secret                        VARCHAR(255) NOT NULL,
token                         VARCHAR(255),
);

CREATE TABLE folder (
id                            INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
text_id                       VARCHAR(255) NOT NULL,
title                         VARCHAR(255) NOT NULL,
uid                           INT NOT NULL,
FOREIGN KEY                   (uid) REFERENCES user(id)
);

CREATE TABLE link(
id                            INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
uid                           INT NOT NULL,
fid                           INT NOT NULL,
url                           VARCHAR(255) NOT NULL,
code                          VARCHAR(255) NOT NULL,
FOREIGN KEY                   (uid) REFERENCES user(id),
FOREIGN KEY                   (fid) REFERENCES folder(id)
);


CREATE TABLE click(
id                            INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
lid                           INT NOT NULL,
date                          DATE NOT NULL,
referrer                      VARCHAR(255) NOT NULL,
remoteIP                      VARCHAR(255) NOT NULL,
FOREIGN KEY                   (lid) REFERENCES link(id)
);


# --- !Downs
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS folder;
DROP TABLE IF EXISTS click;
DROP TABLE IF EXISTS link;
