package me.exrates.dao;

import me.exrates.model.WithdrawRequest;

import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface WithdrawRequestDao {

    void create(WithdrawRequest withdrawRequest);

    void delete(WithdrawRequest withdrawRequest);

    void update(WithdrawRequest withdrawRequest);

    Optional<WithdrawRequest> findById(int id);

    List<WithdrawRequest> findAll();
}
