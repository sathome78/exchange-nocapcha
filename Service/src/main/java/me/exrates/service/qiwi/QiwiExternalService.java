package me.exrates.service.qiwi;

import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;

public interface QiwiExternalService {
    String generateUniqMemo(int userId);

    QiwiResponseTransaction[] getLastTransactions();

}
