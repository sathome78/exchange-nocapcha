package me.exrates.model;

import java.util.Date;
import java.util.Locale;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class Order {

	@Autowired
	private MessageSource messageSource;
	private static final Locale ru = new Locale("ru");
	
	private int id;
	private int walletIdSell;
	private int currencySell;
	private String currencySellString;

	@NotNull(message = "Заполните поле") 
	@DecimalMin(value="0.000000001", message="Значение должно быть больше 0.000000001")
	@DecimalMax(value="10000", message="Значение должно быть меньше 10 000")
	@Digits(integer=5, fraction=9, message = "Значение должно быть в диапазоне: 0.000000001 - 10 000")
	private double amountSell;
	
	private double commission;
	private int currencyBuy;
	private String currencyBuyString;
	private int walletIdBuy;
	
	@NotNull(message = "Заполните поле")
	@DecimalMin(value="0.000000001", message="Значение должно быть больше 0.000000001")
	@DecimalMax(value="10000", message="Значение должно быть меньше 10 000")
	@Digits(integer=5, fraction=9, message = "Значение должно быть в диапазоне: 0.000000001 - 10 000")
	private double amountBuy;
	private double amountBuyWithCommission;

	private OperationType operationType;
	private OrderStatus status;
	private String statusString;
	private Date dateCreation;
	private Date dateFinal;
	
	
	
	public Order() {
		
	}

	
	public String getCurrencySellString() {
		return currencySellString;
	}


	public void setCurrencySellString(String currencySellString) {
		this.currencySellString = currencySellString;
	}


	public String getCurrencyBuyString() {
		return currencyBuyString;
	}


	public void setCurrencyBuyString(String currencyBuyString) {
		this.currencyBuyString = currencyBuyString;
	}


	public double getAmountBuy() {
		return amountBuy;
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	public double getAmountBuyWithCommission() {
		return amountBuy-amountBuy*commission/100;
	}

	public void setAmountBuyWithCommission(double amountBuyWithCommission) {
		this.amountBuyWithCommission = amountBuyWithCommission;
	}

	public void setAmountBuy(double amountBuy) {
		this.amountBuy = amountBuy;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getWalletIdSell() {
		return walletIdSell;
	}


	public void setWalletIdSell(int walletIdSell) {
		this.walletIdSell = walletIdSell;
	}


	public int getCurrencySell() {
		return currencySell;
	}


	public void setCurrencySell(int currencySell) {
		this.currencySell = currencySell;
	}


	public double getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(double amountSell) {
		this.amountSell = amountSell;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public int getCurrencyBuy() {
		return currencyBuy;
	}
	public void setCurrencyBuy(int currencyBuy) {
		this.currencyBuy = currencyBuy;
	}

	public int getWalletIdBuy() {
		return walletIdBuy;
	}

	public void setWalletIdBuy(int walletIdBuy) {
		this.walletIdBuy = walletIdBuy;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateFinal() {
		return dateFinal;
	}

	public void setDateFinal(Date dateFinal) {
		this.dateFinal = dateFinal;
	}
	
}


	