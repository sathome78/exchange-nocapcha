package me.exrates.model;

import me.exrates.model.enums.OperationType;
import me.exrates.model.exceptions.UnsupportedOperationTypeException;

import java.math.BigDecimal;
import java.util.Date;

public class Commission {

	private int id;
	private OperationType operationType;
	private BigDecimal value;
	private Date dateOfChange;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Date getDateOfChange() {
		return dateOfChange;
	}

	public void setDateOfChange(Date dateOfChange) {
		this.dateOfChange = dateOfChange;
	}

	@Override
	public String toString() {
		return "Commission{" +
				"id=" + id +
				", operationType=" + operationType +
				", value=" + value +
				", dateOfChange=" + dateOfChange +
				'}';
	}
}