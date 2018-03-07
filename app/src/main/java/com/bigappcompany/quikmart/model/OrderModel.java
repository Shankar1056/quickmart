package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 Aug 2017 at 12:37 PM
 */

public class OrderModel implements Serializable {
	private final String date;
	private final String orderId;
	private final String orderStatus;
	private final double total;
	
	public OrderModel(JSONObject item) throws JSONException {
		date = item.getString("order_created_date");
		orderId = item.getString("order_id");
		orderStatus = item.getString("order_status_name");
		total = item.getDouble("order_grand_total");
	}
	
	public String getDate() {
		return date;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	
	public double getTotal() {
		return total;
	}
}
