package me.exrates.dao;

import me.exrates.model.PagingData;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatAdditionalDataDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceStatus;

import java.util.List;
import java.util.Locale;
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

  List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList);

    Integer findStatusIdByRequestId(Integer withdrawRequestId);

    int create(WithdrawRequestCreateDto withdrawRequest);

  List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
      String email,
      Integer offset,
      Integer limit,
      List<Integer> operationTypeIdList,
      Locale locale);

  void setStatusById(Integer id, InvoiceStatus newStatus);

  Optional<WithdrawRequestFlatDto> getFlatByIdAndBlock(int id);

  Optional<WithdrawRequestFlatDto> getFlatById(int id);

  PagingData<List<WithdrawRequestFlatDto>> getFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData);

  WithdrawRequestFlatAdditionalDataDto getAdditionalDataForId(int id);
}
