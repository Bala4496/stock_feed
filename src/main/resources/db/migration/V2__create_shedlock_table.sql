CREATE TABLE shedlock
(
    name       VARCHAR(64) primary key,
    lock_until TIMESTAMP(3) not null,
    locked_at  TIMESTAMP(3) not null,
    locked_by  VARCHAR(255) not null
);
