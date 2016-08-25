package me.exrates.dao;

import me.exrates.model.EDCAccount;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCAccountDao {

    EDCAccount findByTransactionId(int id);

    void deleteByTransactionId(int id);

    void create(EDCAccount edcAccount);

}
