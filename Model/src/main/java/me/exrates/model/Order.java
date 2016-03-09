package me.exrates.model;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

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
	private BigDecimal amountSell;
	
	private BigDecimal commission;
	private BigDecimal commissionAmountSell;
	private BigDecimal commissionAmountBuy;
	private int currencyBuy;
	private String currencyBuyString;
	private int walletIdBuy;
	
	@NotNull(message = "Заполните поле")
	@DecimalMin(value="0.000000001", message="Значение должно быть больше 0.000000001")
	@DecimalMax(value="10000", message="Значение должно быть меньше 10 000")
	@Digits(integer=5, fraction=9, message = "Значение должно быть в диапазоне: 0.000000001 - 10 000")
	private BigDecimal amountBuy;

	private OperationType operationType;
	private OrderStatus status;
	private String statusString;
	private LocalDateTime dateCreation;
	private LocalDateTime dateFinal;
	private BigDecimal amountBuyWithCommission;	
	private BigDecimal amountSellWithCommission;
	
	public Order() {
		
	}
	
	public BigDecimal getCommissionAmountSell() {
		return commissionAmountSell;
	}


	public void setCommissionAmountSell(BigDecimal commissionAmountSell) {
		this.commissionAmountSell = commissionAmountSell;
	}


	public BigDecimal getCommissionAmountBuy() {
		return commissionAmountBuy;
	}


	public void setCommissionAmountBuy(BigDecimal commissionAmountBuy) {
		this.commissionAmountBuy = commissionAmountBuy;
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


	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public String getStatusString() {
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	
	public void setAmountBuy(BigDecimal amountBuy) {
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


	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
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


	public LocalDateTime getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}

	public LocalDateTime getDateFinal() {
		return dateFinal;
	}

	public void setDateFinal(LocalDateTime dateFinal) {
		this.dateFinal = dateFinal;
	}

	public BigDecimal getAmountBuyWithCommission() {
		return amountBuy.subtract(amountBuy.multiply(commission.divide(BigDecimal.valueOf(100))));
	}

	public void setAmountBuyWithCommission(BigDecimal amountBuyWithCommission) {
		this.amountBuyWithCommission = amountBuyWithCommission;
	}

	public BigDecimal getAmountSellWithCommission() {
		return amountSell.subtract(amountSell.multiply(commission.divide(BigDecimal.valueOf(100))));
	}

	public void setAmountSellWithCommission(BigDecimal amountSellWithCommission) {
		this.amountSellWithCommission = amountSellWithCommission;
	}
}


	