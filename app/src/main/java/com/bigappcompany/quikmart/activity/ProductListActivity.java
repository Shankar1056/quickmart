package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.CartAdapter;
import com.bigappcompany.quikmart.adapter.ViewPagerAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.fragment.CategoryFragment;
import com.bigappcompany.quikmart.listener.OnCartListener;
import com.bigappcompany.quikmart.model.CartItemModel;
import com.bigappcompany.quikmart.model.GroupModel;
import com.bigappcompany.quikmart.util.Preference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ConstantConditions")
public class ProductListActivity extends BaseActivity implements OnApiListener, CategoryFragment.OnProductListener, OnCartListener {
	private static final String TAG = "ProductListActivity";
	private static final int RC_CATEGORY = 1;
	private static final int RC_CART_ITEMS = 2;
	private ViewPagerAdapter mAdapter;
	private CartAdapter mCartAdapter;
	private TextView cartPriceTV, cartCountTV;
	private SlidingUpPanelLayout sliderSUPL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_product_list);
		super.onCreate(savedInstanceState);
		
		sliderSUPL = (SlidingUpPanelLayout) findViewById(R.id.slider);
		
		GroupModel group = (GroupModel) getIntent().getSerializableExtra(EXTRA_DATA);
		getSupportActionBar().setTitle(group.getTitle());
		
		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		ViewPager listVP = (ViewPager) findViewById(R.id.vp_list);
		listVP.setAdapter(mAdapter);
		
		TabLayout listTL = (TabLayout) findViewById(R.id.tl_list);
		listTL.setupWithViewPager(listVP);
		
		mCartAdapter = new CartAdapter(this);
		RecyclerView cartRV = (RecyclerView) findViewById(R.id.rv_cart);
		cartRV.setAdapter(mCartAdapter);
		cartRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		
		cartPriceTV = (TextView) findViewById(R.id.tv_total_amount);
		cartCountTV = (TextView) findViewById(R.id.tv_cart_count);
		
		findViewById(R.id.iv_checkout).setOnClickListener(this);
		
		ApiTask.builder(this)
			.setUrl(ApiUrl.CATEGORY_BY_GROUP + group.getGroupId())
			.setGET()
			.setRequestCode(RC_CATEGORY)
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_categories)
			.exec();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.CART + "/" + new Preference(this).getSessionKey())
			.setRequestCode(RC_CART_ITEMS)
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_cart_items)
			.exec();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_checkout:
				startActivity(new Intent(this, CartActivity.class));
				break;
			
			default:
				super.onClick(v);
		}
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_CATEGORY:
				JSONArray data = response.getJSONObject("data").getJSONArray("sub_categories");
				
				for (int i = 0; i < data.length(); i++) {
					JSONObject category = data.getJSONObject(i);
					mAdapter.addItem(
						CategoryFragment.newInstance(category.getString("id_category")),
						category.getString("name")
					);
				}
				break;
			
			case RC_CART_ITEMS:
				onCartUpdate(response);
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onCartUpdate(JSONObject response) {
		try {
			mCartAdapter.clear();
			JSONArray data = response.getJSONObject("data").getJSONArray("cart_items");
			for (int i = 0; i < data.length(); i++) {
				mCartAdapter.addItem(new CartItemModel(data.getJSONObject(i)));
			}
			updateCartCount(mCartAdapter.getItemCount());
			
			if (mCartAdapter.getItemCount() > 0) {
				if (sliderSUPL.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
					sliderSUPL.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
				}
				cartPriceTV.setText(getString(R.string.format_price, mCartAdapter.getTotalPrice()));
				cartCountTV.setText(String.valueOf(mCartAdapter.getItemCount()));
			} else {
				sliderSUPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	@Override
	public void onCartUpdate(CartItemModel item) {
		ApiTask.builder(this)
			.setUrl(ApiUrl.CART)
			.setRequestBody(item.toJson())
			.setProgressMessage(R.string.updating_cart)
			.setRequestCode(RC_CART_ITEMS)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public void onBackPressed() {
		if (sliderSUPL.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			sliderSUPL.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		} else {
			super.onBackPressed();
		}
	}
}
