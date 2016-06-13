package me.exrates.dao;

import me.exrates.model.Wallet;
import me.exrates.model.dto.MyWalletsDetailedDto;
import me.exrates.model.dto.MyWalletsStatisticsDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.WalletOperationData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface WalletDao {

    BigDecimal getWalletABalance(int walletId);

    BigDecimal getWalletRBalance(int walletId);

    int getWalletId(int userId, int currencyId);

    int createNewWallet(Wallet wallet);

    int getUserIdFromWallet(int walletId);

    List<Wallet> findAllByUser(int userId);

    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale);

    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email, Locale locale);

    Wallet findByUserAndCurrency(int userId, int currencyId);

    Wallet createWallet(int userId, int currencyId);

    boolean update(Wallet wallet);

    /**
     * Returns dto with:
     * - IDs the currency
     * - IDs the wallets -
     * - balances the wallets of participants the order: user-creator, user-acceptor, company
     * - status the order
     * and blocks the order and the set wallets within current transaction
     *
     * @param orderId
     * @param userAcceptorId
     * @return dto with data
     */
    WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId);

    List<UserWalletSummaryDto> getUsersWalletsSummary();

    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);
}