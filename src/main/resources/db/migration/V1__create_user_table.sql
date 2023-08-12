create table users
(
    id       bigint      primary key,
    username varchar(64) not null unique,
    password varchar(64) not null
);

create sequence users_seq start with 1 increment by 1;