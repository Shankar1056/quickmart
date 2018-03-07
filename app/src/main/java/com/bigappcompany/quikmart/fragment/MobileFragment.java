package com.bigappcompany.quikmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.listener.OnAuthListener;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Jul 2017 at 11:04 AM
 */

public class MobileFragment extends Fragment implements View.OnClickListener {
	private TextInputLayout mobileTIL;
	private AppCompatEditText mobileET;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile, container, false);
		
		mobileET = (AppCompatEditText) view.findViewById(R.id.et_mobile);
		mobileTIL = (TextInputLayout) view.findViewById(R.id.til_mobile);
		view.findViewById(R.id.btn_submit).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		String mobile = getInput();
		
		if (mobile != null) {
			((OnAuthListener) getActivity()).onAuth(mobile, getTag());
		}
	}
	
	/**
	 * If the mobile number is semantically correct, return the result to activity
	 *
	 * @return mobile number
	 */
	public String getInput() {
		String mobile = mobileET.getText().toString().trim();
		
		if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile) || mobile.length() != 10) {
			mobileTIL.setError(getString(R.string.error_mobile));
			return null;
		}
		
		mobileTIL.setError(null);
		
		return mobile;
	}
}
