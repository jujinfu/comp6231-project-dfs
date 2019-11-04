create database dfs;

use dfs;

create table file_dir_info(
    id int primary key auto_increment not null,
    name varchar(2000) not null,
    created_date datetime not null default now(),
    last_modified_date datetime not null default now(),
    is_dir boolean not null default false,
    status varchar(255) not null, -- status should be 'Ready','Locked','Removed'
    status_by_user varchar(255), -- mainly for locked by and removed by
    parent int not null references file_dir_info(id)
);

insert into file_dir_info (name,is_dir,parent) values ('',true,1);