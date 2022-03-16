create table country
(
    uuid        varchar(255)  not null primary key,
    name        varchar(512)  null,
    capital     varchar(512) null,
    code        varchar(512) null
);

create table address
(
    uuid        varchar(255)  not null primary key,
    name        varchar(512)  null,
    latitude    decimal(9, 6) null,
    longitude   decimal(9, 6) null,
    country     varchar(255)  null,
    city        varchar(255)  null,
    postcode    varchar(72)   null,
    remark      varchar(255)  null
);

create index address_idx3
    on address (city);

create index address_idx4
    on address (country);

create index aname_idx
    on address (name);