
UPDATE `birzha`.`MERCHANT_CURRENCY` SET `merchant_commission`='1.990000000' WHERE merchant_id in (select id from MERCHANT where description='Perfect Money');

UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='51' WHERE `id`='1';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='52' WHERE `id`='22';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='1' WHERE `id`='25';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='54' WHERE `id`='32';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='2' WHERE `id`='26';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='56' WHERE `id`='27';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='57' WHERE `id`='28';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='58' WHERE `id`='31';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='59' WHERE `id`='29';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='3' WHERE `id`='12';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='61' WHERE `id`='11';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='62' WHERE `id`='30';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='63' WHERE `id`='21';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='64' WHERE `id`='23';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='65' WHERE `id`='13';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='66' WHERE `id`='3';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='67' WHERE `id`='19';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='68' WHERE `id`='24';
UPDATE `birzha`.`CURRENCY_PAIR` SET `pair_order`='69' WHERE `id`='2';

UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='32';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='31';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='30';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='33';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='34';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='35';
UPDATE `birzha`.`CURRENCY_PAIR` SET `hidden`='1' WHERE `id`='36';


CREATE TABLE `birzha`.`USER_COMMENT` (
  `id` INT(40) NOT NULL AUTO_INCREMENT,
  `user_id` INT(40) NOT NULL,
  `users_comment` VARCHAR(400) NOT NULL,
  `user_creator_id` INT(40) NOT NULL,
  `edit_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `message_sent` TINYINT(1) NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_COMMENT_USER_idx` (`user_id` ASC),
  INDEX `fk_COMMENT_USER_CREATOR_idx` (`user_creator_id` ASC),
  CONSTRAINT `fk_COMMENT_USER`
    FOREIGN KEY (`user_id`)
    REFERENCES `birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_COMMENT_USER_CREATOR`
    FOREIGN KEY (`user_creator_id`)
    REFERENCES `birzha`.`USER` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE `birzha`.`MERCHANT_CURRENCY`
ADD COLUMN `withdraw_block` TINYINT(1) NULL DEFAULT '0' AFTER `merchant_commission`;


INSERT INTO DATABASE_PATCH (version, datetime, patched) VALUES ('patch_73_add_users_comments', DEFAULT, 1);