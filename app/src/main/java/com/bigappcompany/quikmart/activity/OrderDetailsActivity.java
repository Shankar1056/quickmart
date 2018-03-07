package com.bigappcompany.quikmart.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.OrderDetailsAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.OrderDetailsModel;
import com.bigappcompany.quikmart.model.OrderModel;
import com.bigappcompany.quikmart.util.Preference;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailsActivity extends BaseActivity implements OnApiListener {
	private static final int RC_DETAILS = 1;
	private OrderDetailsAdapter mAdapter;
	private OrderModel mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_order_details);
		super.onCreate(savedInstanceState);
		
		mAdapter = new OrderDetailsAdapter();
		RecyclerView orderRV = (RecyclerView) findViewById(R.id.rv_order);
		orderRV.setAdapter(mAdapter);
		
		mOrder = (OrderModel) getIntent().getSerializableExtra(EXTRA_DATA);
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.ORDER_DETAILS + mOrder.getOrderId() + "/" + new Preference(this).getSessionKey())
			.setProgressMessage(R.string.loading_order_details)
			.setRequestCode(RC_DETAILS)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		JSONObject data = response.getJSONObject("data").getJSONArray("orders").getJSONObject(0);
		OrderDetailsModel order = new OrderDetailsModel(data);
		
		((TextView) findViewById(R.id.tv_order_id)).setText(getString(R.string.format_order_id, mOrder.getOrderId()));
		((TextView) findViewById(R.id.tv_total)).setText(getString(R.string.format_price, mOrder.getTotal()));
		((TextView) findViewById(R.id.tv_order_status)).setText(mOrder.getOrderStatus());
		if (order.getPromo() > 0) {
			((TextView) findViewById(R.id.tv_promo)).setText(getString(R.string.format_price, order.getPromo()));
		} else {
			findViewById(R.id.ll_promo).setVisibility(View.GONE);
		}
		
		mAdapter.addItems(order.getItemList());
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
