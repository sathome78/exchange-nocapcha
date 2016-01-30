package me.exrates.dao.impl;

import me.exrates.dao.CompanyAccountDao;
import me.exrates.model.CompanyAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class CompanyAccountDaoImpl implements CompanyAccountDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean create(CompanyAccount companyAccount) {
        final String sql = "INSERT INTO COMPANY_ACCOUNT (wallet_id, amount, transaction_type, date, commission_id) " +
                "VALUES (:walletId, :amount, :transactionType, :date, :commission_id)";
        final Map<String,String> params = new HashMap<String,String>(){
            {
                put("walletId",String.valueOf(companyAccount.getWalletId()));
                put("amount",String.valueOf(companyAccount.getAmount()));
                put("transactionType",String.valueOf(companyAccount.getTransactionType().operation));
                put("date",Timestamp.valueOf(companyAccount.getDate()).toString());
                put("commission_id",String.valueOf(companyAccount.getCommissionId()));
            }
        };
        return new NamedParameterJdbcTemplate(dataSource).update(sql,params)>0;
    }
}