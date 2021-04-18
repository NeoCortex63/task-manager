create table tasks (
    id bigint not null auto_increment,
    due_date date not null,
    task_message varchar(255) not null,
    task_status varchar(255),
    user_id bigint,
    primary key (id)
);

create table user_role (
    user_id bigint not null,
    roles varchar(255)
);

create table users (
    id bigint not null auto_increment,
    username varchar(25) not null,
    password varchar(255) not null,
    email varchar(255) not null,
    activation_code varchar(255),
    active bit not null,
    primary key (id)
);

alter table tasks
    add constraint task_user_fk foreign key (user_id) references users (id);

alter table user_role
    add constraint user_role_user_fk foreign key (user_id) references users (id);