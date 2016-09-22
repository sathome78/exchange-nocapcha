package me.exrates.model;

import java.math.BigDecimal;

public class Currency {

	private int id;
	private String name;
	private String description;
	private BigDecimal minWithdrawSum;

	public Currency() {
		
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getMinWithdrawSum() {
		return minWithdrawSum;
	}

	public void setMinWithdrawSum(BigDecimal minWithdrawSum) {
		this.minWithdrawSum = minWithdrawSum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Currency currency = (Currency) o;

		if (id != currency.id) return false;
		if (name != null ? !name.equals(currency.name) : currency.name != null) return false;
		if (description != null ? !description.equals(currency.description) : currency.description != null)
			return false;
		return minWithdrawSum != null ? minWithdrawSum.equals(currency.minWithdrawSum) : currency.minWithdrawSum == null;

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (minWithdrawSum != null ? minWithdrawSum.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Currency{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", minWithdrawSum=" + minWithdrawSum +
				'}';
	}
}
