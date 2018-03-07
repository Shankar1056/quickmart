package com.bigappcompany.quikmart.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.CartAdapter;
import com.bigappcompany.quikmart.adapter.CombinationAdapter;
import com.bigappcompany.quikmart.adapter.ViewPagerAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.fragment.TextFragment;
import com.bigappcompany.quikmart.listener.OnCartListener;
import com.bigappcompany.quikmart.model.CartItemModel;
import com.bigappcompany.quikmart.model.ProductModel;
import com.bigappcompany.quikmart.util.Preference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ConstantConditions")
public class ProductDetailsActivity extends BaseActivity
	implements CombinationAdapter.OnItemClickListener, OnApiListener, OnCartListener {
	private static final int RC_DETAILS = 1;
	private static final int RC_CART_ITEMS = 2;
	
	private ProductModel product;
	private TextView combinationTV, quantityTV, priceTV, decQtyTV;
	private Dialog dialog;
	private ViewPagerAdapter mAdapter;
	
	private CartAdapter mCartAdapter;
	private TextView cartPriceTV, cartCountTV;
	private SlidingUpPanelLayout sliderSUPL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_product_details);
		super.onCreate(savedInstanceState);
		
		sliderSUPL = (SlidingUpPanelLayout) findViewById(R.id.slider);
		
		product = (ProductModel) getIntent().getSerializableExtra(EXTRA_DATA);
		getSupportActionBar().setTitle(product.getTitle());
		
		if (!product.getDiscount().isEmpty()) {
			((TextView) findViewById(R.id.tv_off)).setText(product.getDiscount());
		} else {
			findViewById(R.id.tv_off).setVisibility(View.GONE);
		}
		
		Picasso.with(this)
			.load(product.getImageUrl())
			.fit()
			.centerInside()
			.into((ImageView) findViewById(R.id.iv_image));
		
		((TextView) findViewById(R.id.tv_title)).setText(product.getTitle());
		
		combinationTV = (TextView) findViewById(R.id.tv_combination);
		combinationTV.setOnClickListener(this);
		priceTV = (TextView) findViewById(R.id.tv_price);
		
		quantityTV = (TextView) findViewById(R.id.tv_quantity);
		
		decQtyTV = (TextView) findViewById(R.id.tv_dec_quantity);
		decQtyTV.setOnClickListener(this);
		findViewById(R.id.tv_inc_quantity).setOnClickListener(this);
		
		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		ViewPager descVP = (ViewPager) findViewById(R.id.vp_content);
		descVP.setAdapter(mAdapter);
		
		((TabLayout) findViewById(R.id.tl_product)).setupWithViewPager(descVP);
		
		mCartAdapter = new CartAdapter(this);
		RecyclerView cartRV = (RecyclerView) findViewById(R.id.rv_cart);
		cartRV.setAdapter(mCartAdapter);
		cartRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		
		cartPriceTV = (TextView) findViewById(R.id.tv_total_amount);
		cartCountTV = (TextView) findViewById(R.id.tv_cart_count);
		
		init();
		setQuantity(false);
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.GET_PRODUCT_DETAILS + product.getProductId())
			.setProgressMessage(R.string.loading_product_details)
			.setRequestCode(RC_DETAILS)
			.setResponseListener(this)
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
			case R.id.tv_combination:
				showCombinationDialog();
				break;
			
			case R.id.tv_dec_quantity:
				product.setQuantity(product.getQuantity() - 1);
				setQuantity(true);
				break;
			
			case R.id.tv_inc_quantity:
				product.setQuantity(product.getQuantity() + 1);
				setQuantity(true);
				break;
			
			default:
				super.onClick(v);
		}
	}
	
	private void showCombinationDialog() {
		dialog = new Dialog(this, R.style.AppThemeDialog);
		dialog.setContentView(R.layout.dialog_combination);
		dialog.show();
		RecyclerView combinationRV = (RecyclerView) dialog.findViewById(R.id.rv_combination);
		combinationRV.setLayoutManager(new LinearLayoutManager(this));
		combinationRV.setHasFixedSize(true);
		combinationRV.setAdapter(new CombinationAdapter(product, this));
	}
	
	private void setQuantity(boolean updateToServer) {
		quantityTV.setText(String.valueOf(product.getQuantity()));
		
		if (product.getQuantity() < 1) {
			decQtyTV.setVisibility(View.INVISIBLE);
			quantityTV.setVisibility(View.INVISIBLE);
		} else {
			decQtyTV.setVisibility(View.VISIBLE);
			quantityTV.setVisibility(View.VISIBLE);
		}
		
		if (updateToServer) {
			ApiTask.builder(this)
				.setUrl(ApiUrl.CART)
				.setRequestBody(product.toJson())
				.setProgressMessage(R.string.updating_cart)
				.setRequestCode(RC_CART_ITEMS)
				.setResponseListener(this)
				.exec();
		}
	}
	
	private void init() {
		priceTV.setText(getString(R.string.format_price, product.getPrice()));
		if (product.getSelectedIndex() > -1) {
			combinationTV.setText(product.getCombinationName());
		} else {
			combinationTV.setVisibility(View.GONE);
		}
		
		Picasso.with(this)
			.load(product.getImageUrl())
			.fit()
			.centerInside()
			.into((ImageView) findViewById(R.id.iv_image));
	}
	
	@Override
	public void onItemClick(int position) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		product.setSelectedIndex(position);
		
		init();
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_DETAILS:
				parseDetails(response);
				break;
			
			case RC_CART_ITEMS:
				parseCartItems(response);
				break;
		}
	}
	
	private void parseDetails(JSONObject response) throws JSONException {
		JSONObject object = response.getJSONObject("data").getJSONArray("products").getJSONObject(0);
		String about = object.optString("product_about");
		String ingredients = object.optString("product_ingredients");
		String nutritional = object.optString("product_nutritional");
		
		if (!about.isEmpty()) {
			mAdapter.addItem(TextFragment.newInstance(about), "About");
		}
		
		if (!ingredients.isEmpty()) {
			mAdapter.addItem(TextFragment.newInstance(ingredients), "Ingredients");
		}
		
		if (!nutritional.isEmpty()) {
			mAdapter.addItem(TextFragment.newInstance(nutritional), "Nutritional Facts");
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	private void parseCartItems(JSONObject response) throws JSONException {
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
	}
	
	@Override
	public void onBackPressed() {
		if (sliderSUPL.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			sliderSUPL.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		} else {
			super.onBackPressed();
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
}
