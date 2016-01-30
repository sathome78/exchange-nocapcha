package me.exrates.model;

public class Wallet {
	
	private int id;
	private int currId;
	private int userId;
	private double activeBalance;
	private double reservedBalance;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrId() {
		return currId;
	}

	public void setCurrId(int currId) {
		this.currId = currId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getActiveBalance() {
		return activeBalance;
	}

	public void setActiveBalance(double activeBalance) {
		this.activeBalance = activeBalance;
	}

	public double getReservedBalance() {
		return reservedBalance;
	}

	public void setReservedBalance(double reservedBalance) {
		this.reservedBalance = reservedBalance;
	}
}