package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCService {

    PendingPayment createInvoice(CreditsOperation operation) throws Exception;
}
