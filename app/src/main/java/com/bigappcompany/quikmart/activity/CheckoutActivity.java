package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.dialog.DateTimeDialog;
import com.bigappcompany.quikmart.model.AddressModel;
import com.bigappcompany.quikmart.model.CheckoutModel;
import com.bigappcompany.quikmart.model.DateModel;
import com.bigappcompany.quikmart.model.PaymentModel;
import com.bigappcompany.quikmart.model.TimeModel;
import com.bigappcompany.quikmart.util.Preference;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CheckoutActivity extends BaseActivity
	implements OnApiListener, DateTimeDialog.OnDateTimeListener, PaymentResultWithDataListener {
	private static final int RC_ADDRESS = 1;
	private static final int RC_ORDER = 2;
	private static final int RC_CHECKOUT = 3;
	private static final int RC_PAYMENT = 4;
	
	private CheckoutModel checkout;
	private RadioGroup paymentRG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_checkout);
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.tv_label_address).setOnClickListener(this);
		findViewById(R.id.tv_label_date_time).setOnClickListener(this);
		findViewById(R.id.tv_proceed_to_pay).setOnClickListener(this);
		
		paymentRG = (RadioGroup) findViewById(R.id.rg_payment);
		
		ApiTask.builder(this)
			.setGET()
			.setRequestCode(RC_CHECKOUT)
			.setUrl(ApiUrl.CHECKOUT + new Preference(this).getSessionKey())
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_checkout_items)
			.exec();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_label_address:
				Intent intent = new Intent(this, AddressActivity.class);
				startActivityForResult(intent, RC_ADDRESS);
				break;
			
			case R.id.tv_label_date_time:
				DateTimeDialog dialog = new DateTimeDialog();
				dialog.show(getSupportFragmentManager(), null);
				break;
			
			case R.id.tv_proceed_to_pay:
				if (isInputValid()) {
					ApiTask.builder(this)
						.setUrl(ApiUrl.ORDER)
						.setRequestBody(checkout.toJson())
						.setRequestCode(RC_ORDER)
						.setProgressMessage(R.string.processing_order)
						.setResponseListener(this)
						.exec();
				}
				break;
			
			default:
				super.onClick(v);
		}
	}
	
	private boolean isInputValid() {
		// Check shipping address
		if (checkout.getAddress() == null) {
			startActivityForResult(new Intent(this, AddressActivity.class), RC_ADDRESS);
			return false;
		}
		
		// check shipping time
		if (checkout.getDate() == null || checkout.getTime() == null) {
			DateTimeDialog dialog = new DateTimeDialog();
			dialog.show(getSupportFragmentManager(), null);
			return false;
		}
		
		// check payment method
		if (paymentRG.getCheckedRadioButtonId() != -1) {
			int index = paymentRG.indexOfChild(paymentRG.findViewById(paymentRG.getCheckedRadioButtonId()));
			checkout.setPayment(checkout.getPaymentList().get(index));
		} else {
			Toast.makeText(this, R.string.select_payment_method, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_CHECKOUT:
				parseCheckout(response);
				break;
			
			case RC_ORDER:
				JSONObject data = response.getJSONObject("data");
				JSONObject order = data.getJSONObject("order_info");
				JSONObject payment = data.getJSONObject("payment_info");
				
				if (payment.getString("payment_type").equalsIgnoreCase("ONLINE")) {
					checkout.setRazorOrderId(payment.getString("razorpay_order_id"));
					makePayment(order.getDouble("order_total"), checkout.getRazorOrderId());
				} else {
					updateCartCount(0);
					startActivity(new Intent(this, PostOrderActivity.class));
				}
				break;
			
			case RC_PAYMENT:
				updateCartCount(0);
				startActivity(new Intent(this, PostOrderActivity.class));
				break;
		}
	}
	
	private void parseCheckout(JSONObject response) throws JSONException {
		checkout = new CheckoutModel(response.getJSONObject("data"));
		((TextView) findViewById(R.id.tv_item))
			.setText(getString(R.string.format_price, checkout.getItemPrice()));
		((TextView) findViewById(R.id.tv_sub_total))
			.setText(getString(R.string.format_price, checkout.getSubTotal()));
		((TextView) findViewById(R.id.tv_delivery))
			.setText(getString(R.string.format_price, checkout.getDeliveryCharges()));
		((TextView) findViewById(R.id.tv_discount))
			.setText(getString(R.string.format_price, checkout.getDiscount()));
		((TextView) findViewById(R.id.tv_grand_total))
			.setText(getString(R.string.format_price, checkout.getGrandTotal()));
		
		ArrayList<PaymentModel> paymentList = checkout.getPaymentList();
		for (PaymentModel payment : paymentList) {
			RadioButton payRB = (RadioButton) getLayoutInflater().inflate(R.layout.item_payment, paymentRG, false);
			payRB.setId(payment.getId());
			payRB.setText(payment.getPaymentTitle());
			paymentRG.addView(payRB);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onDateTime(DateModel date, TimeModel time) {
		checkout.setDate(date);
		checkout.setTime(time);
		
		((TextView) findViewById(R.id.tv_date_time)).setText(date.getDate() + ", " + time.getTimeSlot());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_ADDRESS && resultCode == RESULT_OK) {
			checkout.setAddress((AddressModel) data.getSerializableExtra(EXTRA_DATA));
			((TextView) findViewById(R.id.tv_address)).setText(checkout.getAddress().toString());
		}
	}
	
	@Override
	public void onPaymentSuccess(String s, PaymentData paymentData) {
		ApiTask.builder(this)
			.setUrl(ApiUrl.PAYMENT_UPDATE)
			.setRequestBody(checkout.toJson(s))
			.setProgressMessage(R.string.processing_payment)
			.setRequestCode(RC_PAYMENT)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public void onPaymentError(int i, String s, PaymentData paymentData) {
		
	}
}
