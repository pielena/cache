create table phones
(
    id   bigint not null primary key,
    number varchar(50),
    client_id bigint REFERENCES clients(id)
);

create table addresses
(
    id   bigint not null primary key,
    street varchar(50)
);

alter table clients add column address_id bigint
    CONSTRAINT fk_address_id REFERENCES addresses(id);