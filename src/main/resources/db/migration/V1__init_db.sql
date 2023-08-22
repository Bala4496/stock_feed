create table users
(
    id         serial primary key,
    username   varchar(64) not null unique,
    password   varchar(64) not null,
    role       varchar(32) not null,
    enabled    boolean     not null default true,
    created_at timestamp   not null default now(),
    updated_at timestamp   not null default now()
);

create table api_keys
(
    id      serial primary key,
    key     varchar(64) not null,
    user_id bigint      not null ,
    deleted boolean     not null,
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

create table companies
(
    id   serial primary key,
    code char(4)      not null unique,
    name varchar(64)  not null unique
);

create table quotes
(
    id         serial primary key,
    company_id bigint                  not null
        constraint quotes_companies_id_fk references companies (id) on delete cascade,
    price      numeric(10, 2)          not null,
    created_at timestamp default now() not null
);
