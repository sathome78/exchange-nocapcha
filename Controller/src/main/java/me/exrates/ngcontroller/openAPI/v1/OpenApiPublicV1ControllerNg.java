//package me.exrates.ngcontroller.openAPI.v1;
//
//
//import me.exrates.model.dto.openAPI.OpenOrderDto;
//import me.exrates.model.enums.OrderType;
//import me.exrates.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
//
//@RequestMapping("/api/v1/public")
//@RestController
//public class OpenApiPublicV1ControllerNg {
//
//
//    private final OrderService orderService;
//
//    @Autowired
//    public OpenApiPublicV1ControllerNg(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    @GetMapping(value = "/open-orders/{order_type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
//                                         @RequestParam("currency_pair") String currencyPair) {
//        String currencyPairName = transformCurrencyPair(currencyPair);
//        return orderService.getOpenOrders(currencyPairName, orderType);
//    }
//
//
//}
