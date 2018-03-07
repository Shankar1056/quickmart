package com.bigappcompany.quikmart.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.model.AddressModel;
import com.bigappcompany.quikmart.model.StateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 09 Aug 2017 at 3:57 PM
 */

@SuppressWarnings("ConstantConditions")
public class AddressDialog extends DialogFragment implements View.OnClickListener, OnApiListener {
	private static final String ARG_ADDRESS = "arg_address";
	private static final String ARG_POSITION = "arg_position";
	
	private TextInputEditText nameET, address1ET, address2ET, landmarkET, cityET, pincodeET;
	private TextInputLayout nameTIL, address1TIL, address2TIL, landmarkTIL, cityTIL, pincodeTIL;
	private AppCompatSpinner stateACS;
	private ArrayList<StateModel> stateList = new ArrayList<>();
	
	public static AddressDialog newInstance(AddressModel address, int position) {
		Bundle args = new Bundle();
		args.putSerializable(ARG_ADDRESS, address);
		args.putInt(ARG_POSITION, position);
		AddressDialog fragment = new AddressDialog();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, R.style.AppThemeDialog);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_address, container, false);
		
		nameET = (TextInputEditText) view.findViewById(R.id.et_name);
		address1ET = (TextInputEditText) view.findViewById(R.id.et_address1);
		address2ET = (TextInputEditText) view.findViewById(R.id.et_address2);
		landmarkET = (TextInputEditText) view.findViewById(R.id.et_landmark);
		cityET = (TextInputEditText) view.findViewById(R.id.et_city);
		pincodeET = (TextInputEditText) view.findViewById(R.id.et_pincode);
		
		nameTIL = (TextInputLayout) view.findViewById(R.id.til_name);
		address1TIL = (TextInputLayout) view.findViewById(R.id.til_address1);
		address2TIL = (TextInputLayout) view.findViewById(R.id.til_address2);
		landmarkTIL = (TextInputLayout) view.findViewById(R.id.til_landmark);
		cityTIL = (TextInputLayout) view.findViewById(R.id.til_city);
		pincodeTIL = (TextInputLayout) view.findViewById(R.id.til_pincode);
		stateACS = (AppCompatSpinner) view.findViewById(R.id.acs_state);
		
		view.findViewById(R.id.tv_add).setOnClickListener(this);
		
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getDialog().getWindow().setGravity(Gravity.BOTTOM);
		
		ApiTask.builder(getContext())
			.setGET()
			.setUrl(ApiUrl.STATES)
			.setResponseListener(this)
			.setProgressMessage(R.string.loading_states)
			.exec();
		
		init();
		return view;
	}
	
	private void init() {
		AddressModel address = (AddressModel) getArguments().getSerializable(ARG_ADDRESS);
		nameET.setText(address.getName());
		address1ET.setText(address.getAddress1());
		address2ET.setText(address.getAddress2());
		landmarkET.setText(address.getLandmark());
		cityET.setText(address.getCity());
		pincodeET.setText(address.getPincode());
	}
	
	@Override
	public void onClick(View v) {
		AddressModel address = getAddress();
		if (address != null) {
			dismiss();
			((OnAddressListener) getActivity()).onAddAddress(address, getArguments().getInt(ARG_POSITION, -1));
		}
	}
	
	private AddressModel getAddress() {
		boolean isValid = true;
		String name = nameET.getText().toString().trim();
		String address1 = address1ET.getText().toString().trim();
		String address2 = address2ET.getText().toString().trim();
		String landmark = landmarkET.getText().toString().trim();
		String city = cityET.getText().toString().trim();
		String pincode = pincodeET.getText().toString().trim();
		
		if (name.length() < 3) {
			nameTIL.setError(getString(R.string.invalid_name));
			isValid = false;
		} else {
			nameTIL.setError(null);
		}
		
		if (address1.length() < 5) {
			address1TIL.setError(getString(R.string.invalid_address));
			isValid = false;
		} else {
			address1TIL.setError(null);
		}
		
		if (address2.length() < 5) {
			address2TIL.setError(getString(R.string.invalid_address));
			isValid = false;
		} else {
			address2TIL.setError(null);
		}
		
		if (address2.length() < 5) {
			address2TIL.setError(getString(R.string.invalid_address));
			isValid = false;
		} else {
			address2TIL.setError(null);
		}
		
		if (landmark.length() < 5) {
			landmarkTIL.setError(getString(R.string.invalid_landmark));
			isValid = false;
		} else {
			landmarkTIL.setError(null);
		}
		
		if (city.length() < 3) {
			cityTIL.setError(getString(R.string.invalid_city));
			isValid = false;
		} else {
			cityTIL.setError(null);
		}
		
		if (!pincode.matches("\\d{6}")) {
			pincodeTIL.setError(getString(R.string.invalid_pincode));
			isValid = false;
		} else {
			pincodeTIL.setError(null);
		}
		
		if (isValid) {
			AddressModel address = (AddressModel) getArguments().getSerializable(ARG_ADDRESS);
			address.setName(name);
			address.setAddress1(address1);
			address.setAddress2(address2);
			address.setLandmark(landmark);
			address.setCity(city);
			address.setState(stateList.get(stateACS.getSelectedItemPosition()));
			address.setPincode(pincode);
			
			return address;
		}
		
		return null;
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException {
		if (isAdded()) {
			JSONArray data = response.getJSONObject("data").getJSONArray("states");
			ArrayList<String> stateList = new ArrayList<>();
			
			for (int i = 0; i < data.length(); i++) {
				StateModel state = new StateModel(data.getJSONObject(i));
				this.stateList.add(state);
				stateList.add(state.getState());
			}
			
			stateACS.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, stateList));
			stateACS.setSelection(12);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	public interface OnAddressListener {
		void onAddAddress(AddressModel address, int position);
	}
}
