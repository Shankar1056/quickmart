package com.bigappcompany.quikmart.model;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 07 Aug 2017 at 12:28 PM
 */

public class CartItemModel {
	private final String productId;
	private final String imageUrl;
	private final String title;
	private final double price;
	private final CombinationModel combination;
	private int qty;
	
	
	public CartItemModel(JSONObject object) throws JSONException {
		productId = object.getString("product_id");
		title = object.getString("cart_product_name");
		imageUrl = object.optString("cart_product_image", null);
		qty = object.optInt("cart_product_qty");
		price = object.optDouble("cart_product_unit_price_with_tax", 0);
		
		if (object.has("combinations")) {
			JSONArray combinations = object.getJSONArray("combinations");
			combination = new CombinationModel(combinations.getJSONObject(0), CombinationModel.CART);
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
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("product_id", productId);
		object.addProperty("qty", getQty());
		if (combination != null) {
			object.addProperty("combination_id", combination.getCombinationId());
		}
		
		return object;
	}
	
	public int getQty() {
		return combination != null ? combination.getQty() : qty;
	}
	
	public void setQty(int qty) {
		if (combination != null) {
			combination.setQty(qty);
		} else {
			this.qty = qty;
		}
	}
}
