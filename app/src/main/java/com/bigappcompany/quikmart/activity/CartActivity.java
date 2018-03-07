package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.CartAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.listener.OnCartListener;
import com.bigappcompany.quikmart.model.CartItemModel;
import com.bigappcompany.quikmart.util.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartActivity extends BaseActivity implements OnApiListener, OnCartListener {
	private static final int RC_ITEMS = 1;
	
	private CartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_cart);
		super.onCreate(savedInstanceState);
		
		mAdapter = new CartAdapter(this);
		RecyclerView cartRV = (RecyclerView) findViewById(R.id.rv_cart);
		cartRV.setLayoutManager(new LinearLayoutManager(this));
		cartRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		cartRV.setAdapter(mAdapter);
		
		findViewById(R.id.tv_checkout).setOnClickListener(this);
		findViewById(R.id.btn_home).setOnClickListener(this);
		
		findViewById(R.id.ll_content).setVisibility(View.GONE);
		findViewById(R.id.ll_empty).setVisibility(View.GONE);
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.CART + "/" + new Preference(this).getSessionKey())
			.setRequestCode(RC_ITEMS)
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_cart_items)
			.exec();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_checkout:
				startActivity(new Intent(this, CheckoutActivity.class));
				break;
			
			default:
				super.onClick(v);
		}
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_ITEMS:
				parseCartItems(response);
				break;
		}
	}
	
	private void parseCartItems(JSONObject response) throws JSONException {
		JSONArray data = response.getJSONObject("data").getJSONArray("cart_items");
		if (data.length() > 0) {
			mAdapter.clear();
			for (int i = 0; i < data.length(); i++) {
				mAdapter.addItem(new CartItemModel(data.getJSONObject(i)));
			}
			
			updateCartCount(mAdapter.getItemCount());
			
			JSONObject cart = response.getJSONObject("data");
			double subTotal = cart.getDouble("cart_subtotal");
			double shipping = cart.getDouble("delivery_charges");
			double grandTotal = cart.getDouble("cart_grand_total");
			
			((TextView) findViewById(R.id.tv_label_sub_total))
				.setText(getString(R.string.format_cart_subtotal, mAdapter.getItemCount()));
			((TextView) findViewById(R.id.tv_sub_total)).setText(getString(R.string.format_price, subTotal));
			((TextView) findViewById(R.id.tv_shipping_charge)).setText(getString(R.string.format_price, shipping));
			((TextView) findViewById(R.id.tv_grand_total)).setText(getString(R.string.format_price, grandTotal));
			findViewById(R.id.ll_empty).setVisibility(View.GONE);
			findViewById(R.id.ll_content).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.ll_empty).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_content).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onCartUpdate(CartItemModel item) {
		ApiTask.builder(this)
			.setUrl(ApiUrl.CART)
			.setRequestBody(item.toJson())
			.setProgressMessage(R.string.updating_cart)
			.setRequestCode(RC_ITEMS)
			.setResponseListener(this)
			.exec();
	}
}
