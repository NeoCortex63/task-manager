delete from user_role;
delete from users;

insert into users(id, username, password, email, active) values
('1', 'admin', '$2a$10$nM5sBH9w9rYKuIpu/y7SCOS4v2G5vfntXapltjQevub1C7IU12RNG', 'admin@gmail.com', 1),
('2', 'user', '$2a$10$nM5sBH9w9rYKuIpu/y7SCOS4v2G5vfntXapltjQevub1C7IU12RNG', 'user@gmail.com', 1);

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'),
(2, 'USER');