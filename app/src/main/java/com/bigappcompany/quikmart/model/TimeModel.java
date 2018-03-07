package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Aug 2017 at 1:11 PM
 */

public class TimeModel {
	private final String timeSlot;
	private final String timeId;
	
	public TimeModel(JSONObject time) throws JSONException {
		timeId = time.getString("timeslot_id");
		timeSlot = time.getString("timeslot_description");
	}
	
	public String getTimeSlot() {
		return timeSlot;
	}
	
	public String getId() {
		return timeId;
	}
}
