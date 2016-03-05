package me.exrates.controller;

import com.mysql.fabric.xmlrpc.base.Data;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    CommissionService commissionService;

    @RequestMapping(value = "/dashboard")
    public ModelAndView dashboard(Principal principal) {
        ModelAndView model = new ModelAndView();
        model.setViewName("dashboard");

        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        CurrencyPair currencyPair = currencyService.getCurrencyPairById(2,1);
        model.addObject("currencyPairs", currencyPairs);
        model.addObject("currencyPair", currencyPair);

        model.addObject("lastOrder", dashboardService.getLastClosedOrder());

        List<Order> ordersBuy = dashboardService.getAllBuyOrders(currencyPair);
        List<Order> ordersSell = dashboardService.getAllSellOrders(currencyPair);
        model.addObject("ordersBuy", ordersBuy);
        model.addObject("ordersSell", ordersSell);

        Order order = new Order();

        Double sumAmountBuy = 0.0;
        for(Order orderRecord : ordersBuy){
            sumAmountBuy += orderRecord.getAmountBuy();
        }
        Double sumAmountSell = 0.0;
        for(Order orderRecord : ordersSell){
            sumAmountSell += orderRecord.getAmountBuy();
        }
        model.addObject("sumAmountBuy", sumAmountBuy);
        model.addObject("sumAmountSell", sumAmountSell);


        List<Map<String, BigDecimal>> list = dashboardService.getAmountsFromClosedOrders(currencyPair);
        BigDecimal sumAmountBuyClosed = new BigDecimal(0.0);
        BigDecimal sumAmountSellClosed = new BigDecimal(0.0);
        for (Map<String, BigDecimal> tempRow : list) {
            sumAmountBuyClosed =  tempRow.get("amount_buy").setScale(2,BigDecimal.ROUND_CEILING);
            sumAmountSellClosed = tempRow.get("amount_sell").setScale(2,BigDecimal.ROUND_CEILING);
        }
        model.addObject("sumAmountBuyClosed", sumAmountBuyClosed.setScale(2,BigDecimal.ROUND_CEILING));
        model.addObject("sumAmountSellClosed", sumAmountSellClosed.setScale(2,BigDecimal.ROUND_CEILING));

        if (principal != null){
            model.addObject("balanceCurrency1", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()),1));
            model.addObject("balanceCurrency2", dashboardService.getBalanceByCurrency(userService.getIdByEmail(principal.getName()),2));
        }

        BigDecimal minPrice = dashboardService.getMinPriceByCurrency(currencyPair);
        BigDecimal maxPrice = dashboardService.getMaxPriceByCurrency(currencyPair);
        model.addObject("minPrice",minPrice.setScale(5,BigDecimal.ROUND_CEILING) );
//        order.setAmountSell(minPrice);
        model.addObject("maxPrice",maxPrice.setScale(5,BigDecimal.ROUND_CEILING) );
//        order.setAmountBuy(maxPrice);

        model.addObject(order);
        return model;
    }

    @RequestMapping(value = "/dashboard/chartArray", method = RequestMethod.GET)
    public @ResponseBody ArrayList chartArray() {

        CurrencyPair currencyPair = currencyService.getCurrencyPairById(2,1);
        List<Map<String, Object>> list = dashboardService.getDataForChart(currencyPair);

        ArrayList<ArrayList> arrayListMain = new ArrayList<ArrayList>();

        for (Map<String, Object> tempRow : list) {
            Double amount = (Double) tempRow.get("amount");
            Double amountSell = (Double) tempRow.get("amount_sell");
            Timestamp timestamp = (Timestamp)tempRow.get("date_final");
            Date date  = new Date(timestamp.getTime());

            ArrayList<Object> arrayList = new ArrayList<Object>();
            arrayList.add(timestamp.toString());
            arrayList.add(amount);
            arrayList.add(amount);
            arrayList.add(amount);
            arrayList.add(amount);
            arrayList.add(amountSell);
            arrayListMain.add(arrayList);

        }
        return arrayListMain;
   }

    @RequestMapping(value = "/dashboard/commission/{type}",method = RequestMethod.GET)
    public @ResponseBody Double  getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "input" :
                return commissionService.getCommissionByType(OperationType.INPUT);
            case "output" :
                return commissionService.getCommissionByType(OperationType.OUTPUT);
            default:
                return null;
        }
    }

    @RequestMapping(value = "/forgotPassword")
    public ModelAndView forgotPassword() {
        ModelAndView model = new ModelAndView();
        return model;
    }
    }
