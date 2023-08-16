create table user_keys
(
    id         serial primary key,
    key        varchar(64) not null,
    user_id    bigint      not null,
    expired_at timestamp   not null default now()
);

