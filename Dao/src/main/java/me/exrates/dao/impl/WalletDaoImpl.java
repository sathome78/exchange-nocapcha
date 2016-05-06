package me.exrates.dao.impl;

import me.exrates.dao.WalletDao;
import me.exrates.model.Wallet;
import me.exrates.model.dto.UsersWalletsDto;
import me.exrates.model.dto.UsersWalletsSummaryDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
    private NamedParameterJdbcTemplate jdbcTemplate;

    public BigDecimal getWalletABalance(int walletId) {
        if (walletId == 0) {
            return new BigDecimal(0);
        }
        String sql = "SELECT active_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
    }

    public BigDecimal getWalletRBalance(int walletId) {
        String sql = "SELECT reserved_balance FROM WALLET WHERE id = :walletId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        return jdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
    }

    @Override
    public boolean setWalletABalance(int walletId, BigDecimal newBalance) {
        final String sql = "UPDATE WALLET SET active_balance =:newBalance WHERE id =:walletId";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("newBalance", String.valueOf(newBalance));
                put("walletId", String.valueOf(walletId));
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean setWalletRBalance(int walletId, BigDecimal newBalance) {
        final String sql = "UPDATE WALLET SET reserved_balance =:newBalance WHERE id =:walletId";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("newBalance", String.valueOf(newBalance));
                put("walletId", String.valueOf(walletId));
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
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
    public WalletsForOrderAcceptionDto getWalletsForOrderByOrderId(Integer orderId, Integer userAcceptorId) {
        String sql = "SELECT " +
                " (SELECT currency1_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id) AS currency_base, " +
                " (SELECT currency2_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id) AS currency_convert, " +
                " cw1.id AS company_wallet_currency_base, " +
                " cw2.id AS company_wallet_currency_convert, " +
                " IF (EXORDERS.operation_type_id=4, w1.id, w2.id) AS wallet_in_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.id, w1.id) AS wallet_out_for_creator, " +
                " IF (EXORDERS.operation_type_id=4, w2.reserved_balance, w1.reserved_balance) AS wallet_out_reserv_for_creator, " +
                " IF (EXORDERS.operation_type_id=3, w1a.id, w2a.id) AS wallet_in_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.id, w1a.id) AS wallet_out_for_acceptor, " +
                " IF (EXORDERS.operation_type_id=3, w2a.reserved_balance, w1a.reserved_balance) AS wallet_out_reserv_for_acceptor" +
                " FROM EXORDERS  " +
                " LEFT JOIN COMPANY_WALLET cw1 ON (cw1.currency_id= (SELECT currency1_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id)) " +
                " LEFT JOIN COMPANY_WALLET cw2 ON (cw2.currency_id= (SELECT currency2_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id)) " +
                " LEFT JOIN WALLET w1 ON \t(w1.user_id = EXORDERS.user_id) AND " +
                "             (w1.currency_id= (SELECT currency1_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id)) " +
                " LEFT JOIN WALLET w2 ON \t(w2.user_id = EXORDERS.user_id) AND " +
                "             (w2.currency_id= (SELECT currency2_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id)) " +
                " LEFT JOIN WALLET w1a ON \t(w1a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w1a.currency_id= (SELECT currency1_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id))" +
                " LEFT JOIN WALLET w2a ON \t(w2a.user_id = " + (userAcceptorId == null ? "EXORDERS.user_acceptor_id" : ":user_acceptor_id") + ") AND " +
                "             (w2a.currency_id= (SELECT currency2_id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.id = EXORDERS.currency_pair_id)) " +
                " WHERE (EXORDERS.id = :order_id)";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("order_id", String.valueOf(orderId));
        if (userAcceptorId != null) {
            namedParameters.put("user_acceptor_id", String.valueOf(userAcceptorId));
        }
        return jdbcTemplate.queryForObject(sql, namedParameters, (rs, i) -> {
            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
            walletsForOrderAcceptionDto.setCurrencyBase(rs.getInt("currency_base"));
            walletsForOrderAcceptionDto.setCurrencyConvert(rs.getInt("currency_convert"));
            walletsForOrderAcceptionDto.setCompanyWalletCurrencyBase(rs.getInt("company_wallet_currency_base"));
            walletsForOrderAcceptionDto.setCompanyWalletCurrencyConvert(rs.getInt("company_wallet_currency_convert"));
            walletsForOrderAcceptionDto.setUserCreatorInWalletId(rs.getInt("wallet_in_for_creator"));
            walletsForOrderAcceptionDto.setUserCreatorOutWalletId(rs.getInt("wallet_out_for_creator"));
            walletsForOrderAcceptionDto.setUserCreatorOutWalletReserv(rs.getBigDecimal("wallet_out_reserv_for_creator"));
            walletsForOrderAcceptionDto.setUserAcceptorInWalletId(rs.getInt("wallet_in_for_acceptor"));
            walletsForOrderAcceptionDto.setUserAcceptorOutWalletId(rs.getInt("wallet_out_for_acceptor"));
            walletsForOrderAcceptionDto.setUserAcceptorOutWalletReserv(rs.getBigDecimal("wallet_out_reserv_for_acceptor"));
            /**/
            return walletsForOrderAcceptionDto;
        });
    }

    @Override
    public List<UsersWalletsSummaryDto> getUsersWalletsSummary() {
        String sql = "SELECT CURRENCY.name as currency_name, COUNT(*) as wallets_amount, SUM(WALLET.active_balance) as active_balance, SUM(WALLET.reserved_balance) as reserved_balance " +
                " FROM WALLET " +
                " JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                " GROUP BY CURRENCY.name";
        ArrayList<UsersWalletsSummaryDto> result = (ArrayList<UsersWalletsSummaryDto>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<UsersWalletsSummaryDto>() {
            @Override
            public UsersWalletsSummaryDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UsersWalletsSummaryDto usersWalletsSummaryDto = new UsersWalletsSummaryDto();
                usersWalletsSummaryDto.setCurrencyName(rs.getString("currency_name"));
                usersWalletsSummaryDto.setWalletsAmount(rs.getInt("wallets_amount"));
                usersWalletsSummaryDto.setActiveBalance(rs.getBigDecimal("active_balance"));
                usersWalletsSummaryDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                usersWalletsSummaryDto.setActiveBalancePerWallet(BigDecimalProcessing.doAction(usersWalletsSummaryDto.getActiveBalance(), BigDecimal.valueOf(usersWalletsSummaryDto.getWalletsAmount()), ActionType.DEVIDE));
                usersWalletsSummaryDto.setReservedBalancePerWallet(BigDecimalProcessing.doAction(usersWalletsSummaryDto.getReservedBalance(), BigDecimal.valueOf(usersWalletsSummaryDto.getWalletsAmount()), ActionType.DEVIDE));
                return usersWalletsSummaryDto;
            }
        });
        return result;
    }

    @Override
    public List<UsersWalletsDto> getUsersWalletsList() {
        String sql = "SELECT USER.nickname as user_nickname, USER.email as user_email, CURRENCY.name as currency_name, SUM(WALLET.active_balance) as active_balance, SUM(WALLET.reserved_balance) as reserved_balance " +
                " FROM WALLET " +
                " JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
                " JOIN USER ON (USER.id = WALLET.user_id) " +
                " GROUP BY USER.nickname, USER.email, CURRENCY.name";
        ArrayList<UsersWalletsDto> result = (ArrayList<UsersWalletsDto>) jdbcTemplate.query(sql, new BeanPropertyRowMapper<UsersWalletsDto>() {
            @Override
            public UsersWalletsDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UsersWalletsDto usersWalletsDto = new UsersWalletsDto();
                usersWalletsDto.setCurrencyName(rs.getString("currency_name"));
                usersWalletsDto.setUserNickname(rs.getString("user_nickname"));
                usersWalletsDto.setUserEmail(rs.getString("user_email"));
                usersWalletsDto.setActiveBalance(rs.getBigDecimal("active_balance"));
                usersWalletsDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
                return usersWalletsDto;
            }
        });
        return result;
    }
}