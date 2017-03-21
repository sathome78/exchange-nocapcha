ALTER TABLE birzha.wallet MODIFY active_balance DOUBLE(40,9) UNSIGNED DEFAULT '0.000000000';
ALTER TABLE birzha.wallet MODIFY reserved_balance DOUBLE(40,9) UNSIGNED DEFAULT '0.000000000';

UPDATE WALLET set active_balance = active_balance - 100000 WHERE id = 4281;