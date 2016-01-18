package me.exrates.beans;

import java.util.Date;

public class Commission {

	private int id;
	private String operationType;
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

	public String getName() {
		return operationType;
	}

	public void setName(String name) {
		this.operationType = name;
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
