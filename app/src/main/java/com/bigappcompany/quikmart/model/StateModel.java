package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 09 Aug 2017 at 4:36 PM
 */

public class StateModel implements Serializable {
	private final String id, state;
	
	public StateModel(JSONObject state) throws JSONException {
		id = state.getString("id_state");
		this.state = state.getString("name");
	}
	
	public String getState() {
		return state;
	}
	
	String getStateId() {
		return id;
	}
}
