package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.WalletOperationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WalletDaoImpl implements WalletDao {

    @Autowired
    CommissionDao commissionDao;
    @Autowired
    TransactionDao transactionDao;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BigDecimal getWalletABalance(int walletId) {
        if (walletId == 0) {
            return new BigDecimal(0);
        }
        String sql = "SELECT active_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public BigDecimal getWalletRBalance(int walletId) {
        String sql = "SELECT reserved_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int getWalletId(int userId, int currencyId) {
        String sql = "SELECT id FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("userId", String.valueOf(userId));
        namedParameters.put("currencyId", String.valueOf(currencyId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }


    public int createNewWallet(Wallet wallet) {
        String sql = "INSERT INTO WALLET (currency_id,user_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("currId", wallet.getCurrencyId())
                .addValue("userId", wallet.getUserId())
                .addValue("activeBalance", wallet.getActiveBalance());
        int result = jdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public List<Wallet> findAllByUser(int userId) {
        final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET" +
                "  INNER JOIN CURRENCY On WALLET.currency_id = CURRENCY.id and WALLET.user_id = :userId";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("userId", userId);
            }
        };
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Wallet.class));
    }

    @Override
    public Wallet findByUserAndCurrency(int userId, int currencyId) {
        final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET INNER JOIN CURRENCY On" +
                "  WALLET.currency_id = CURRENCY.id WHERE user_id = :userId and currency_id = :currencyId";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("userId", userId);
                put("currencyId", currencyId);
            }
        };
        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Wallet.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public Wallet createWallet(int userId, int currencyId) {
        final String sql = "INSERT INTO WALLET (currency_id,user_id) VALUES(:currId,:userId)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("currId", currencyId)
                .addValue("userId", userId);
        if (jdbcTemplate.update(sql, parameters, keyHolder) > 0) {
            Wallet wallet = new Wallet();
            wallet.setActiveBalance(BigDecimal.valueOf(0));
            wallet.setReservedBalance(BigDecimal.valueOf(0));
            wallet.setId(keyHolder.getKey().intValue());
            wallet.setCurrencyId(currencyId);
            return wallet;
        }
        return null;
    }

    @Override
    public boolean update(Wallet wallet) {
        final String sql = "UPDATE WALLET SET active_balance = :activeBalance, reserved_balance = :reservedBalance WHERE id = :id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("id", wallet.getId());
                put("activeBalance", wallet.getActiveBalance());
                put("reservedBalance", wallet.getReservedBalance());
            }
        };
        return jdbcTemplate.update(sql, params) == 1;
    }


    public int getUserIdFromWallet(int walletId) {
        final String sql = "SELECT user_id FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
        String sql = "SELECT " +
                " EXORDERS.id AS order_id, " +
                " EXORDERS.status_id AS order_status_id, " +
                " CURRENCY_PAIR.currency1_id AS currency_base, " +
                " CURRENCY_PAIR.currency2_id AS currency_convert, " +
                " cw1.id AS company_wallet_currency_base, " +
                " cw1.balance AS company_wallet_currency_base_balance, " +
                " cw1.commission_balance AS company_wallet_currency_base_commission_balance, " +
                " cw2.id AS company_wallet_currency_convert, " +
                " cw2.balance AS company_wallet_currency_convert_balance, " +
                " cw2.commission_balance AS company_wallet_currency_convert_commission_balance, " +

                " IF (EXORDERS.operation_type_id=4, w1.id, w2.id) AS wallet_in_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w1.active_balance, w2.active_balance) AS wallet_in_active_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w1.reserved_balance, w2.reserved_balance) AS wallet_in_reserved_for_creator, " +

                " IF (EXORDERS.operation_type_id=4, w2.id, w1.id) AS wallet_out_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.active_balance, w1.active_balance) AS wallet_out_active_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.reserved_balance, w1.reserved_balance) AS wallet_out_reserved_for_creator, " +

                " IF (EXORDERS.operation_type_id=3, w1a.id, w2a.id) AS wallet_in_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w1a.active_balance, w2a.active_balance) AS wallet_in_active_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w1a.reserved_balance, w2a.reserved_balance) AS wallet_in_reserved_for_acceptor, " +

                " IF (EXORDERS.operation_type_id=3, w2a.id, w1a.id) AS wallet_out_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.active_balance, w1a.active_balance) AS wallet_out_active_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.reserved_balance, w1a.reserved_balance) AS wallet_out_reserved_for_acceptor" +
                " FROM EXORDERS  " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                " LEFT JOIN COMPANY_WALLET cw1 ON (cw1.currency_id= CURRENCY_PAIR.currency1_id) " +
                " LEFT JOIN COMPANY_WALLET cw2 ON (cw2.currency_id= CURRENCY_PAIR.currency2_id) " +
                " LEFT JOIN WALLET w1 ON  (w1.user_id = EXORDERS.user_id) AND " +
                "             (w1.currency_id= CURRENCY_PAIR.currency1_id) " +
                " LEFT JOIN WALLET w2 ON  (w2.user_id = EXORDERS.user_id) AND " +
                "             (w2.currency_id= CURRENCY_PAIR.currency2_id) " +
                " LEFT JOIN WALLET w1a ON  (w1a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w1a.currency_id= CURRENCY_PAIR.currency1_id)" +
                " LEFT JOIN WALLET w2a ON  (w2a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w2a.currency_id= CURRENCY_PAIR.currency2_id) " +
                " WHERE (EXORDERS.id = :order_id)" +
                " FOR UPDATE "; //FOR UPDATE !Impotant
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("order_id", String.valueOf(orderId));
        if (userAcceptorId != null) {
            namedParameters.put("user_acceptor_id", String.valueOf(userAcceptorId));
        }
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, (rs, i) -> {
                WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
                walletsForOrderAcceptionDto.setOrderId(rs.getInt("order_id"));
                walletsForOrderAcceptionDto.setOrderStatusId(rs.getInt("order_status_id"));
             /**/
                walletsForOrderAcceptionDto.setCurrencyBase(rs.getInt("currency_base"));
                walletsForOrderAcceptionDto.setCurrencyConvert(rs.getInt("currency_convert"));
            /**/
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBase(rs.getInt("company_wallet_currency_base"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBaseBalance(rs.getBigDecimal("company_wallet_currency_base_balance"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyBaseCommissionBalance(rs.getBigDecimal("company_wallet_currency_base_commission_balance"));
            /**/
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvert(rs.getInt("company_wallet_currency_convert"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvertBalance(rs.getBigDecimal("company_wallet_currency_convert_balance"));
                walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvertCommissionBalance(rs.getBigDecimal("company_wallet_currency_convert_commission_balance"));
            /**/
                walletsForOrderAcceptionDto.setUserCreatorInWalletId(rs.getInt("wallet_in_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_creator"));
            /**/
                walletsForOrderAcceptionDto.setUserCreatorOutWalletId(rs.getInt("wallet_out_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_creator"));
                walletsForOrderAcceptionDto.setUserCreatorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_creator"));
            /**/
                walletsForOrderAcceptionDto.setUserAcceptorInWalletId(rs.getInt("wallet_in_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorInWalletActiveBalance(rs.getBigDecimal("wallet_in_active_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorInWalletReservedBalance(rs.getBigDecimal("wallet_in_reserved_for_acceptor"));
            /**/
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletId(rs.getInt("wallet_out_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletActiveBalance(rs.getBigDecimal("wallet_out_active_for_acceptor"));
                walletsForOrderAcceptionDto.setUserAcceptorOutWalletReservedBalance(rs.getBigDecimal("wallet_out_reserved_for_acceptor"));
            /**/
                return walletsForOrderAcceptionDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<UserWalletSummaryDto> getUsersWalletsSummary() {
        String sql = "SELECT CURRENCY.name as currency_name, COUNT(*) as wallets_amount, SUM(WALLET.active_balance) as active_balance, SUM(WALLET.reserved_balance) as reserved_balance " +
                " FROM WALLET " +
                " JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                " GROUP BY CURRENCY.name";
        ArrayList<UserWalletSummaryDto> result = (ArrayList<UserWalletSummaryDto>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserWalletSummaryDto>() {
            @Override
            public UserWalletSummaryDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UserWalletSummaryDto userWalletSummaryDto = new UserWalletSummaryDto();
                userWalletSummaryDto.setCurrencyName(rs.getString("currency_name"));
                userWalletSummaryDto.setWalletsAmount(rs.getInt("wallets_amount"));
                userWalletSummaryDto.setActiveBalance(rs.getBigDecimal("active_balance"));
                userWalletSummaryDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                userWalletSummaryDto.setActiveBalancePerWallet(BigDecimalProcessing.doAction(userWalletSummaryDto.getActiveBalance(), BigDecimal.valueOf(userWalletSummaryDto.getWalletsAmount()), ActionType.DEVIDE));
                userWalletSummaryDto.setReservedBalancePerWallet(BigDecimalProcessing.doAction(userWalletSummaryDto.getReservedBalance(), BigDecimal.valueOf(userWalletSummaryDto.getWalletsAmount()), ActionType.DEVIDE));
                return userWalletSummaryDto;
            }
        });
        return result;
    }

    @Override
    public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId) {
        CompanyWallet companyWallet = new CompanyWallet();
        String sql = "SELECT WALLET.id AS wallet_id, WALLET.currency_id, WALLET.active_balance, WALLET.reserved_balance, " +
                "  COMPANY_WALLET.id AS company_wallet_id, COMPANY_WALLET.currency_id, COMPANY_WALLET.balance, COMPANY_WALLET.commission_balance " +
                "  FROM WALLET " +
                "  JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = WALLET.currency_id) " +
                "  WHERE WALLET.id = :walletId " +
                "  FOR UPDATE"; //FOR UPDATE Important!
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        Wallet wallet = null;
        try {
            wallet = jdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Wallet>() {
                @Override
                public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Wallet result = new Wallet();
                    result.setId(rs.getInt("wallet_id"));
                    result.setCurrencyId(rs.getInt("currency_id"));
                    result.setActiveBalance(rs.getBigDecimal("active_balance"));
                    result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                    /**/
                    companyWallet.setId(rs.getInt("company_wallet_id"));
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("currency_id"));
                    companyWallet.setCurrency(currency);
                    companyWallet.setBalance(rs.getBigDecimal("balance"));
                    companyWallet.setCommissionBalance(rs.getBigDecimal("commission_balance"));
                    return result;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return WalletTransferStatus.NOT_FOUND;
        }
        /**/
        BigDecimal newActiveBalance = BigDecimalProcessing.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
        BigDecimal newReservedBalance = BigDecimalProcessing.doAction(wallet.getReservedBalance(), amount, ActionType.SUBTRACT);
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }
        /**/
        sql = "UPDATE WALLET SET active_balance = :active_balance, reserved_balance = :reserved_balance WHERE id =:walletId";
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("active_balance", newActiveBalance);
                put("reserved_balance", newReservedBalance);
                put("walletId", String.valueOf(walletId));
            }
        };
        if (jdbcTemplate.update(sql, params) <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }
        /**/
        Transaction transaction = new Transaction();
        transaction.setOperationType(OperationType.WALLET_INNER_TRANSFER);
        transaction.setUserWallet(wallet);
        transaction.setCompanyWallet(companyWallet);
        transaction.setAmount(amount);
        Commission commission = commissionDao.getCommission(OperationType.WALLET_INNER_TRANSFER);
        transaction.setCommissionAmount(commission.getValue());
        transaction.setCommission(commission);
        transaction.setCurrency(companyWallet.getCurrency());
        transaction.setProvided(true);
        transaction.setActiveBalanceBefore(wallet.getActiveBalance());
        transaction.setReservedBalanceBefore(wallet.getReservedBalance());
        transaction.setCompanyBalanceBefore(companyWallet.getBalance());
        transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
        transaction.setSourceType(sourceType);
        transaction.setSourceId(sourceId);
        try {
            transactionDao.create(transaction);
        } catch (Exception e) {
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        /**/
        return WalletTransferStatus.SUCCESS;
    }


    public WalletTransferStatus walletBalanceChange(WalletOperationData walletOperationData) {
        BigDecimal amount = walletOperationData.getAmount();
        if (walletOperationData.getOperationType() == OperationType.OUTPUT) {
            amount = amount.negate();
        }
        /**/
        CompanyWallet companyWallet = new CompanyWallet();
        String sql = "SELECT WALLET.id AS wallet_id, WALLET.currency_id, WALLET.active_balance, WALLET.reserved_balance, " +
                "  COMPANY_WALLET.id AS company_wallet_id, COMPANY_WALLET.currency_id, COMPANY_WALLET.balance, COMPANY_WALLET.commission_balance " +
                "  FROM WALLET " +
                "  JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = WALLET.currency_id) " +
                "  WHERE WALLET.id = :walletId " +
                "  FOR UPDATE"; //FOR UPDATE Important!
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletOperationData.getWalletId()));
        Wallet wallet = null;
        try {
            wallet = jdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Wallet>() {
                @Override
                public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Wallet result = new Wallet();
                    result.setId(rs.getInt("wallet_id"));
                    result.setCurrencyId(rs.getInt("currency_id"));
                    result.setActiveBalance(rs.getBigDecimal("active_balance"));
                    result.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                    /**/
                    companyWallet.setId(rs.getInt("company_wallet_id"));
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("currency_id"));
                    companyWallet.setCurrency(currency);
                    companyWallet.setBalance(rs.getBigDecimal("balance"));
                    companyWallet.setCommissionBalance(rs.getBigDecimal("commission_balance"));
                    return result;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return WalletTransferStatus.NOT_FOUND;
        }
        /**/
        BigDecimal newActiveBalance;
        BigDecimal newReservedBalance;
        if (walletOperationData.getBalanceType() == WalletOperationData.BalanceType.ACTIVE) {
            newActiveBalance = BigDecimalProcessing.doAction(wallet.getActiveBalance(), amount, ActionType.ADD);
            newReservedBalance = wallet.getReservedBalance();
        } else {
            newActiveBalance = wallet.getActiveBalance();
            newReservedBalance = BigDecimalProcessing.doAction(wallet.getReservedBalance(), amount, ActionType.ADD);
        }
        if (newActiveBalance.compareTo(BigDecimal.ZERO) == -1 || newReservedBalance.compareTo(BigDecimal.ZERO) == -1) {
            return WalletTransferStatus.CAUSED_NEGATIVE_BALANCE;
        }
        /**/
        sql = "UPDATE WALLET SET active_balance = :active_balance, reserved_balance = :reserved_balance WHERE id =:walletId";
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("active_balance", newActiveBalance);
                put("reserved_balance", newReservedBalance);
                put("walletId", String.valueOf(walletOperationData.getWalletId()));
            }
        };
        if (jdbcTemplate.update(sql, params) <= 0) {
            return WalletTransferStatus.WALLET_UPDATE_ERROR;
        }
        /**/
        Transaction transaction = new Transaction();
        transaction.setOperationType(walletOperationData.getOperationType());
        transaction.setUserWallet(wallet);
        transaction.setCompanyWallet(companyWallet);
        transaction.setAmount(walletOperationData.getAmount());
        transaction.setCommissionAmount(walletOperationData.getCommmissionAmount());
        transaction.setCommission(walletOperationData.getCommission());
        transaction.setCurrency(companyWallet.getCurrency());
        transaction.setProvided(true);
        transaction.setActiveBalanceBefore(wallet.getActiveBalance());
        transaction.setReservedBalanceBefore(wallet.getReservedBalance());
        transaction.setCompanyBalanceBefore(companyWallet.getBalance());
        transaction.setCompanyCommissionBalanceBefore(companyWallet.getCommissionBalance());
        transaction.setSourceType(walletOperationData.getSourceType());
        transaction.setSourceId(walletOperationData.getSourceId());
        try {
            transactionDao.create(transaction);
        } catch (Exception e) {
            return WalletTransferStatus.TRANSACTION_CREATION_ERROR;
        }
        /**/
        return WalletTransferStatus.SUCCESS;
    }

}