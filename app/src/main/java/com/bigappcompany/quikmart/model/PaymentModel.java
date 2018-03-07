package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 07 Aug 2017 at 3:56 PM
 */

public class PaymentModel {
	private final int id;
	private final String paymentTitle;
	
	public PaymentModel(JSONObject payment) throws JSONException {
		id = payment.getInt("paymentmehtod_id");
		paymentTitle = payment.getString("paymentmethods_name");
	}
	
	public int getId() {
		return id;
	}
	
	public String getPaymentTitle() {
		return paymentTitle;
	}
}
