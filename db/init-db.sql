create table bank_account
(
    id      serial primary key,
    name    text not null,
    balance int  not null default 0
);

insert into bank_account(name, balance)
values ('John', 100);

insert into bank_account(name, balance)
values ('Anna', 0);
