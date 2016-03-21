ALTER TABLE `birzha`.`registration_token`
RENAME TO  `birzha`.`TEMPORAL_TOKEN` ;

CREATE TABLE `birzha`.`TOKEN_TYPE` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

  ALTER TABLE `birzha`.`TEMPORAL_TOKEN`
ADD COLUMN `token_type_id` INT(40) NULL AFTER `date_creation`,
ADD INDEX `fk_TEMPORAL_TOKEN_TYPE_idx` (`token_type_id` ASC);
ALTER TABLE `birzha`.`TEMPORAL_TOKEN`
ADD CONSTRAINT `fk_TEMPORAL_TOKEN_TYPE`
  FOREIGN KEY (`token_type_id`)
  REFERENCES `birzha`.`TOKEN_TYPE` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

INSERT INTO `birzha`.`TOKEN_TYPE` (`id`, `name`) VALUES ('1', 'registration');
INSERT INTO `birzha`.`TOKEN_TYPE` (`id`, `name`) VALUES ('2', 'changePassword');
INSERT INTO `birzha`.`TOKEN_TYPE` (`id`, `name`) VALUES ('3', 'changeFinPassword');