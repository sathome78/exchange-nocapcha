package me.exrates.model.enums.invoice;

import me.exrates.model.exceptions.UnsupportedGroupUserRoleNameException;
import me.exrates.model.exceptions.UnsupportedWithdrawRequestTableViewTypeNameException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.WithdrawStatusEnum.*;

/**
 * Created by ValkSam on 21.03.2017.
 */
public enum WithdrawRequestTableViewTypeEnum {

  ALL,
  FOR_WORK(WAITING_MANUAL_POSTING, IN_WORK_OF_ADMIN, WAITING_CONFIRMATION),
  POSTED(POSTED_AUTO, POSTED_MANUAL),
  DECLINED(DECLINED_ADMIN);

  private List<WithdrawStatusEnum> withdrawStatusList = new ArrayList<>();

  WithdrawRequestTableViewTypeEnum(WithdrawStatusEnum ... withdrawStatusEnum) {
    withdrawStatusList.addAll(Arrays.asList(withdrawStatusEnum));
  }

  public List<WithdrawStatusEnum> getWithdrawStatusList() {
    return withdrawStatusList;
  }

  public static WithdrawRequestTableViewTypeEnum convert(String name) {
    return Arrays.stream(WithdrawRequestTableViewTypeEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestTableViewTypeNameException(name));
  }
}
