package com.bigappcompany.quikmart.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 Aug 2017 at 3:27 PM
 */

public class OrderDetailsModel {
	private final ArrayList<OrderItemModel> itemList = new ArrayList<>();
	private final double promo;
	
	public OrderDetailsModel(JSONObject data) throws JSONException {
		promo = data.getDouble("order_coupon_applied");
		
		JSONArray items = data.getJSONArray("order_items");
		for (int i = 0; i < items.length(); i++) {
			itemList.add(new OrderItemModel(items.getJSONObject(i)));
		}
	}
	
	public ArrayList<OrderItemModel> getItemList() {
		return itemList;
	}
	
	public double getPromo() {
		return promo;
	}
}
