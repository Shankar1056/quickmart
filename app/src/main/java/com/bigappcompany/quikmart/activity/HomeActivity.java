package com.bigappcompany.quikmart.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.CategoryAdapter;
import com.bigappcompany.quikmart.adapter.SubCategoryAdapter;
import com.bigappcompany.quikmart.adapter.ViewPagerAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.fragment.ImageFragment;
import com.bigappcompany.quikmart.model.CategoryModel;
import com.bigappcompany.quikmart.model.GroupModel;
import com.bigappcompany.quikmart.util.Preference;
import com.bigappcompany.quikmart.view.ViewPagerCustomDuration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends BaseActivity
	implements Runnable, ViewPager.OnPageChangeListener, OnApiListener, CategoryAdapter.OnItemClickListener, SubCategoryAdapter.OnItemClickListener {
	private static final String TAG = "HomeActivity";
	private static final int RC_SLIDERS = 1;
	private static final int RC_CATEGORIES = 2;
	private static final int RC_CART_COUNT = 3;
	
	private ViewPagerCustomDuration mBannerVP;
	private ViewPagerAdapter mAdapter;
	private CategoryAdapter mCategoryAdapter;
	private SubCategoryAdapter mSubCategoryAdapter;
	private Handler mBannerHandler;
	private boolean isAscending = true, userScrollChange = false, isBackPressed;
	private int prevPos = 0;
	private int previousState = ViewPager.SCROLL_STATE_IDLE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_home);
		if (savedInstanceState == null) {
			savedInstanceState = new Bundle();
		}
		
		savedInstanceState.putBoolean(IS_HOME, true);
		super.onCreate(savedInstanceState);
		
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		
		LinearLayout navigationLL = (LinearLayout) findViewById(R.id.ll_navigation);
		
		for (int i = 0; i < navigationLL.getChildCount(); i++) {
			navigationLL.getChildAt(i).setOnClickListener(this);
		}
		
		findViewById(R.id.et_search).setOnClickListener(this);
		
		mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mBannerVP = (ViewPagerCustomDuration) findViewById(R.id.vp_banner);
		mBannerVP.setScrollDurationFactor(3);
		mBannerVP.setAdapter(mAdapter);
		mBannerVP.addOnPageChangeListener(this);
		mBannerHandler = new Handler();
		
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(mBannerVP);
		
		mCategoryAdapter = new CategoryAdapter(this);
		RecyclerView categoryRV = (RecyclerView) findViewById(R.id.rv_category);
		categoryRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		categoryRV.setAdapter(mCategoryAdapter);
		
		mSubCategoryAdapter = new SubCategoryAdapter(this);
		RecyclerView subcategoryRV = (RecyclerView) findViewById(R.id.rv_subcategory);
		subcategoryRV.setNestedScrollingEnabled(false);
		subcategoryRV.setAdapter(mSubCategoryAdapter);
		
		// sliders
		ApiTask.builder(this)
			.setUrl(ApiUrl.SLIDERS)
			.setResponseListener(this)
			.setGET()
			.setRequestCode(RC_SLIDERS)
			.exec();
		
		// categories
		ApiTask.builder(this)
			.setUrl(ApiUrl.MAIN_CATEGORIES)
			.setRequestCode(RC_CATEGORIES)
			.setResponseListener(this)
			.setGET()
			.exec();
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.CART + "/" + new Preference(this).getSessionKey())
			.setRequestCode(RC_CART_COUNT)
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_cart_items)
			.exec();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mBannerHandler.postDelayed(this, 3000);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.nav_my_cart:
				startActivity(new Intent(this, CartActivity.class));
				break;
			
			case R.id.nav_my_orders:
				startActivity(new Intent(this, OrderActivity.class));
				break;
			
			case R.id.nav_my_addresses:
				startActivity(new Intent(this, AddressActivity.class));
				break;
			
			case R.id.nav_logout:
				logout();
				break;
			
			case R.id.nav_rate_us:
				rateUs();
				break;
			
			case R.id.nav_share:
				share();
				break;
			
			default:
				super.onClick(v);
		}
		
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
	}
	
	private void share() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share, getPackageName()));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
	
	private void logout() {
		new AlertDialog.Builder(this, R.style.AppThemeDialog)
			.setTitle(R.string.title_logout)
			.setMessage(R.string.msg_logout)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Preference pref = new Preference(HomeActivity.this);
					pref.setLoggedIn(false);
					pref.setSessionKey(null);
					finish();
				}
			})
			.create()
			.show();
	}
	
	private void rateUs() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		mBannerHandler.removeCallbacks(this);
	}
	
	/**
	 * throws divided by zero {@link ArithmeticException} if the #getChildCount() returns 0
	 */
	@Override
	public void run() {
		try {
			int nextPos;
			
			if (isAscending) {
				nextPos = (mBannerVP.getCurrentItem() + 1) % mAdapter.getCount();
				isAscending = nextPos < mAdapter.getCount() - 1;
			} else {
				nextPos = (mBannerVP.getCurrentItem() - 1) % mAdapter.getCount();
				isAscending = nextPos < 1;
			}
			
			mBannerVP.setCurrentItem(nextPos, true);
			mBannerHandler.postDelayed(this, 3000);
		} catch (ArithmeticException e) {
			Log.w(TAG, e.getMessage(), e);
		}
	}
	
	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
			isBackPressed = false;
		} else if (!isBackPressed) {
			isBackPressed = true;
			Toast.makeText(this, R.string.toast_back_press, Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isBackPressed = false;
				}
			}, 3000);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}
	
	@Override
	public void onPageSelected(int position) {
		if (userScrollChange) {
			isAscending = prevPos < position && position < mAdapter.getCount() - 1 && position > 0;
		}
		
		prevPos = position;
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING) {
			userScrollChange = true;
		} else if (previousState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
			userScrollChange = false;
		}
		
		previousState = state;
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_SLIDERS:
				JSONArray data = response.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);
					mAdapter.addItem(ImageFragment.newInstance(object.getString("homepage_slider_image")));
				}
				
				break;
			
			case RC_CATEGORIES:
				JSONArray categories = response.getJSONObject("data").getJSONArray("main_categories");
				for (int i = 0; i < categories.length(); i++) {
					CategoryModel category = new CategoryModel(categories.getJSONObject(i));
					mCategoryAdapter.addItem(category);
					mSubCategoryAdapter.addItem(category);
				}
				break;
			
			case RC_CART_COUNT:
				data = response.getJSONObject("data").getJSONArray("cart_items");
				updateCartCount(data.length());
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onItemClick(CategoryModel item) {
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onViewAll(CategoryModel item) {
		Intent intent = new Intent(this, CategoryActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onItemClick(GroupModel item) {
		Intent intent = new Intent(this, ProductListActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
}
