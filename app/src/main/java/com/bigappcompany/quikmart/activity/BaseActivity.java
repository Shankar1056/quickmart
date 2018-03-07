package com.bigappcompany.quikmart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.util.Preference;
import com.razorpay.Checkout;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bigappcompany.quikmart.R.id.toolbar;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 13 Mar 2017 at 6:38 PM
 */

@SuppressWarnings("ConstantConditions")
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
	public static final String EXTRA_DATA = "extra_data";
	protected static final String IS_HOME = "is_home";
	private static final String TAG = "BaseActivity";

	private static int cartCount = 0;
	
	protected Toolbar mToolbar;
	private Handler mCartHandler;
	private TextView cartCountTV;
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mToolbar = (Toolbar) findViewById(toolbar);
		setSupportActionBar(mToolbar);
		
		if (savedInstanceState == null || !savedInstanceState.getBoolean(IS_HOME, false)) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		mCartHandler = new Handler();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (cartCountTV != null) {
			updateCartCount(cartCount);
		}
	}
	
	protected void updateCartCount(int count) {
		cartCount = count;
		if (cartCount > 0) {
			cartCountTV.setVisibility(View.VISIBLE);
			cartCountTV.setText(String.valueOf(cartCount));
		} else {
			cartCountTV.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		
		final MenuItem item = menu.findItem(R.id.action_cart);
		MenuItemCompat.setActionView(item, R.layout.cart_item_count);
		
		cartCountTV = (TextView) item.getActionView().findViewById(R.id.tv_notif_count);
		updateCartCount(cartCount);
		
		final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.shake);
		mCartHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				cartCountTV.startAnimation(rotation);
				mCartHandler.postDelayed(this, 5000);
			}
		}, 5000);
		
		item.getActionView().setOnClickListener(this);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	final void makePayment(double amount, String orderId) {
		Checkout co = new Checkout();
		Preference preference = new Preference(this);
		
		try {
			JSONObject options = new JSONObject();
			options.put("name", "Quikmart");
			options.put("description", "Quikmart payment");
			options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
			options.put("currency", "INR");
			options.put("amount", amount * 100);
			options.put("order_id", orderId);
			
			JSONObject preFill = new JSONObject();
			preFill.put("email", preference.getEmail());
			preFill.put("contact", preference.getMobile());
			
			options.put("prefill", preFill);
			
			co.open(this, options);
		} catch (Exception e) {
			Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.action_cart:
				startActivity(new Intent(this, CartActivity.class));
				break;
			
			case R.id.btn_home:
				startActivity(new Intent(this, HomeActivity.class));
				finish();
				break;
		}
	}
}
