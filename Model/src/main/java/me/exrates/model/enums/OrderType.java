package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedOrderTypeException;

import java.util.Arrays;

/**
 * Created by OLEG on 06.04.2017.
 */
public enum OrderType {
  SELL(1), BUY(2);
  
  private int type;
  
  public int getType() {
    return type;
  }
  
  OrderType(int type) {
    this.type = type;
  }
  
  public static OrderType convert(int type) {
    return Arrays.stream(OrderType.values()).filter(ot -> ot.type == type).findAny()
            .orElseThrow(UnsupportedOrderTypeException::new);
  }
  
  public static OrderType convert(String name) {
    return Arrays.stream(OrderType.values()).filter(ot -> ot.name().equals(name)).findAny()
            .orElseThrow(UnsupportedOrderTypeException::new);
  }

  @Override
  public String toString() {
    return "OrderType{" +
            "type=" + this.name() +
            '}';
  }
}
