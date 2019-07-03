package me.exrates.dao;

import me.exrates.model.QuberaUserData;
import me.exrates.model.dto.qubera.QuberaRequestDto;

import java.util.Map;

public interface QuberaDao {

    Integer findUserIdByAccountNumber(String accountNumber);

    boolean logResponse(QuberaRequestDto requestDto);

    boolean saveUserDetails(QuberaUserData userData);

    Map<String, String> getUserDetailsForCurrency(int userId, int currencyId);

    boolean existAccountByUserEmailAndCurrencyName(String email, String currency);

    String getAccountByUserEmail(String email);
}
