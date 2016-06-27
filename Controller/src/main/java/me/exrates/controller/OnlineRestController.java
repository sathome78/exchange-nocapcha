package me.exrates.controller;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.*;
import me.exrates.model.dto.onlineTableDto.*;
import me.exrates.model.enums.ChartType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.service.*;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Valk on 11.05.2016.
 */

@RestController
public class OnlineRestController {
    /*default depth the interval for chart*/
    final public BackDealInterval BACK_DEAL_INTERVAL_DEFAULT = new BackDealInterval("24 HOUR");
    /*depth the accepted order history*/
    final public BackDealInterval ORDER_HISTORY_INTERVAL = new BackDealInterval("24 HOUR");
    /*limit the data fetching of order history (additional to ORDER_HISTORY_INTERVAL). (-1) means no limit*/
    final public Integer ORDER_HISTORY_LIMIT = -1;
    /*default limit the data fetching for all tables. (-1) means no limit*/
    final public Integer TABLES_LIMIT_DEFAULT = -1;
    /*default type of the chart*/
    final public ChartType CHART_TYPE_DEFAULT = ChartType.STOCK;

    @Autowired
    CommissionService commissionService;

    @Autowired
    OrderService orderService;

    @Autowired
    WalletService walletService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    NewsService newsService;

    @Autowired
    ReferralService referralService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @RequestMapping(value = "/dashboard/commission/{type}", method = RequestMethod.GET)
    public BigDecimal getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "sell":
                return commissionService.findCommissionByType(OperationType.SELL).getValue();
            case "buy":
                return commissionService.findCommissionByType(OperationType.BUY).getValue();
            default:
                return null;
        }
    }

    @RequestMapping(value = "/dashboard/myWalletsStatistic", method = RequestMethod.GET)
    public List<MyWalletsStatisticsDto> getStatisticsForAllCurrencies(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                                      Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsStatistic" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return walletService.getAllWalletsForUserReduced(cacheData, email, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/currencyPairStatistic", method = RequestMethod.GET)
    public List<ExOrderStatisticsShortByPairsDto> getStatisticsForAllCurrencies(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                                                HttpServletRequest request) {
        String cacheKey = "currencyPairStatistic" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return orderService.getOrdersStatisticByPairs(cacheData, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/chartArray/{type}", method = RequestMethod.GET)
    public ArrayList chartArray(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        final BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        ChartType chartType = (ChartType) request.getSession().getAttribute("chartType");
        /**/
        ArrayList<List> arrayListMain = new ArrayList<>();
        /*in first row return backDealInterval - to synchronize period menu with it*/
        arrayListMain.add(new ArrayList<Object>() {{
            add(backDealInterval);
        }});
        /**/
        if (chartType == ChartType.AREA) {
            /*GOOGLE*/
            List<Map<String, Object>> rows = orderService.getDataForAreaChart(currencyPair, backDealInterval);
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
        } else if (chartType == ChartType.CANDLE) {
            /*GOOGLE*/
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
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
                arrayListMain.add(arrayList);
            }
        } else if (chartType == ChartType.STOCK) {
            /*AMCHARTS*/
            List<CandleChartItemDto> rows = orderService.getDataForCandleChart(currencyPair, backDealInterval);
            for (CandleChartItemDto candle : rows) {
                ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
                arrayList.add(candle.getBeginDate().toString());
                arrayList.add(candle.getEndDate().toString());
                arrayList.add(candle.getOpenRate());
                arrayList.add(candle.getCloseRate());
                arrayList.add(candle.getLowRate());
                arrayList.add(candle.getHighRate());
                arrayList.add(candle.getBaseVolume());
                arrayListMain.add(arrayList);
            }
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        return arrayListMain;
    }

    /**
     * Sets (init or reset) and returns current params:
     * - current currency pair
     * - current period
     * - current chart
     *
     * @param currencyPairName
     * @param period
     * @param request
     * @return object with values of params
     */
    @RequestMapping(value = "/dashboard/currentParams", method = RequestMethod.GET)
    public CurrentParams setCurrentParams(
            @RequestParam(required = false) String currencyPairName,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String chart,
            HttpServletRequest request) {
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
        BackDealInterval backDealInterval;
        if (period == null) {
            backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
            if (backDealInterval == null) {
                backDealInterval = BACK_DEAL_INTERVAL_DEFAULT;
            }
        } else {
            backDealInterval = new BackDealInterval(period);
        }
        request.getSession().setAttribute("currentBackDealInterval", backDealInterval);
        /**/
        ChartType chartType;
        if (chart == null) {
            chartType = (ChartType) request.getSession().getAttribute("chartType");
            if (chartType == null) {
                chartType = CHART_TYPE_DEFAULT;
            }
        } else {
            chartType = ChartType.convert(chart);
        }
        request.getSession().setAttribute("chartType", chartType);
        /**/
        CurrentParams currentParams = new CurrentParams();
        currentParams.setCurrencyPair((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair"));
        currentParams.setPeriod(((BackDealInterval) request.getSession().getAttribute("currentBackDealInterval")).getInterval());
        currentParams.setChartType(((ChartType) request.getSession().getAttribute("chartType")).getTypeName());
        return currentParams;
    }

    /**
     * Sets (init or reset) and returns table params for <b>tableId</b>:
     * - current limit
     *
     * @param tableId
     * @param limitValue is page size for pagination
     * @param request
     * @return object with values of params
     */
    @RequestMapping(value = "/dashboard/tableParams/{tableId}", method = RequestMethod.GET)
    public TableParams setTableParams(
            @PathVariable String tableId,
            @RequestParam(required = false) Integer limitValue,
            @RequestParam(required = false) OrderStatus orderStatusValue,
            HttpServletRequest request) {
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        if (tableParams == null) {
            tableParams = new TableParams();
            tableParams.setTableId(tableId);
        }
        /**/
        Integer limit;
        if (limitValue == null) {
            limit = tableParams.getPageSize();
            if (limit == null) {
                limit = TABLES_LIMIT_DEFAULT;
            }
        } else {
            limit = limitValue;
        }
        tableParams.setPageSize(limit);
        /**/
        request.getSession().setAttribute(attributeName, tableParams);
        return tableParams;
    }

    /*Retrieves data with statistics for orders of current CurrencyPair*/
    @RequestMapping(value = "/dashboard/ordersForPairStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ExOrderStatisticsDto getNewCurrencyPairData(HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        BackDealInterval backDealInterval = (BackDealInterval) request.getSession().getAttribute("currentBackDealInterval");
        /**/
        ExOrderStatisticsDto exOrderStatisticsDto = orderService.getOrderStatistic(currencyPair, backDealInterval, localeResolver.resolveLocale(request));
        return exOrderStatisticsDto;
    }

    @RequestMapping(value = "/dashboard/createPairSelectorMenu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getCurrencyPairNameList() {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        return currencyPairs.stream().map(e -> e.getName()).collect((Collectors.toList()));
    }

    @RequestMapping(value = "/dashboard/acceptedOrderHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderAcceptedHistoryDto> getOrderHistory(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                         HttpServletRequest request, HttpServletResponse response) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        if (currencyPair == null) {
            try {
                request.getRequestDispatcher("/dashboard/currentParams").forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
        String cacheKey = "acceptedOrderHistory" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return orderService.getOrderAcceptedForPeriod(cacheData, ORDER_HISTORY_INTERVAL, ORDER_HISTORY_LIMIT, currencyPair, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/orderCommissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderCommissionsDto getOrderCommissions(HttpServletRequest request) {
        return orderService.getCommissionForOrder();
    }

    @RequestMapping(value = "/dashboard/sellOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getSellOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        String email = principal == null ? "" : principal.getName();
        /*unlock the displaying of own orders*/
        email = null;
        /**/
        String cacheKey = "sellOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return orderService.getAllSellOrders(cacheData, currencyPair, email, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/BuyOrders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderListDto> getBuyOrdersList(@RequestParam(required = false) Boolean refreshIfNeeded,
                                               Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        String email = principal == null ? "" : principal.getName();
        /*unlock the displaying of own orders*/
        email = null;
        /**/
        String cacheKey = "BuyOrders" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return orderService.getAllBuyOrders(cacheData, currencyPair, email, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/myWalletsData", method = RequestMethod.GET)
    public List<MyWalletsDetailedDto> getMyWalletsData(@RequestParam(required = false) Boolean refreshIfNeeded,
                                                       Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        String cacheKey = "myWalletsData" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        return walletService.getAllWalletsForUserDetailed(cacheData, email, localeResolver.resolveLocale(request));
    }

    @RequestMapping(value = "/dashboard/myOrdersData/{tableId}", method = RequestMethod.GET)
    public List<OrderWideListDto> getMyOrdersData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) OperationType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        CurrencyPair currencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myOrdersData" + tableId + status + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<OrderWideListDto> result = orderService.getMyOrdersWithState(cacheData, email, currencyPair, status, type, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

    @RequestMapping(value = "/dashboard/myReferralData/{tableId}", method = RequestMethod.GET)
    public List<MyReferralDetailedDto> getMyReferralData(
            @RequestParam(required = false) Boolean refreshIfNeeded,
            @PathVariable("tableId") String tableId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) PagingDirection direction,
            Principal principal,
            HttpServletRequest request) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        /**/
        String attributeName = tableId + "Params";
        TableParams tableParams = (TableParams) request.getSession().getAttribute(attributeName);
        Assert.requireNonNull(tableParams, "The parameters are not populated for the " + tableId);
        tableParams.setOffsetAndLimitForSql(page, direction);
        /**/
        String cacheKey = "myReferralData" + request.getHeader("windowid");
        refreshIfNeeded = refreshIfNeeded == null ? false : refreshIfNeeded;
        CacheData cacheData = new CacheData(request, cacheKey, !refreshIfNeeded);
        List<MyReferralDetailedDto> result = referralService.findAllMyReferral(cacheData, email, tableParams.getOffset(), tableParams.getLimit(), localeResolver.resolveLocale(request));
        if (!result.isEmpty()) {
            result.get(0).setPage(tableParams.getPageNumber());
        }
        tableParams.updateEofState(result);
        return result;
    }

}
