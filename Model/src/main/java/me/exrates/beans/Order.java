package me.exrates.beans;

import java.util.Date;

public class Order {

	private int id;
	private int walletIdFrom;
	private double amountFrom;
	private int currencyTo;
	private int walletIdTo;
	private double exchangeRate;
	private Date date_creation;
	private Date date_final;
	private String status;
	
	public Order() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWalletIdFrom() {
		return walletIdFrom;
	}

	public void setWalletIdFrom(int walletIdFrom) {
		this.walletIdFrom = walletIdFrom;
	}

	public double getAmountFrom() {
		return amountFrom;
	}

	public void setAmountFrom(double amountFrom) {
		this.amountFrom = amountFrom;
	}

	public int getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(int currencyTo) {
		this.currencyTo = currencyTo;
	}

	public int getWalletIdTo() {
		return walletIdTo;
	}

	public void setWalletIdTo(int walletIdTo) {
		this.walletIdTo = walletIdTo;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
}
