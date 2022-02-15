drop table users if exists;

create table users (
  id int auto_increment,
  firstname varchar(20),
  lastname varchar(20),
  employee_id int
);

insert into users (firstname, lastname, employee_id) values('Alex', 'Ljunggren', 100);
insert into users (firstname, lastname, employee_id) values('Christie', 'Ljunggren', 101);
insert into users (firstname, lastname, employee_id) values('Gage', 'Ljunggren', 102);
insert into users (firstname, lastname, employee_id) values('John', 'Doe', 103);
