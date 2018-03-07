package com.bigappcompany.quikmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.listener.OnAuthListener;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Jul 2017 at 2:30 PM
 */

public class SignUpFragment extends Fragment implements View.OnClickListener {
	private AppCompatEditText nameET, emailET;
	private TextInputLayout nameTIL, emailTIL;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
		
		nameTIL = (TextInputLayout) view.findViewById(R.id.til_name);
		emailTIL = (TextInputLayout) view.findViewById(R.id.til_email);
		nameET = (AppCompatEditText) view.findViewById(R.id.et_name);
		emailET = (AppCompatEditText) view.findViewById(R.id.et_email);
		view.findViewById(R.id.btn_submit).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		String name = nameET.getText().toString().trim();
		String email = emailET.getText().toString().trim();
		boolean isValid = true;
		
		if (name.length() < 3) {
			nameTIL.setError(getString(R.string.error_name));
			isValid = false;
		} else {
			nameTIL.setError(null);
		}
		
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			emailTIL.setError(getString(R.string.error_email));
			isValid = false;
		} else {
			emailTIL.setError(null);
		}
		
		if (isValid) {
			String[] data = new String[]{name, email};
			((OnAuthListener) getActivity()).onAuth(data, getTag());
		}
	}
}
