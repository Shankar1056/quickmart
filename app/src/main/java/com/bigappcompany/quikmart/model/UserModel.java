package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 13 Jul 2017 at 12:01 PM
 */

public class UserModel {
	private String phone;
	private String name;
	private String email;
	private String sessionId;
	
	public UserModel(JSONObject data) throws JSONException {
		sessionId = data.getString("session_token");
		name = data.getString("first_name");
		email = data.getString("email");
		phone = data.getString("mobile");
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getSessionId() {
		return sessionId;
	}
}
