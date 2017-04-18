
create table ORDER_TYPE
(
  id int not null auto_increment
    primary key,
  name varchar(10) not null,
  constraint order_type_name_uindex
  unique (name)
)
;

INSERT INTO ORDER_TYPE(id, name) VALUES (1, 'SELL'), (2, 'BUY');

create table CURRENCY_PAIR_LIMIT
(
  id int not null auto_increment
    primary key,
  currency_pair_id int not null,
  user_role_id int not null,
  order_type_id int not null,
  min_rate decimal(40,9) not null DEFAULT 0,
  max_rate decimal(40,9) not null DEFAULT 99999999999,
  constraint currency_pair_limit__uq_index
  unique (currency_pair_id, user_role_id, order_type_id),
  constraint currency_pair_limit___fk_cur_pair
  foreign key (currency_pair_id) references CURRENCY_PAIR (id),
  constraint currency_pair_limit___fk_role
  foreign key (user_role_id) references USER_ROLE (id),
  constraint currency_pair_limit___fk_ord_type
  foreign key (order_type_id) references ORDER_TYPE (id)
)
;

create index currency_pair_limit___fk_role
  on CURRENCY_PAIR_LIMIT (user_role_id)
;

create index currency_pair_limit___fk_ord_type
  on CURRENCY_PAIR_LIMIT (order_type_id)
;

CREATE INDEX currency_pair_limit__index_user_role_order_type ON CURRENCY_PAIR_LIMIT (user_role_id, order_type_id);


INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)
  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP
  JOIN USER_ROLE UR
  JOIN ORDER_TYPE OT;
