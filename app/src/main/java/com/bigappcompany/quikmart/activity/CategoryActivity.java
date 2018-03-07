package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.AllCategoryAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.CategoryModel;
import com.bigappcompany.quikmart.model.GroupModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryActivity extends BaseActivity implements OnApiListener, AllCategoryAdapter.OnItemClickListener {
	private AllCategoryAdapter mAdapter;
	private CategoryModel mCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_category);
		super.onCreate(savedInstanceState);
		
		mCategory = (CategoryModel) getIntent().getSerializableExtra(EXTRA_DATA);
		
		mAdapter = new AllCategoryAdapter(this);
		RecyclerView categoryRV = (RecyclerView) findViewById(R.id.rv_category);
		categoryRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		categoryRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
		categoryRV.setAdapter(mAdapter);
		
		ApiTask.builder(this)
			.setUrl(ApiUrl.SUBCATEGORIES + mCategory.getCategoryId())
			.setGET()
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_category)
			.exec();
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		JSONArray data = response.getJSONObject("data").getJSONArray("sub_categories");
		
		for (int i = 0; i < data.length(); i++) {
			mAdapter.addItem(new GroupModel(data.getJSONObject(i)));
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onItemClick(GroupModel item) {
		Intent intent = new Intent(this, ProductListActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
}
