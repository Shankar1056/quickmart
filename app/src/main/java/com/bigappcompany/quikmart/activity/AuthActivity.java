package com.bigappcompany.quikmart.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.bigappcompany.quikmart.api.OnApiListener;
import com.bigappcompany.quikmart.fragment.MobileFragment;
import com.bigappcompany.quikmart.fragment.OTPFragment;
import com.bigappcompany.quikmart.fragment.SignUpFragment;
import com.bigappcompany.quikmart.listener.OnAuthListener;
import com.bigappcompany.quikmart.model.AuthModel;
import com.bigappcompany.quikmart.model.UserModel;
import com.bigappcompany.quikmart.util.Preference;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AuthActivity extends AppCompatActivity implements OnAuthListener, OnApiListener, OnSmsCatchListener<String> {
	private static final String TAG = "AuthActivity";
	private static final String TAG_MOBILE = "TAG_MOBILE";
	private static final String TAG_OTP = "TAG_OTP";
	private static final String TAG_SIGN_UP = "TAG_SIGN_UP";
	
	private static final int RC_SEND_OTP = 1;
	private static final int RC_VERIFY_OTP = 2;
	private static final int RC_REGISTER = 4;
	
	private AuthModel mAuth;
	private SmsVerifyCatcher mOTPCatcher;
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		
		mOTPCatcher = new SmsVerifyCatcher(this, this);
		
		// init authentication model
		mAuth = new AuthModel();
		
		// init holder with mobile fragment (default case)
		onAuth(null, "");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mOTPCatcher.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mOTPCatcher.onStop();
	}
	
	@Override
	public void onAuth(Object data, String tag) {
		switch (tag) {
			default: // attach mobile fragment to holder
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.fl_holder, new MobileFragment(), TAG_MOBILE)
					.commit();
				break;
			
			case TAG_MOBILE: // attach OTP fragment to holder
				mAuth.setMobile(data.toString());
				
				ApiTask.builder(this)
					.setUrl(ApiUrl.GENERATE_OTP)
					.setRequestBody(mAuth.toJson())
					.setProgressMessage(R.string.authenticating)
					.setRequestCode(RC_SEND_OTP)
					.setResponseListener(this)
					.exec();
				break;
			
			case TAG_OTP:
				mAuth.setOtp(data.toString());
				ApiTask.builder(this)
					.setUrl(ApiUrl.SUBMIT_OTP)
					.setRequestBody(mAuth.toJson())
					.setProgressMessage(R.string.verifying)
					.setRequestCode(RC_VERIFY_OTP)
					.setResponseListener(this)
					.exec();
				break;
			
			case TAG_SIGN_UP:
				String[] content = (String[]) data;
				mAuth.setName(content[0]);
				mAuth.setEmail(content[1]);
				
				ApiTask.builder(this)
					.setUrl(ApiUrl.REGISTER)
					.setRequestBody(mAuth.toJson())
					.setProgressMessage(R.string.signing_up)
					.setRequestCode(RC_REGISTER)
					.setResponseListener(this)
					.exec();
				break;
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mOTPCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	@Override
	public void onSuccess(JSONObject response, int requestCode, Bundle savedData) {
		try {
			
			switch (requestCode) {
				case RC_SEND_OTP:
					getSupportFragmentManager().beginTransaction()
						.replace(R.id.fl_holder, new OTPFragment(), TAG_OTP)
						.addToBackStack(null)
						.commit();
					break;
				
				case RC_VERIFY_OTP:
					parseVerification(response);
					break;
				
				case RC_REGISTER:
					Toast.makeText(this, response.getString("msg"), Toast.LENGTH_SHORT).show();
					parseRegistration(response.getJSONObject("data"));
					setResult(RESULT_OK);
					finish();
					break;
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	private void parseRegistration(JSONObject data) throws JSONException {
		Preference pref = new Preference(this);
		pref.setName(mAuth.getName());
		pref.setEmail(mAuth.getEmail());
		pref.setSessionKey(data.getString("session_token"));
		pref.setMobile(mAuth.getMobile());
		pref.setLoggedIn(true);
	}
	
	private void parseVerification(JSONObject response) throws JSONException {
		switch (response.getInt("otp_status")) {
			case -2: // invalid OTP
				try {
					OTPFragment fragment = (OTPFragment) getSupportFragmentManager().findFragmentByTag(TAG_OTP);
					fragment.setError();
				} catch (NullPointerException | ClassCastException e) {
					Log.w(TAG, "parseVerification: ", e);
				}
				break;
			
			case 0: // existing user
				Toast.makeText(this, R.string.logged_in_successfully, Toast.LENGTH_SHORT).show();
				setProfileInfo(response.getJSONObject("data"));
				setResult(RESULT_OK);
				finish();
				break;
			
			case 1: // new user
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.fl_holder, new SignUpFragment(), TAG_SIGN_UP)
					.addToBackStack(null)
					.commit();
				break;
			
		}
	}
	
	private void setProfileInfo(JSONObject data) throws JSONException {
		Preference pref = new Preference(this);
		UserModel user = new UserModel(data);
		pref.setSessionKey(user.getSessionId());
		pref.setName(user.getName());
		pref.setMobile(user.getPhone());
		pref.setEmail(user.getEmail());
		pref.setLoggedIn(true);
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onSmsCatch(String message) {
		try {
			OTPFragment fragment = (OTPFragment) getSupportFragmentManager().findFragmentByTag(TAG_OTP);
			fragment.setOtp(message.split(" ")[0]);
		} catch (NullPointerException | ClassCastException e) {
			Log.w(TAG, "parseVerification: ", e);
		}
	}
}
