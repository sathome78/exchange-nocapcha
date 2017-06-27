package me.exrates.dao;

import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;

import java.util.List;
import java.util.Locale;

/**
 * created by ValkSam
 */
public interface InputOutputDao {

  List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
      String email,
      Integer offset,
      Integer limit,
      List<Integer> operationTypeIdList,
      Locale locale);

}
