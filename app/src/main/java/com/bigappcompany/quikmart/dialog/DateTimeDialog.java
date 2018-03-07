package com.bigappcompany.quikmart.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.adapter.DateAdapter;
import com.bigappcompany.quikmart.adapter.TimeAdapter;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.DateModel;
import com.bigappcompany.quikmart.model.TimeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Aug 2017 at 12:39 PM
 */

@SuppressWarnings("ConstantConditions")
public class DateTimeDialog extends DialogFragment implements OnApiListener, View.OnClickListener, DateAdapter.OnItemClickListener {
	private DateAdapter mDateAdapter;
	private TimeAdapter mTimeAdapter;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, R.style.AppThemeDialog);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_date_time, container, false);
		
		mDateAdapter = new DateAdapter(this);
		RecyclerView dateRV = (RecyclerView) view.findViewById(R.id.rv_date);
		dateRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		dateRV.setAdapter(mDateAdapter);
		
		mTimeAdapter = new TimeAdapter();
		RecyclerView timeRV = (RecyclerView) view.findViewById(R.id.rv_time);
		timeRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		timeRV.setAdapter(mTimeAdapter);
		
		view.findViewById(R.id.tv_cancel).setOnClickListener(this);
		view.findViewById(R.id.tv_done).setOnClickListener(this);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		
		ApiTask.builder(getContext())
			.setGET()
			.setUrl(ApiUrl.TIME_SLOT)
			.setResponseListener(this)
			.setProgressMessage(R.string.fetching_time_slots)
			.exec();
		
		return view;
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		JSONArray data = response.getJSONArray("data");
		for (int i = 0; i < data.length(); i++) {
			mTimeAdapter.addItem(new TimeModel(data.getJSONObject(i)));
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_cancel:
				dismiss();
				break;
			
			case R.id.tv_done:
				dismiss();
				((OnDateTimeListener) getActivity())
					.onDateTime(mDateAdapter.getSelectedDate(), mTimeAdapter.getSelectedTime());
				break;
		}
	}
	
	@Override
	public void onDateChanged(int position) {
		mTimeAdapter.notifyDateChanged(position);
	}
	
	public interface OnDateTimeListener {
		void onDateTime(DateModel date, TimeModel time);
	}
}
