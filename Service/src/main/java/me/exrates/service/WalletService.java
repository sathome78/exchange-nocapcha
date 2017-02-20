package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface WalletService {

    void balanceRepresentation(Wallet wallet);

    List<Wallet> getAllWallets(int userId);

    /**
     * Return list the user wallets data
     * @param email is email to determine user
     * @return list the user wallets data
     */
    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData, String email, Locale locale);

    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, Locale locale);

    List<Currency> getCurrencyList();

    int getWalletId(int userId, int currencyId);

    BigDecimal getWalletABalance(int walletId);

    BigDecimal getWalletRBalance(int walletId);

    boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck);

    int createNewWallet(Wallet wallet);

    int getUserIdFromWallet(int walletId);

    Wallet findByUserAndCurrency(User user, Currency currency);

    Wallet create(User user, Currency currency);

    void depositActiveBalance(Wallet wallet, BigDecimal sum);

    void withdrawActiveBalance(Wallet wallet, BigDecimal sum);

    void depositReservedBalance(Wallet wallet, BigDecimal sum);

    void withdrawReservedBalance(Wallet wallet, BigDecimal sum);

    /**
     * Returns user's wallets info
     *
     * @return list the UserWalletSummaryDto
     * @author ValkSam
     */
    List<UserWalletSummaryDto> getUsersWalletsSummary(List<Integer> roles);

    /**
     * Transfers money between active balance the wallet and reserved balance the wallet
     * and creates corresponding transaction
     *
     * @param walletId   is wallet ID
     * @param amount     amount to transfer
     * @param sourceType type the operation that caused the transfer
     * @param sourceId   ID the operation in the table that corresponds to sourceType
     * @return WalletTransferStatus with detail about result
     * @author ValkSam
     */
    WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId);

    WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData);

    List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale);

    @Transactional(readOnly = true)
    MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId);

    @Transactional(readOnly = true)
    List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale);

    @Transactional(readOnly = true)
    List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email, Locale locale);

    @Transactional(rollbackFor = Exception.class)
    void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount);

    String transferCostsToUser(Integer fromUserWalletId, String toUserNickname, Integer currencyId, BigDecimal amount, Locale locale);
}