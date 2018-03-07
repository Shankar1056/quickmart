package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 31 Jul 2017 at 5:29 PM
 */

public class CombinationModel implements Serializable {
	static final int PRODUCT = 0, CART = 1, ORDER = 2;
	private final String combinationId, imageUrl;
	private final double price;
	private final String combination;
	private final String discount;
	private int qty;
	
	CombinationModel(JSONObject item, int type) throws JSONException {
		switch (type) {
			case PRODUCT:
				combinationId = item.getString("combination_id");
				combination = item.getString("combination_name");
				price = item.getDouble("combination_price_with_tax");
				imageUrl = item.getString("combination_image");
				
				if (item.optInt("product_combination_discount_applicable") == 1) {
					if (item.optString("product_combination_discount_type").equalsIgnoreCase("fixed")) {
						discount = "\u20B9 " + item.optString("product_combination_discount") + " off";
					} else {
						discount = item.optString("product_combination_discount") + " % off";
					}
				} else {
					discount = "";
				}
				break;
			
			case CART:
				combinationId = item.getString("cart_product_combination_id");
				combination = item.getString("cart_product_combination_name");
				price = item.getDouble("cart_product_combination_unit_price_with_tax");
				imageUrl = item.getString("cart_product_combination_image");
				qty = item.getInt("cart_product_combination_qty");
				
				if (item.optInt("cart_product_combination_discount_applicable") == 1) {
					if (item.optString("cart_product_combination_discount_type").equalsIgnoreCase("fixed")) {
						discount = "\u20B9 " + item.optString("cart_product_combination_discount") + " off";
					} else {
						discount = item.optString("cart_product_combination_discount") + " % off";
					}
				} else {
					discount = "";
				}
				break;
			
			default:
				combinationId = item.getString("combination_id");
				imageUrl = item.getString("combination_image");
				combination = item.getString("combination_name");
				qty = item.getInt("combinations_qty");
				price = item.getDouble("combination_display_price");
				discount = "";
				break;
		}
	}
	
	String getDiscount() {
		return discount;
	}
	
	String getImageUrl() {
		return imageUrl;
	}
	
	double getPrice() {
		return price;
	}
	
	public String getCombination() {
		return combination;
	}
	
	String getCombinationId() {
		return combinationId;
	}
	
	int getQty() {
		return qty;
	}
	
	void setQty(int qty) {
		this.qty = qty;
	}
}
