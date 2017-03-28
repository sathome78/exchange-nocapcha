package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.form.AuthorityOptionsForm;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.*;
import me.exrates.service.exception.NoPermissionForOperationException;
import me.exrates.service.exception.OrderDeletingException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.BusinessUserRoleEnum.ADMIN;
import static me.exrates.model.enums.GroupUserRoleEnum.ADMINS;
import static me.exrates.model.enums.GroupUserRoleEnum.USERS;
import static me.exrates.model.enums.UserCommentTopicEnum.GENERAL;
import static me.exrates.model.enums.UserRole.ADMINISTRATOR;
import static me.exrates.model.enums.UserRole.FIN_OPERATOR;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Log4j2
public class ReportController {

  @Autowired
  ReportService reportService;

  @RequestMapping(value = "/2a8fy7b07dxe44/report/downloadInputOutputSummaryReport", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
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





}