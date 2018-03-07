package com.bigappcompany.quikmart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.activity.ProductDetailsActivity;
import com.bigappcompany.quikmart.adapter.ProductListAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.ProductModel;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.bigappcompany.quikmart.activity.BaseActivity.EXTRA_DATA;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 31 Jul 2017 at 12:10 PM
 */

public class CategoryFragment extends Fragment implements Paginate.Callbacks, OnApiListener, ProductListAdapter.OnItemClickListener {
	private static final String ARG_ID = "arg_id";
	private static final int RC_PRODUCTS = 1;
	private static final int RC_CART_UPDATE = 2;
	
	private String id;
	private ProductListAdapter mAdapter;
	private Paginate mPaginate;
	
	private int mProductIndex = 0;
	private boolean isLoading = false, hasLoadedAll = false;
	
	public static CategoryFragment newInstance(String id) {
		Bundle args = new Bundle();
		args.putString(ARG_ID, id);
		CategoryFragment fragment = new CategoryFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		
		mAdapter = new ProductListAdapter(this);
		RecyclerView listRV = (RecyclerView) view;
		listRV.setAdapter(mAdapter);
		listRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		
		id = getArguments().getString(ARG_ID);
		
		mProductIndex = 0;
		isLoading = false;
		hasLoadedAll = false;
		mPaginate = Paginate.with(listRV, this)
			.setLoadingTriggerThreshold(2)
			.addLoadingListItem(true)
			.setLoadingListItemCreator(new CustomLoadingListItemCreator())
			.setLoadingListItemSpanSizeLookup(new CustomLoadingListItemSpanLookup())
			.build();
		mPaginate.setHasMoreDataToLoad(true);
		
		return view;
	}
	
	@Override
	public void onLoadMore() {
		isLoading = true;
		ApiTask.builder(getContext())
			.setUrl(ApiUrl.PRODUCTS_BY_CATEGORY + id + "/" + mProductIndex)
			.setGET()
			.setRequestCode(RC_PRODUCTS)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public boolean isLoading() {
		return isLoading;
	}
	
	@Override
	public boolean hasLoadedAllItems() {
		return hasLoadedAll;
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_PRODUCTS:
				isLoading = false;
				mProductIndex += 10;
				JSONArray data = response.getJSONObject("data").getJSONArray("products");
				
				if (data.length() > 0) {
					for (int i = 0; i < data.length(); i++) {
						mAdapter.addItem(new ProductModel(data.getJSONObject(i)));
					}
				} else {
					hasLoadedAll = true;
					mPaginate.setHasMoreDataToLoad(false);
				}
				break;
			
			case RC_CART_UPDATE:
				((OnProductListener) getActivity()).onCartUpdate(response);
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onItemClick(ProductModel item) {
		Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onQuantityUpdate(ProductModel item) {
		ApiTask.builder(getContext())
			.setUrl(ApiUrl.CART)
			.setRequestBody(item.toJson())
			.setProgressMessage(R.string.updating_cart)
			.setRequestCode(RC_CART_UPDATE)
			.setResponseListener(this)
			.exec();
	}
	
	public interface OnProductListener {
		void onCartUpdate(JSONObject response);
	}
	
	private class CustomLoadingListItemCreator implements LoadingListItemCreator {
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = getActivity().getLayoutInflater().inflate(R.layout.loading_row, parent, false);
			return new ViewHolder(view);
		}
		
		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
			
		}
		
		class ViewHolder extends RecyclerView.ViewHolder {
			ViewHolder(View itemView) {
				super(itemView);
			}
		}
	}
	
	private class CustomLoadingListItemSpanLookup implements LoadingListItemSpanLookup {
		@Override
		public int getSpanSize() {
			return 1;
		}
	}
}
