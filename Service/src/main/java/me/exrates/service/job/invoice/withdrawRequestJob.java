package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.WithdrawRequestPostDto;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.WithdrawService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.POST_AUTO;

/**
 * Created by ValkSam
 */
@Service
@Log4j2(topic = "job")
public class withdrawRequestJob {

  @Autowired
  WithdrawService withdrawService;

  @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 1)
  private void setInPostingStatus() throws Exception {
    withdrawService.setAllAvailableInPostingStatus();
  }

  @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 1)
  private void postWithdraw() {
    try {
      InvoiceActionTypeEnum action = POST_AUTO;
      List<InvoiceStatus> candidate = WithdrawStatusEnum.getAvailableForActionStatusesList(action);
      if (candidate.size() != 1) {
        log.fatal("no one or more then one base status for action " + action);
        throw new AssertionError();
      }
      List<WithdrawRequestPostDto> withdrawForPostingList = withdrawService.dirtyReadForPostByStatusList(candidate.get(0));
      for (WithdrawRequestPostDto withdrawRequest : withdrawForPostingList) {
        try {
          withdrawService.autoPostWithdrawalRequest(withdrawRequest);
        } catch (Exception e) {
          log.error(ExceptionUtils.getStackTrace(e));
        }
      }
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }
  }

}



