package me.exrates.dao;

import me.exrates.model.PagingData;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;

import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface WithdrawRequestDao {

    void create(WithdrawRequest withdrawRequest);

    void delete(WithdrawRequest withdrawRequest);

    void update(WithdrawRequest withdrawRequest);

    Optional<WithdrawRequest> findByIdAndBlock(int id);

    Optional<WithdrawRequest> findById(int id);

    List<WithdrawRequest> findAll();

    PagingData<List<WithdrawRequest>> findByStatus(Integer requestStatus, Integer currentUserId, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData);
}
