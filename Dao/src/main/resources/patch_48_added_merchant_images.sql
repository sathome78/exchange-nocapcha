CREATE TABLE `birzha`.`MERCHANT_IMAGE` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `merchant_id` INT NOT NULL,
  `image_path` VARCHAR(300) NOT NULL,
  `image_name` VARCHAR(45) NOT NULL,
  `currency_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_MERCHANT_id_idx` (`merchant_id` ASC),
  CONSTRAINT `fk3_MERCHANT_id`
    FOREIGN KEY (`merchant_id`)
    REFERENCES `birzha`.`MERCHANT` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);




INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Yandex kassa')
, '/client/img/merchants/visa.png', 'Yandex kassa', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Yandex.Money')
, '/client/img/merchants/yandexmoney.png', 'Yandex.Money', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/ediniykoshelek.png', 'Ediniy koshelek', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/qiwi_wallet.png', 'Qiwi', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/beeline.png', 'Beeline', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/advcash.png', 'Advcash', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/megafon.png', 'Megafon', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/btc_e.png', 'Btc-e', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/mts.png', 'Mts', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/exmo.png', 'Exmo', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/okpay.png', 'Okpay', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/sotoviymir.png', 'Sotoviy mir', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/forwardmobile.png', 'Forward mobile', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/evroset.png', 'Evroset', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/svyaznoy.png', 'Svyaznoy', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/payeer.png', 'Payeer', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/cifrograd.png', 'Cifrograd', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/salonisvyazi.png', 'Saloni svyazi', (SELECT id from CURRENCY WHERE name='RUB'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Perfect Money')
, '/client/img/merchants/perfectmoney.png', 'Perfect Money', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Advcash Money')
, '/client/img/merchants/advcash.png', 'Advcash Money', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Nix Money')
, '/client/img/merchants/nixmoney.png', 'Nix Money', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/ediniykoshelek.png', 'Ediniy koshelek', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/qiwi_wallet.png', 'Qiwi', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/beeline.png', 'Beeline', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/megafon.png', 'Megafon', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/btc_e.png', 'Btc-e', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/mts.png', 'Mts', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/exmo.png', 'Exmo', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/okpay.png', 'Okpay', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/sotoviymir.png', 'Sotoviy mir', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/forwardmobile.png', 'Forward mobile', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/evroset.png', 'Evroset', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/svyaznoy.png', 'Svyaznoy', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/payeer.png', 'Payeer', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/cifrograd.png', 'Cifrograd', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/salonisvyazi.png', 'Saloni svyazi', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Perfect Money')
, '/client/img/merchants/perfectmoney.png', 'Perfect Money', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Advcash Money')
, '/client/img/merchants/advcash.png', 'Advcash Money', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Nix Money')
, '/client/img/merchants/nixmoney.png', 'Nix Money', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/ediniykoshelek.png', 'Ediniy koshelek', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/qiwi_wallet.png', 'Qiwi', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/beeline.png', 'Beeline', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/megafon.png', 'Megafon', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/btc_e.png', 'Btc-e', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/mts.png', 'Mts', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/exmo.png', 'Exmo', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/okpay.png', 'Okpay', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/sotoviymir.png', 'Sotoviy mir', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/forwardmobile.png', 'Forward mobile', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/evroset.png', 'Evroset', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/svyaznoy.png', 'Svyaznoy', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/payeer.png', 'Payeer', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/cifrograd.png', 'Cifrograd', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/salonisvyazi.png', 'Saloni svyazi', (SELECT id from CURRENCY WHERE name='EUR'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='EDR Coin')
, '/client/img/merchants/edrcoin.png', 'EDR Coin', (SELECT id from CURRENCY WHERE name='EDRC'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='LiqPay')
, '/client/img/merchants/liqpay.png', 'LiqPay', (SELECT id from CURRENCY WHERE name='UAH'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Privat24')
, '/client/img/merchants/privat24.png', 'Privat24', (SELECT id from CURRENCY WHERE name='UAH'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/interkassa.png', 'Interkassa', (SELECT id from CURRENCY WHERE name='USD'));
INSERT INTO `birzha`.`MERCHANT_IMAGE` (`merchant_id`, `image_path`, `image_name`, `currency_id`) VALUES ((SELECT id from MERCHANT WHERE name='Interkassa')
, '/client/img/merchants/interkassa.png', 'Interkassa', (SELECT id from CURRENCY WHERE name='EUR'));



INSERT INTO DATABASE_PATCH VALUES('patch_48_added_merchant_images',default,1);