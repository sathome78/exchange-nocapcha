update USER set status='2';

ALTER TABLE `birzha`.`USER` 
CHANGE COLUMN `status` `status` INT(40) NOT NULL DEFAULT 1 ,
ADD INDEX `fk_USER_USER_STATUS1_idx` (`status` ASC);

CREATE TABLE IF NOT EXISTS `birzha`.`USER_STATUS` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

ALTER TABLE USER
ADD CONSTRAINT `fk_USER_USER_STATUS1`
  FOREIGN KEY (`status`)
  REFERENCES `birzha`.`USER_STATUS` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
 update USER set status=2;
 
 INSERT DATABASE_PATCH VALUES('patch_11_modified_user',default,0);
 
 INSERT USER_STATUS VALUES(0,'registered','without email confirmation');
 INSERT USER_STATUS VALUES(0,'activated','with email confirmation');
 INSERT USER_STATUS VALUES(0,'blocked','blocked by admin');

