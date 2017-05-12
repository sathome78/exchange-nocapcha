package me.exrates.dao;

import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.enums.RippleTransactionStatus;

import java.util.List;

/**
 * Created by maks on 12.05.2017.
 */
public interface RippleTransactionDao {

    int createRippleTransaction(RippleTransaction rippleTransaction);

    boolean updateRippleTransaction(RippleTransaction rippleTransaction);

    RippleTransaction getTransactionByHash(String hash, boolean forUpdate);

    RippleTransaction getTransactionById(int id);

    List<RippleTransaction> getTransactionsByStatus(RippleTransactionStatus status);
}
