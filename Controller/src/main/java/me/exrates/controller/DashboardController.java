package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CurrencyPairStatisticsDto;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
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

//    private CurrencyPair currentCurrencyPair;

    @RequestMapping(value = {"/dashboard/locale"})
    public void localeSwitcherCommand() {
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
        ExOrder lastOrder = dashboardService.getLastClosedOrderForCurrencyPair(currencyPair);
        model.addObject("lastOrder", lastOrder);
        /**/
        if (lastOrder != null) {
            CurrencyPair cp = currencyService.findCurrencyPairById(lastOrder.getCurrencyPairId());
            model.addObject("lastOrderCurrency", currencyService.getCurrencyName(cp.getCurrency1().getId()));
        }
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
        model.addObject("sumAmountBuyClosed", lastOrder == null ? new BigDecimal(0.0) : lastOrder.getAmountBase());
        model.addObject("sumAmountSellClosed", lastOrder == null ? new BigDecimal(0.0) : lastOrder.getAmountConvert());
        /**/
        if (principal != null) {
            model.addObject("balanceCurrency1", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()), currencyPair.getCurrency1().getId()));
            model.addObject("balanceCurrency2", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()), currencyPair.getCurrency2().getId()));
        }
        /**/
        BigDecimal minPrice = dashboardService.getMinPriceByCurrency(currencyPair);
        BigDecimal maxPrice = dashboardService.getMaxPriceByCurrency(currencyPair);
        model.addObject("minPrice", minPrice);
        model.addObject("maxPrice", maxPrice);
        /**/
        Order order = new Order();
        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/dashboard/chartArray", method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList chartArray(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        List<Map<String, Object>> rows = dashboardService.getDataForChart(currencyPair);

        ArrayList<List> arrayListMain = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Timestamp dateAcception = (Timestamp) row.get("dateAcception");
            BigDecimal exrate = (BigDecimal) row.get("exrate");

            if (dateAcception !=null) {
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(dateAcception.toString());
                arrayList.add(exrate.doubleValue());
                arrayListMain.add(arrayList);
            }

        }
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

        String email = user.getEmail();
        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("user", user);
            model.setViewName("/forgotPassword");
            return model;
        }
        user = userService.findByEmail(email);
        userService.update(user, false, false, true, localeResolver.resolveLocale(request));

        model.setViewName("redirect:/dashboard");

        return model;
    }

    @RequestMapping(value = "/resetPasswordConfirm")
    public ModelAndView resetPasswordConfirm(@RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token).getId();
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
    public ModelAndView updatePassword(@ModelAttribute User user, BindingResult result, HttpServletRequest request) {

        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        user.setPhone("");
        if (result.hasErrors()) {
            return new ModelAndView("/updatePassword", "user", user);
        } else {
            String password = user.getPassword();
            ModelAndView model = new ModelAndView();
            org.springframework.security.core.userdetails.User userSpring = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userService.findByEmail(userSpring.getUsername());
            user.setPassword(password);
            userService.updateUserByAdmin(user);
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
            return model;
        }
    }

    @RequestMapping(value = "/forgotPassword/submitUpdate", method = RequestMethod.POST)
    public ModelAndView submitUpdate(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));

        if (result.hasErrors()) {
            model.addObject("user", user);
            model.setViewName("updatePassword");
            return model;
        } else {

            User userUpdate = userService.getUserById(user.getId());
            userUpdate.setPassword(user.getPassword());

            userService.updateUserByAdmin(userUpdate);
            model.setViewName("redirect:/dashboard");
        }

        return model;
    }

    @RequestMapping(value = "/admin/changeCurrencyPair", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CurrencyPairStatisticsDto getNewCurrencyPairData(@RequestParam(required = false) String currencyPairName, HttpServletRequest request) {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        CurrencyPair currencyPair;
        if (currencyPairName == null) {
            if (request.getSession().getAttribute("currentCurrencyPair") == null) {
                currencyPair = currencyPairs.get(0);
            } else {
                currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
            }
        } else {
            currencyPair = currencyPairs
                    .stream()
                    .filter(e -> e.getName().equals(currencyPairName))
                    .collect(Collectors.toList()).get(0);
        }
        request.getSession().setAttribute("currentCurrencyPair", currencyPair);
        ExOrder lastOrder = dashboardService.getLastClosedOrderForCurrencyPair(currencyPair);
        CurrencyPairStatisticsDto currencyPairStatisticsDto = new CurrencyPairStatisticsDto();
        currencyPairStatisticsDto.setName(currencyPair.getName());
        currencyPairStatisticsDto.setCurrency1(currencyPair.getCurrency1().getName());
        currencyPairStatisticsDto.setCurrency2(currencyPair.getCurrency2().getName());
        if (lastOrder != null) {
            CurrencyPair cp = currencyService.findCurrencyPairById(lastOrder.getCurrencyPairId());
            currencyPairStatisticsDto.setLastOrderCurrency(currencyService.getCurrencyName(cp.getCurrency1().getId()));
        } else {
            currencyPairStatisticsDto.setLastOrderCurrency("");
        }
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(localeResolver.resolveLocale(request)));
        df.setMaximumFractionDigits(9);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(true);
        currencyPairStatisticsDto.setAmountBuy(lastOrder == null ? "0" : df.format(lastOrder.getAmountBase()));
        /**/
        BigDecimal sumAmountBuyClosed = lastOrder == null ? new BigDecimal(0.0) : lastOrder.getAmountBase();
        BigDecimal sumAmountSellClosed = lastOrder == null ? new BigDecimal(0.0) : lastOrder.getAmountConvert();
        currencyPairStatisticsDto.setSumAmountBuyClosed(lastOrder == null ? "0" : df.format(lastOrder.getAmountBase()));
        currencyPairStatisticsDto.setSumAmountSellClosed(lastOrder == null ? "0" : df.format(lastOrder.getAmountConvert()));

        return currencyPairStatisticsDto;
    }

    @RequestMapping(value = "/admin/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getCurrencyPairNameList() {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        return currencyPairs.stream().map(e -> e.getName()).collect((Collectors.toList()));
    }
}


