package com.bigappcompany.quikmart.model;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 06 May 2017 at 2:23 PM
 */

public class AddressModel implements Serializable {
	private String addressId, name, address1, address2, landmark, city, pincode;
	private StateModel state;
	
	public AddressModel() {
	}
	
	public AddressModel(JSONObject address) throws JSONException {
		JSONObject object = new JSONObject();
		object.put("id_state", address.getString("state_id"));
		object.put("name", address.getString("state_name"));
		state = new StateModel(object);
		pincode = address.getString("pincode");
		address1 = address.getString("address1");
		address2 = address.getString("address2");
		name = address.getString("name");
		city = address.getString("city");
		landmark = address.getString("landmark");
		addressId = address.getString("address_id");
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("pincode", pincode);
		object.addProperty("address1", address1);
		object.addProperty("address2", address2);
		object.addProperty("city", city);
		object.addProperty("landmark", landmark);
		object.addProperty("id_state", state.getStateId());
		object.addProperty("first_name", name);
		object.addProperty("id_address", addressId);
		return object;
	}
	
	public void setState(StateModel state) {
		this.state = state;
	}
	
	public String getAddress() {
		return address1 + ", " + address2 + ", " + city + ", " + state.getState() + ", " + pincode + ", " + landmark;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getLandmark() {
		return landmark;
	}
	
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getPincode() {
		return pincode;
	}
	
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	
	String getAddressId() {
		return addressId;
	}
	
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	
	@Override
	public String toString() {
		return name + "\n\n" + address1 + ", " + address2 + ", " + landmark + ", " + city + ", " + state.getState() + "-" + pincode;
	}
}
