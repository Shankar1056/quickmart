package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.OrderAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.OrderModel;
import com.bigappcompany.quikmart.util.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends BaseActivity implements OrderAdapter.OnItemClickListener, OnApiListener {
	private OrderAdapter mAdapter;
	private LinearLayout emptyLL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order);
		super.onCreate(savedInstanceState);
		setTitle(R.string.my_orders);
		
		mAdapter = new OrderAdapter(this);
		RecyclerView orderRV = (RecyclerView) findViewById(R.id.rv_order);
		orderRV.setAdapter(mAdapter);
		
		emptyLL = (LinearLayout) findViewById(R.id.ll_empty);
		emptyLL.setVisibility(View.GONE);
		findViewById(R.id.btn_home).setOnClickListener(this);
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.ORDER + "/" + new Preference(this).getSessionKey())
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_orders)
			.exec();
	}
	
	@Override
	public void onItemClick(OrderModel item) {
		Intent intent = new Intent(this, OrderDetailsActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		JSONArray data = response.getJSONObject("data").getJSONArray("orders");
		
		if (data.length() > 0) {
			for (int i = 0; i < data.length(); i++) {
				mAdapter.addItem(new OrderModel(data.getJSONObject(i)));
			}
			emptyLL.setVisibility(View.GONE);
		} else {
			emptyLL.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
