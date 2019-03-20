package me.exrates.ngDao;

import me.exrates.model.ngModel.UserBalancesDto;

import java.util.List;

public interface BalanceDao {
    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);
}
