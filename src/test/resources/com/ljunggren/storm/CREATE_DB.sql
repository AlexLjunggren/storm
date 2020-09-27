drop table users if exists;

create table users (
  id int,
  firstname varchar(20),
  lastname varchar(20),
  employee_id int
);

insert into users (id, firstname, lastname, employee_id) values(1, 'Alex', 'Ljunggren', 100);
insert into users (id, firstname, lastname, employee_id) values(2, 'Christie', 'Ljunggren', 101);
insert into users (id, firstname, lastname, employee_id) values(3, 'Gage', 'Ljunggren', 102);
insert into users (id, firstname, lastname, employee_id) values(4, 'John', 'Doe', 103);
