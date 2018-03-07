package com.bigappcompany.quikmart.model;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 07 Aug 2017 at 3:48 PM
 */

public class CheckoutModel {
	private final ArrayList<PaymentModel> paymentList = new ArrayList<>();
	private double itemPrice;
	private double subTotal;
	private double discount;
	private double grandTotal;
	private DateModel date;
	private TimeModel time;
	private AddressModel address;
	private PaymentModel payment;
	private double deliveryCharges;
	private String razorOrderId;
	
	public CheckoutModel(JSONObject data) throws JSONException {
		JSONObject cart = data.getJSONObject("customer_cart_data");
		itemPrice = subTotal = cart.getDouble("cart_subtotal");
		grandTotal = cart.getDouble("cart_grand_total");
		discount = cart.optDouble("cart_discount", 0);
		deliveryCharges = cart.optDouble("delivery_charges", 0);
		
		JSONArray payments = data.getJSONArray("payment_methods");
		for (int i = 0; i < payments.length(); i++) {
			paymentList.add(new PaymentModel(payments.getJSONObject(i)));
		}
	}
	
	public double getItemPrice() {
		return itemPrice;
	}
	
	public double getSubTotal() {
		return subTotal;
	}
	
	public double getDiscount() {
		return discount;
	}
	
	public double getGrandTotal() {
		return grandTotal;
	}
	
	public ArrayList<PaymentModel> getPaymentList() {
		return paymentList;
	}
	
	public void setPayment(PaymentModel payment) {
		this.payment = payment;
	}
	
	public double getDeliveryCharges() {
		return deliveryCharges;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("user_selected_address_id", address.getAddressId());
		object.addProperty("user_selected_payment_method_id", payment.getId());
		object.addProperty("timeslot_date", date.getDate());
		object.addProperty("user_selected_timslot_id", time.getId());
		return object;
	}
	
	public AddressModel getAddress() {
		return address;
	}
	
	public void setAddress(AddressModel address) {
		this.address = address;
	}
	
	public DateModel getDate() {
		return date;
	}
	
	public void setDate(DateModel date) {
		this.date = date;
	}
	
	public TimeModel getTime() {
		return time;
	}
	
	public void setTime(TimeModel time) {
		this.time = time;
	}
	
	public String getRazorOrderId() {
		return razorOrderId;
	}
	
	public void setRazorOrderId(String razorOrderId) {
		this.razorOrderId = razorOrderId;
	}
	
	public JsonObject toJson(String transactionId) {
		JsonObject object = new JsonObject();
		object.addProperty("razorpay_order_id", razorOrderId);
		object.addProperty("razorpay_payment_id", transactionId);
		return object;
	}
}
