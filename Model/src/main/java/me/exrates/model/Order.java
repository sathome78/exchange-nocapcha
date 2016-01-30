package me.exrates.model;

import java.util.Date;
import java.util.Locale;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class Order {

	@Autowired
	private MessageSource messageSource;
	private static final Locale ru = new Locale("ru");

	private int id;
	private int walletIdSell;
	private int currencySell;

	@NotNull(message = "Заполните поле")
	@DecimalMin(value="0.000000001", message="Значение должно быть больше 0.000000001")
	@DecimalMax(value="10000", message="Значение должно быть меньше 10 000")
	@Digits(integer=5, fraction=9, message = "Значение должно быть в диапазоне: 0.000000001 - 10 000")
	private double amountSell;

	private int commission;
	private int currencyBuy;
	private int walletIdBuy;

	@NotNull(message = "Заполните поле")
	@DecimalMin(value="0.000000001", message="Значение должно быть больше 0.000000001")
	@DecimalMax(value="10000", message="Значение должно быть меньше 10 000")
	@Digits(integer=5, fraction=9, message = "Значение должно быть в диапазоне: 0.000000001 - 10 000")
	private double exchangeRate;

	private int operationType;
	private String status;
	private Date date_creation;
	private Date date_final;

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

	public int getCommission() {
		return commission;
	}

	public void setCommission(int commission) {
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

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate_creation() {
		return date_creation;
	}

	public void setDate_creation(Date date_creation) {
		this.date_creation = date_creation;
	}

	public Date getDate_final() {
		return date_final;
	}

	public void setDate_final(Date date_final) {
		this.date_final = date_final;
	}
}