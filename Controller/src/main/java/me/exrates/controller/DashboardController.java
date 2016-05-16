package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    @Autowired
    OrderService orderService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    RegisterFormValidation registerFormValidation;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    VerifyReCaptchaSec verifyReCaptcha;

    @RequestMapping(value = {"/dashboard/locale"})
    public void localeSwitcherCommand(Principal principal, HttpServletRequest request) {
        userService.setPreferedLang(userService.getIdByEmail(principal.getName()), localeResolver.resolveLocale(request));
        request.getSession(true);
    }

    @RequestMapping(value = {"/dashboard"})
    public ModelAndView dashboard(@ModelAttribute CurrencyPair currencyPair, Principal principal, @RequestParam(required = false) String errorNoty, @RequestParam(required = false) String successNoty, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.addObject("successNoty", successNoty);
        model.addObject("errorNoty", errorNoty);
        model.setViewName("dashboard");
        /**/
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        if (currencyPair.getName() == null) {
            currencyPair = currencyPairs.get(0);
        }
        /**/
        for (CurrencyPair currencyPairRecord : currencyPairs) {
            if (currencyPairRecord.getName().equals(currencyPair.getName())) {
                currencyPair = currencyPairRecord;
            }
        }
        /**/
        request.getSession().setAttribute("currentCurrencyPair", currencyPair);
        model.addObject("currencyPairs", currencyPairs);
        model.addObject("currencyPair", currencyPair);
        /**/
        List<OrderListDto> ordersBuy = dashboardService.getAllBuyOrders(currencyPair);
        List<OrderListDto> ordersSell = dashboardService.getAllSellOrders(currencyPair);
        model.addObject("ordersBuy", ordersBuy);
        model.addObject("ordersSell", ordersSell);
        /**/
        BigDecimal sumAmountBuy = ordersBuy.stream().map(OrderListDto::getAmountBase).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmountSell = ordersSell.stream().map(OrderListDto::getAmountBase).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addObject("sumAmountBuy", sumAmountBuy);
        model.addObject("sumAmountSell", sumAmountSell);
        /**/
        if (principal != null) {
            model.addObject("balanceCurrency1", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()), currencyPair.getCurrency1().getId()));
            model.addObject("balanceCurrency2", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()), currencyPair.getCurrency2().getId()));
        }
        /**/
        ExOrder exOrder = new ExOrder();
        model.addObject(exOrder);
        return model;
    }

    @RequestMapping(value = "/dashboard/chartArray/{type}", method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList chartArray(@PathVariable("type") String chartType, @RequestParam(required = false) String period, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        /**/
        BackDealInterval backDealInterval = getBackDealInterval(period, request);
        ArrayList<List> arrayListMain = new ArrayList<>();
        /*in first row return backDealInterval - to synchronize period menu with it*/
        arrayListMain.add(new ArrayList<Object>() {{
            add(backDealInterval);
        }});
        /**/
        if ("area".equals(chartType)) {
            List<Map<String, Object>> rows = dashboardService.getDataForAreaChart(currencyPair, backDealInterval);
            for (Map<String, Object> row : rows) {
                Timestamp dateAcception = (Timestamp) row.get("dateAcception");
                BigDecimal exrate = (BigDecimal) row.get("exrate");
                BigDecimal volume = (BigDecimal) row.get("volume");
                if (dateAcception != null) {
                    ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
                    arrayList.add(dateAcception.toString());
                    arrayList.add(exrate.doubleValue());
                    arrayList.add(volume.doubleValue());
                /*titles of values for chart tip*/
                    arrayList.add(messageSource.getMessage("orders.date", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.exrate", null, localeResolver.resolveLocale(request)));
                    arrayList.add(messageSource.getMessage("orders.volume", null, localeResolver.resolveLocale(request)));
                    arrayListMain.add(arrayList);
                }
            }
        } else if ("candle".equals(chartType)) {
            List<CandleChartItemDto> rows = dashboardService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
                arrayList.add(candle.getBeginPeriod().toString());
                arrayList.add(candle.getEndPeriod().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                /*titles of values for chart tip*/
                arrayList.add(messageSource.getMessage("orders.date", null, localeResolver.resolveLocale(request)));
                arrayList.add(messageSource.getMessage("orders.exrate", null, localeResolver.resolveLocale(request)));
                arrayList.add(messageSource.getMessage("orders.volume", null, localeResolver.resolveLocale(request)));
                arrayListMain.add(arrayList);
            }
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        return arrayListMain;
    }

    @RequestMapping(value = "/dashboard/commission/{type}", method = RequestMethod.GET)
    public
    @ResponseBody
    BigDecimal getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "sell":
                return commissionService.findCommissionByType(OperationType.SELL).getValue();
            case "buy":
                return commissionService.findCommissionByType(OperationType.BUY).getValue();
            default:
                return null;
        }
    }

    @RequestMapping(value = "/forgotPassword")
    public ModelAndView forgotPassword() {
        ModelAndView model = new ModelAndView();
        model.addObject("user", new User());
        model.setViewName("forgotPassword");

        return model;
    }

    @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
    public ModelAndView forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("/forgotPassword", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }
        /**/
        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("user", user);
            model.setViewName("/forgotPassword");
            return model;
        }
        String email = user.getEmail();
        user = userService.findByEmail(email);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(email);
        userService.update(updateUserDto, true, localeResolver.resolveLocale(request));
        /**/
        model.setViewName("redirect:/dashboard");
        /**/
        return model;
    }

    @RequestMapping(value = "/resetPasswordConfirm")
    public ModelAndView resetPasswordConfirm(@RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token);
            User user = userService.getUserById(userId);
            model.addObject("user", user);
            model.setViewName("updatePassword");
            org.springframework.security.core.userdetails.User userSpring = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), false, false, false, false,
                    userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
            Collection<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
            authList.add(new SimpleGrantedAuthority(UserRole.ROLE_CHANGE_PASSWORD.name()));
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userSpring, null, authList);
            SecurityContextHolder.getContext().setAuthentication(auth);
            user.setPassword(null);
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/dashboard/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@ModelAttribute User user, BindingResult result, HttpServletRequest request, Principal principal) {

        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            return new ModelAndView("/updatePassword", "user", user);
        } else {
            String password = user.getPassword();
            ModelAndView model = new ModelAndView();
            UpdateUserDto updateUserDto = new UpdateUserDto(userService.findByEmail(principal.getName()).getId());
            updateUserDto.setPassword(password);
            userService.updateUserByAdmin(updateUserDto);
            /**/
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
            return model;
        }
    }

    @RequestMapping(value = "/forgotPassword/submitUpdate", method = RequestMethod.POST)
    public ModelAndView submitUpdate(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        /**/
        if (result.hasErrors()) {
            model.addObject("user", user);
            model.setViewName("updatePassword");
            return model;
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setPassword(user.getPassword());
            userService.updateUserByAdmin(updateUserDto);
            model.setViewName("redirect:/dashboard");
        }
        /**/
        return model;
    }

    @RequestMapping(value = "/dashboard/changeCurrencyPair", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ExOrderStatisticsDto getNewCurrencyPairData(@RequestParam(required = false) String currencyPairName, @RequestParam(required = false) String period, HttpServletRequest request) {
        CurrencyPair currencyPair;
        if (currencyPairName == null) {
            if (request.getSession().getAttribute("currentCurrencyPair") == null) {
                List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
                currencyPair = currencyPairs.get(0);
            } else {
                currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
            }
        } else {
            List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
            currencyPair = currencyPairs
                    .stream()
                    .filter(e -> e.getName().equals(currencyPairName))
                    .collect(Collectors.toList()).get(0);
        }
        request.getSession().setAttribute("currentCurrencyPair", currencyPair);
        /**/
        BackDealInterval backDealInterval = getBackDealInterval(period, request);
        ExOrderStatisticsDto exOrderStatisticsDto = dashboardService.getOrderStatistic(currencyPair, backDealInterval);
        return exOrderStatisticsDto;
    }

    @RequestMapping(value = "/dashboard/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getCurrencyPairNameList() {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        return currencyPairs.stream().map(e -> e.getName()).collect((Collectors.toList()));
    }

    private BackDealInterval getBackDealInterval(String period, HttpServletRequest request) {
        BackDealInterval result;
        if (period == null) {
            result = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
            if (result == null) {
                result = new BackDealInterval("24 HOUR");
            }
        } else {
            result = new BackDealInterval(period);
        }
        return result;
    }

}


