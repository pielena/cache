-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)
/*
create table client
(
    id   bigserial not null primary key,
    name varchar(50)
);

 */

drop table if exists clients;
drop table if exists addresses;
drop table if exists phones;
drop sequence if exists  hibernate_sequence;

-- Для @GeneratedValue(strategy = GenerationType.SEQUENCE)
create sequence hibernate_sequence start with 1 increment by 1;

create table clients
(
    id   bigint not null primary key,
    name varchar(50)
);