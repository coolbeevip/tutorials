create table t_customers
(
    id varchar(36) not null,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    age int not null,
    created_at datetime not null,
    last_updated_at datetime not null,
    constraint t_customers_pk primary key (id)
)engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table t_orders_0
(
    id varchar(36) not null,
    order_desc varchar(100) not null,
    customer_id varchar(36) not null,
    total_price decimal not null,
    created_at datetime not null,
    last_updated_at datetime not null,
    constraint t_orders_0_pk primary key (id)
)engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;

create table t_orders_1
(
    id varchar(36) not null,
    order_desc varchar(100) not null,
    customer_id varchar(36) not null,
    total_price decimal not null,
    created_at datetime not null,
    last_updated_at datetime not null,
    constraint t_orders_1_pk primary key (id)
)engine=innodb default charset=utf8mb4 collate=utf8mb4_general_ci;