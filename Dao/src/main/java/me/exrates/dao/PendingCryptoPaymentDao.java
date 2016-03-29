package me.exrates.dao;

import me.exrates.model.BlockchainPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PendingCryptoPaymentDao {

    BlockchainPayment create(BlockchainPayment blockchainPayment);

    BlockchainPayment findByInvoiceId(int invoiceId);

    BlockchainPayment findByAddress(String address);

    boolean delete(int invoiceId);
}