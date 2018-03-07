package com.bigappcompany.quikmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.listener.OnAuthListener;


/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Jul 2017 at 1:14 PM
 */

public class OTPFragment extends Fragment implements View.OnClickListener {
	private AppCompatEditText otpET;
	private TextInputLayout otpTIL;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_otp, container, false);
		
		otpTIL = (TextInputLayout) view.findViewById(R.id.til_otp);
		otpET = (AppCompatEditText) view.findViewById(R.id.et_otp);
		view.findViewById(R.id.btn_submit).setOnClickListener(this);
		
		return view;
	}
	
	public void setError() {
		otpTIL.setError(getString(R.string.error_otp));
		Toast.makeText(getContext(), R.string.error_otp, Toast.LENGTH_SHORT).show();
	}
	
	public void setOtp(String otp) {
		otpET.setText(otp);
		onClick(null);
	}
	
	@Override
	public void onClick(View v) {
		String otp = otpET.getText().toString().trim();
		
		if (otp.matches("\\d{5}")) {
			((OnAuthListener) getActivity()).onAuth(otp, getTag());
			otpTIL.setError(null);
		} else {
			otpTIL.setError(getString(R.string.error_otp));
		}
	}
}
