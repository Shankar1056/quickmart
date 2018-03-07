package com.bigappcompany.quikmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.AddressAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.dialog.AddressDialog;
import com.bigappcompany.quikmart.model.AddressModel;
import com.bigappcompany.quikmart.util.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ConstantConditions")
public class AddressActivity extends BaseActivity implements OnApiListener, AddressDialog.OnAddressListener, AddressAdapter.OnItemClickListener {
	private static final int RC_ITEMS = 1;
	private static final int RC_UPDATE = 2;
	private static final int RC_DELETE = 3;
	
	private static final String SAVED_DATA = "saved_data";
	private static final String POSITION = "position";
	
	private AddressAdapter mAdapter;
	private TextView emptyTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_address);
		super.onCreate(savedInstanceState);
		
		mAdapter = new AddressAdapter(this);
		RecyclerView addressRV = (RecyclerView) findViewById(R.id.rv_address);
		addressRV.setLayoutManager(new LinearLayoutManager(this));
		addressRV.setAdapter(mAdapter);
		
		findViewById(R.id.fab_add).setOnClickListener(this);
		
		TextView proceedTV = (TextView) findViewById(R.id.tv_proceed_to_pay);
		proceedTV.setOnClickListener(this);
		proceedTV.setVisibility(getCallingActivity() != null ? View.VISIBLE : View.GONE);
		
		emptyTV = (TextView) findViewById(R.id.tv_empty);
		emptyTV.setVisibility(View.GONE);
		
		ApiTask.builder(this)
			.setGET()
			.setUrl(ApiUrl.ADDRESS + new Preference(this).getSessionKey())
			.setRequestCode(RC_ITEMS)
			.setProgressMessage(R.string.loading_addresses)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab_add:
				AddressDialog dialog = AddressDialog.newInstance(new AddressModel(), -1);
				dialog.show(getSupportFragmentManager(), null);
				break;
			
			case R.id.tv_proceed_to_pay:
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DATA, mAdapter.getSelectedAddress());
				setResult(RESULT_OK, intent);
				finish();
				break;
			
			default:
				super.onClick(v);
		}
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		switch (requestCode) {
			case RC_ITEMS:
				JSONArray data = response.getJSONObject("data").getJSONArray("address");
				if (data.length() > 0) {
					for (int i = 0; i < data.length(); i++) {
						mAdapter.addItem(new AddressModel(data.getJSONObject(i)));
					}
					emptyTV.setVisibility(View.GONE);
				} else {
					emptyTV.setVisibility(View.VISIBLE);
				}
				break;
			
			case RC_UPDATE:
				int position = savedData.getInt(POSITION);
				AddressModel address = (AddressModel) savedData.getSerializable(SAVED_DATA);
				if (position == -1) {
					address.setAddressId(response.getString("data"));
					mAdapter.addItem(address);
				} else {
					mAdapter.updateItem(address, position);
				}
				emptyTV.setVisibility(View.GONE);
				break;
			
			case RC_DELETE:
				position = savedData.getInt(POSITION);
				mAdapter.deleteItem(position);
				
				if (mAdapter.getItemCount() == 0) {
					emptyTV.setVisibility(View.VISIBLE);
				}
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onAddAddress(AddressModel address, int position) {
		Bundle data = new Bundle();
		data.putSerializable(SAVED_DATA, address);
		data.putInt(POSITION, position);
		
		ApiTask.builder(this)
			.setUrl(ApiUrl.ADDRESS)
			.setRequestBody(address.toJson())
			.setProgressMessage(R.string.updating_address)
			.setRequestCode(RC_UPDATE)
			.setSavedData(data)
			.setResponseListener(this)
			.exec();
	}
	
	@Override
	public void onUpdate(AddressModel item, int position) {
		AddressDialog dialog = AddressDialog.newInstance(item, position);
		dialog.show(getSupportFragmentManager(), null);
	}
	
	@Override
	public void onDelete(AddressModel item, int position) {
		Bundle data = new Bundle();
		data.putSerializable(SAVED_DATA, item);
		data.putInt(POSITION, position);
		
		ApiTask.builder(this)
			.setDELETE()
			.setUrl(ApiUrl.ADDRESS)
			.setRequestBody(item.toJson())
			.setProgressMessage(R.string.updating_address)
			.setRequestCode(RC_DELETE)
			.setSavedData(data)
			.setResponseListener(this)
			.exec();
	}
}
