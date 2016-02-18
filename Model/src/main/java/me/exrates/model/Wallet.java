package me.exrates.model;

public class Wallet {
	
	private int id;
	private int currencyId;
	private int userId;
	private double activeBalance;
	private double reservedBalance;
	private String name;

	public Wallet() {

	}
	
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

	public int getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
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

	public String getFullName(){
		return name+" "+activeBalance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Wallet wallet = (Wallet) o;

		if (id != wallet.id) return false;
		if (currencyId != wallet.currencyId) return false;
		if (userId != wallet.userId) return false;
		if (Double.compare(wallet.activeBalance, activeBalance) != 0) return false;
		if (Double.compare(wallet.reservedBalance, reservedBalance) != 0) return false;
		return name != null ? name.equals(wallet.name) : wallet.name == null;

	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = id;
		result = 31 * result + currencyId;
		result = 31 * result + userId;
		temp = Double.doubleToLongBits(activeBalance);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(reservedBalance);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}