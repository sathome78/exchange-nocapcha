package me.exrates.model;

import java.util.Date;

public class Commission {

	private int id;
<<<<<<< HEAD
	private int operationType;
	private double value;
	private Date dateOfChange;
	
	public Commission() {
		
=======
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
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

<<<<<<< HEAD
	public int getOperationType() {
		return operationType;
	}

	public void setoperationType(int operationType) {
=======
	public OperationType getOperationType() {
		return operationType;
	}

	public void setoperationType(OperationType operationType) {
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
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
<<<<<<< HEAD
	
	
}
=======
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
