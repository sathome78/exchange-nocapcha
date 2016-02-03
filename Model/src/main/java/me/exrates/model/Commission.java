package me.exrates.model;

import java.util.Date;

public class Commission {

	private int id;
	private int operationType;
	private double value;
	private Date dateOfChange;
	
	public Commission() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setoperationType(int operationType) {
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
