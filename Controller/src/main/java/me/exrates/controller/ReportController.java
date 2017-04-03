package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.SummaryInOutReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryTotalInOutDto;
import me.exrates.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ReportController {

  @Autowired
  ReportService reportService;

  @RequestMapping(value = "/2a8fy7b07dxe44/report/InputOutputSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
  @ResponseBody
  public String getUsersWalletsSummeryTotalInOut(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String role,
      @RequestParam String direction,
      @RequestParam List<String> currencyList,
      Principal principal) {
    String value = InvoiceReportDto.getTitle() +
        reportService.getInvoiceReport(principal.getName(), startDate, endDate, role, direction, currencyList)
            .stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());
    return value;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/report/UsersWalletsSummaryInOut", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, String> getUsersWalletsSummeryInOut(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String role,
      @RequestParam(required = false) List<String> currencyList,
      Principal principal) {
    List<SummaryInOutReportDto> resultList = reportService.getUsersSummaryInOutList(principal.getName(), startDate, endDate, role, currencyList);
    String listString = SummaryInOutReportDto.getTitle() +
        resultList.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());
    /**/
    Map<String, UserSummaryTotalInOutDto> resultMap = reportService.getUsersSummaryInOutMap(resultList);
    /**/
    String summaryString = UserSummaryTotalInOutDto.getTitle() +
        resultMap.values().stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());
    return new HashMap<String, String>() {{
      put("list", listString);
      put("summary", summaryString);
    }};
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/report/downloadUsersWalletsSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
  @ResponseBody
  public String getUsersWalletsSummeryTxt(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam String role,
      @RequestParam(required = false) List<String> currencyList,
      Principal principal) {
    return
        UserSummaryDto.getTitle() +
            reportService.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(principal.getName(), startDate, endDate, role, currencyList)
                .stream()
                .map(e -> e.toString())
                .collect(Collectors.joining());
  }


}