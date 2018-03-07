package com.bigappcompany.quikmart.model;

import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Jul 2017 at 11:45 AM
 */

public class AuthModel {
	private String mobile;
	private String otp;
	private String name;
	private String email;
	
	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("mobile", mobile);
		object.addProperty("otp", otp);
		object.addProperty("first_name", name);
		object.addProperty("email", email);
		return object;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
