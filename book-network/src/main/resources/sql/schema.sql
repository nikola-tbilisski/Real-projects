create table _user (
    id bigint primary key generated by default as identity ,
    firstname varchar(20),
    lastname varchar(30),
    date_of_birth date,
    email varchar(50) unique ,
    password varchar(20),
    created_date timestamp default current_timestamp,
    last_modified_date timestamp default current_timestamp,
    account_locked boolean,
    enabled boolean
)
