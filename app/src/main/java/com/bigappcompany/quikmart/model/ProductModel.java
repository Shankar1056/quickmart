package com.bigappcompany.quikmart.model;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 31 Jul 2017 at 2:28 PM
 */

public class ProductModel implements Serializable {
	private final String productId, name;
	private final ArrayList<CombinationModel> combinationList = new ArrayList<>();
	private int selectedIndex = 0;
	private double price;
	private String imageUrl;
	private int quantity;
	
	public ProductModel(JSONObject item) throws JSONException {
		productId = item.getString("product_id");
		name = item.getString("product_name");
		
		if (item.has("combinations")) {
			JSONArray combinations = item.getJSONArray("combinations");
			for (int i = 0; i < combinations.length(); i++) {
				combinationList.add(new CombinationModel(combinations.getJSONObject(i), CombinationModel.PRODUCT));
			}
		} else {
			selectedIndex = -1;
			price = item.getDouble("product_price_with_tax");
			imageUrl = item.getString("product_image");
		}
	}
	
	public String getTitle() {
		return name;
	}
	
	public String getDiscount() {
		return selectedIndex == -1 ? "" : combinationList.get(selectedIndex).getDiscount();
	}
	
	public String getCombinationName() {
		return combinationList.get(selectedIndex).getCombination();
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public ArrayList<CombinationModel> getCombinationList() {
		return combinationList;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("product_id", productId);
		object.addProperty("qty", quantity);
		
		if (selectedIndex > -1) {
			object.addProperty("combination_id", combinationList.get(selectedIndex).getCombinationId());
		}
		
		return object;
	}
	
	public CartItemModel toCartModel() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("cart_product_name", name);
		object.put("cart_product_image", getImageUrl());
		object.put("cart_product_qty", getQuantity());
		object.put("cart_product_unit_price_with_tax", getPrice());
		return new CartItemModel(object);
	}
	
	public String getImageUrl() {
		return selectedIndex == -1 ? imageUrl : combinationList.get(selectedIndex).getImageUrl();
	}
	
	public double getPrice() {
		return selectedIndex == -1 ? price : combinationList.get(selectedIndex).getPrice();
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
