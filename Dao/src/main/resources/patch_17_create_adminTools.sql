ALTER TABLE `birzha`.`USER`
CHANGE COLUMN `phone` `phone` VARCHAR(45) NULL DEFAULT NULL ;
ALTER TABLE `birzha`.`USER_ROLE`
DROP FOREIGN KEY `fk_USER_id`;
ALTER TABLE `birzha`.`USER_ROLE`
DROP COLUMN `user_id`,
DROP INDEX `fk_USER_id_idx`;
DELETE FROM `birzha`.`USER_ROLE`;
INSERT INTO `birzha`.`USER_ROLE` (`id`, `name`) VALUES ('1', 'ADMINISTRATOR');
INSERT INTO `birzha`.`USER_ROLE` (`id`, `name`) VALUES ('2', 'ACCOUNTANT');
INSERT INTO `birzha`.`USER_ROLE` (`id`, `name`) VALUES ('3', 'ADMIN_USER');
INSERT INTO `birzha`.`USER_ROLE` (`id`, `name`) VALUES ('4', 'USER');


ALTER TABLE `birzha`.`USER`
ADD COLUMN `roleid` INT(11) NOT NULL DEFAULT 4 AFTER `ipaddress`,
ADD INDEX `fk_USER_ROLE_id_idx` (`roleid` ASC);
ALTER TABLE `birzha`.`USER`

ADD CONSTRAINT `fk_USER_ROLE_id`
FOREIGN KEY (`roleid`)
REFERENCES `birzha`.`USER_ROLE` (`id`)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

INSERT DATABASE_PATCH VALUES('patch_17_create_adminTools',default,0);