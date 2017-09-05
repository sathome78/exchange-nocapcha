ALTER TABLE USER_ROLE_SETTINGS ADD considered_for_price_range TINYINT(1) DEFAULT '0' NOT NULL;
ALTER TABLE BOT_LAUNCH_SETTINGS ADD consider_user_orders TINYINT(1) DEFAULT 0 NOT NULL;
ALTER TABLE BOT_LAUNCH_SETTINGS
  MODIFY COLUMN consider_user_orders TINYINT(1) NOT NULL DEFAULT 0 AFTER is_enabled;


create table USER_ADMIN_AUTHORITY_ROLE_APPLICATION
(
  user_id int not null,
  admin_authority_id int not null,
  applied_to_role_id int not null,
  primary key (user_id, admin_authority_id, applied_to_role_id),
  constraint user_admin_authority_role_application___fk_u_id
  foreign key (user_id) references USER (id),
  constraint user_admin_authority_role_application___fk_aaid
  foreign key (admin_authority_id) references ADMIN_AUTHORITY (id),
  constraint user_admin_authority_role_application___fk_role
  foreign key (applied_to_role_id) references USER_ROLE (id)
)
;

create index user_admin_authority_role_application___fk_role
  on USER_ADMIN_AUTHORITY_ROLE_APPLICATION (applied_to_role_id)
;

create index user_admin_authority_role_application___fk_u_id
  on USER_ADMIN_AUTHORITY_ROLE_APPLICATION (user_id)
;

INSERT INTO USER_ADMIN_AUTHORITY_ROLE_APPLICATION SELECT UAA.user_id, UAA.admin_authority_id, UR.id
                                                  FROM USER_ADMIN_AUTHORITY UAA
                                                    JOIN USER_ROLE UR
WHERE UAA.admin_authority_id = 8 AND UAA.enabled = 1;


