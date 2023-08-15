create table users
(
    id         serial       primary key,
    username   varchar(64)  not null unique,
    password   varchar(64)  not null,
    role       varchar(32)  not null,
    enabled    boolean      not null default false,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now()
);

