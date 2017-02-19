package me.exrates.dao;

import java.util.List;
import java.util.Optional;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PendingPaymentDao {

    void create(PendingPayment pendingPayment);

    List<PendingPayment> findAllByHash(String hash);

    Optional<PendingPayment> findByInvoiceId(Integer invoiceId);

    Optional<PendingPayment> findByAddress(String address);

    void delete(int invoiceId);

    Optional<PendingPayment> findByIdAndBlock(Integer invoiceId);

    void updateAcceptanceStatus(PendingPayment pendingPayment);
}
