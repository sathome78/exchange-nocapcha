package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.SummaryInOutReportDto;
import me.exrates.model.dto.UserSummaryTotalInOutDto;
import me.exrates.service.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
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
    Map<String, UserSummaryTotalInOutDto> resultMap = new HashMap<String, UserSummaryTotalInOutDto>() {
      @Override
      public UserSummaryTotalInOutDto put(String key, UserSummaryTotalInOutDto value) {
        if (this.get(key) == null) {
          return super.put(key, value);
        } else {
          UserSummaryTotalInOutDto storedValue = this.get(key);
          storedValue.setTotalIn(storedValue.getTotalIn().add(value.getTotalIn()));
          storedValue.setTotalOut(storedValue.getTotalOut().add(value.getTotalOut()));
          return super.put(key, storedValue);
        }
      }
    };
    resultList.forEach(e -> resultMap.put(
        e.getCurrency(),
        new UserSummaryTotalInOutDto(e.getCurrency(), StringUtils.isEmpty(e.getCreationDateIn()) ? BigDecimal.ZERO : e.getAmount(), StringUtils.isEmpty(e.getCreationDateOut()) ? BigDecimal.ZERO : e.getAmount())
    ));
    String summaryString = UserSummaryTotalInOutDto.getTitle() +
        resultMap.values().stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());
    return new HashMap<String, String>() {{
      put("list", listString);
      put("summary", summaryString);
    }};
  }


}