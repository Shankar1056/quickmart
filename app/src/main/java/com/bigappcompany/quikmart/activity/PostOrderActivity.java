package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.bigappcompany.quikmart.R;

public class PostOrderActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_post_order);
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.order_placed);
		findViewById(R.id.btn_home).setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}
}
