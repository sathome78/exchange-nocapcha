ALTER TABLE `birzha`.`WITHDRAW_REQUEST`
ADD COLUMN `merchant_image_id` INT(11) NULL DEFAULT NULL AFTER `processed_by`,
ADD INDEX `withdraw_request_ibfk_3_idx` (`merchant_image_id` ASC);
ALTER TABLE `birzha`.`withdraw_request`
ADD CONSTRAINT `withdraw_request_ibfk_3`
  FOREIGN KEY (`merchant_image_id`)
  REFERENCES `birzha`.`merchant_image` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;



INSERT INTO DATABASE_PATCH VALUES('patch_53_added_imageMerchantId_column_in_withdraw',default,1);