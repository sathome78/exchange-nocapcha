package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.RequestLimitExceededException;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestsAdminTableDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.WithdrawRequestsAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.service.*;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * created by ValkSam
 */
@Controller
public class RefillRequestAdminController {

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private MessageSource messageSource;

  @Autowired
  RefillService refillService;

  @Autowired
  UserService userService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private CurrencyService currencyService;

  @RequestMapping(value = "/2a8fy7b07dxe44/refill")
  public ModelAndView refillRequests(Principal principal) {
    final Map<String, Object> params = new HashMap<>();
    List<UserCurrencyOperationPermissionDto> permittedCurrencies = currencyService.getCurrencyOperationPermittedForRefill(principal.getName())
        .stream().filter(dto -> dto.getInvoiceOperationPermission() != InvoiceOperationPermission.NONE).collect(Collectors.toList());
    params.put("currencies", permittedCurrencies);
    if (!permittedCurrencies.isEmpty()) {
      List<Integer> currencyList = permittedCurrencies.stream()
          .map(UserCurrencyOperationPermissionDto::getCurrencyId)
          .collect(Collectors.toList());
      List<Merchant> merchants = merchantService.getAllUnblockedForOperationTypeByCurrencies(currencyList, OperationType.INPUT)
          .stream()
          .map(item -> new Merchant(item.getMerchantId(), item.getName(), item.getDescription()))
          .distinct()
          .collect(Collectors.toList());
      params.put("merchants", merchants);
    }
    return new ModelAndView("refillRequests", params);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/refillRequests", method = GET)
  @ResponseBody
  public DataTable<List<RefillRequestsAdminTableDto>> findRequestByStatus(
      @RequestParam("viewType") String viewTypeName,
      RefillFilterData refillFilterData,
      @RequestParam Map<String, String> params,
      Principal principal,
      Locale locale) {
    RefillRequestTableViewTypeEnum viewTypeEnum = RefillRequestTableViewTypeEnum.convert(viewTypeName);
    List<Integer> statusList = viewTypeEnum.getRefillStatusList().stream().map(RefillStatusEnum::getCode).collect(Collectors.toList());
    DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
    refillFilterData.initFilterItems();
    return refillService.getRefillRequestByStatusList(statusList, dataTableParams, refillFilterData, principal.getName(), locale);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(InvoiceNotFoundException.class)
  @ResponseBody
  public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({
      InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
      InvoiceActionIsProhibitedForNotHolderException.class
  })
  @ResponseBody
  public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler({
      NotEnoughUserWalletMoneyException.class, RequestLimitExceededException.class
  })
  @ResponseBody
  public ErrorInfo NotAcceptableExceptionHandler(HttpServletRequest req, Exception exception) {
    log.error(exception);
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return new ErrorInfo(req.getRequestURL(), exception);
  }

}
