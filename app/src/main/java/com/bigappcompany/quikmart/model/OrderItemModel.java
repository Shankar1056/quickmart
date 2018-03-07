package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 Aug 2017 at 3:29 PM
 */

public class OrderItemModel {
	private final String title;
	private final int qty;
	private final String imageUrl;
	private final double price;
	private final CombinationModel combination;
	
	OrderItemModel(JSONObject item) throws JSONException {
		title = item.getString("order_product_name");
		imageUrl = item.optString("order_product_image", null);
		qty = item.optInt("order_product_qty");
		price = item.optDouble("order_item_display_price", 0);
		
		if (item.has("combinations")) {
			JSONObject combination = item.getJSONObject("combinations");
			this.combination = new CombinationModel(combination, CombinationModel.ORDER);
		} else {
			combination = null;
		}
	}
	
	public String getImageUrl() {
		return combination != null ? combination.getImageUrl() : imageUrl;
	}
	
	public String getTitle() {
		return title;
	}
	
	public double getPrice() {
		return combination != null ? combination.getQty() * combination.getPrice() : qty * price;
	}
	
	public int getQty() {
		return combination != null ? combination.getQty() : qty;
	}
}
