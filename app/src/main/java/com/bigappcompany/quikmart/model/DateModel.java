package com.bigappcompany.quikmart.model;

import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Aug 2017 at 12:59 PM
 */

public class DateModel {
	private final String date;
	
	public DateModel(Calendar calendar) {
		date = DateFormat.format("yyyy-MM-dd", calendar).toString();
	}
	
	public String getDate() {
		return date;
	}
}
