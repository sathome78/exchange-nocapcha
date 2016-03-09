package me.exrates.model;

import java.util.Comparator;

public class OperationViewComparator implements Comparator<OperationView>{

	@Override
	public int compare(OperationView one, OperationView two) {
		return two.getDatetime().compareTo(one.getDatetime());
	}

}
