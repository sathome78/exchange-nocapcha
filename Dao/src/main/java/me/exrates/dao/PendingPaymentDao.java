package me.exrates.dao;

import java.util.Optional;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PendingPaymentDao {

    void create(PendingPayment pendingPayment);

    Optional<PendingPayment> findByInvoiceId(int invoiceId);

    Optional<PendingPayment> findByAddress(String address);

    void delete(int invoiceId);
}
