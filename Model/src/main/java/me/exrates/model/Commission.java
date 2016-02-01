package me.exrates.model;

import java.util.Date;

public class Commission {

	private int id;
	private OperationType operationType;
	private double value;
	private Date dateOfChange;

	public enum OperationType {
		INPUT(1),
		OUTPUT(2),
		SELL(3),
		BUY(4);

		public final int type;

		OperationType(int type) {
			this.type = type;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setoperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getDateOfChange() {
		return dateOfChange;
	}

	public void setDateOfChange(Date dateOfChange) {
		this.dateOfChange = dateOfChange;
	}
}