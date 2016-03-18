package me.exrates.dao;

import me.exrates.model.BlockchainPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PendingBlockchainPaymentDao {

    BlockchainPayment create(BlockchainPayment blockchainPayment);

    BlockchainPayment findByInvoiceId(int invoiceId);

    boolean delete(int invoiceId);
}