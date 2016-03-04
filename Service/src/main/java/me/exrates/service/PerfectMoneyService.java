package me.exrates.service;

import me.exrates.model.Payment;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PerfectMoneyService {

    Optional<Map<String,String>> preparePayment(Payment payment,Principal principal);
}