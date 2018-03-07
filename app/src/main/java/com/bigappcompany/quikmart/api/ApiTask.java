package com.bigappcompany.quikmart.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.bigappcompany.quikmart.BuildConfig;
import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.activity.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.bigappcompany.quikmart.activity.BaseActivity.EXTRA_DATA;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-01-11 at 4:15 PM
 * <p>
 * Wrapper class over the OkHttp library.
 * This class can be used for api calls over HTTP or HTTPS protocols
 * </p>
 */

@SuppressWarnings("ConstantConditions")
public class ApiTask extends AsyncTask<String, Integer, String> {
	private static final String TAG = "ApiTask";
	private static final String TAG_REQUEST = "Request";
	private static final String TAG_RESPONSE = "Response";
	
	private static final int STATUS_INVALID_SESSION = -1;
	private static final int STATUS_API_OBSOLETE = 666;
	
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private final int mRequestCode;
	private final boolean isGet, isDelete;
	private final OnApiListener mListener;
	private final Context mContext;
	private final Bundle savedData;
	
	private CharSequence mProgressMessage;
	private ProgressDialog mDialog;
	
	/**
	 * @param context         : mContext for the loader mDialog, null for no loader
	 * @param progressMessage : Progress message, null for no progress loader
	 * @param listener        : Callback listener
	 * @param requestCode     : Request code to be returned with callback
	 */
	ApiTask(@NonNull Context context, @NonNull CharSequence progressMessage, @NonNull OnApiListener
		listener, boolean isGet, boolean isDelete, int requestCode, Bundle savedData) {
		this(context, listener, isGet, isDelete, requestCode, savedData);
		mProgressMessage = progressMessage;
	}
	
	/**
	 * @param listener    : Callback listener
	 * @param requestCode : Request code to be returned with callback
	 */
	ApiTask(@NonNull Context context, @NonNull OnApiListener listener, boolean isGet, boolean isDelete,
	        int requestCode, Bundle
		        savedData) {
		mContext = context;
		mListener = listener;
		mRequestCode = requestCode;
		this.savedData = savedData;
		this.isGet = isGet;
		this.isDelete = isDelete;
	}
	
	public static ApiBuilder builder(Context context) {
		return new ApiBuilder(context);
	}
	
	/**
	 * Does POST request to the specified URL if there is a request body specified by params[1]
	 * If there is no body then it does GET request
	 *
	 * @param params [0] mandatory argument specifies URL to be requested.
	 * @return server response, probably a string in JSON format
	 */
	@Override
	protected String doInBackground(String... params) {
		try {
			return isDelete ? doDeleteRequest(params[0], params[1]) :
				isGet ? doGetRequest(params[0]) : doPostRequest(params[0], params[1]);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	private String doDeleteRequest(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
			.addHeader("x-api-key", "1Imk32wjX0Tdcu77tB7PHhgBoYh")
			.url(url)
			.delete(body)
			.build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();
		
		String responseStr = response.body().string();
		
		if (BuildConfig.DEBUG) {
			
			Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
			JsonParser jp = new JsonParser();
			
			Log.e(TAG_REQUEST, url + "\n" + gson.toJson(jp.parse(json)));
			Log.e(TAG_RESPONSE, response.code() + " " + responseStr);
			
			try {
				Log.e(TAG_RESPONSE, gson.toJson(jp.parse(responseStr)));
			} catch (RuntimeException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		
		return responseStr;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if (mProgressMessage != null) {
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage(mProgressMessage);
			mDialog.setCancelable(false);
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
					
					if (mListener != null) {
						mListener.onFailure(mRequestCode, savedData);
					}
				}
			});
			mDialog.show();
		}
	}
	
	@Override
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		
		// Dismiss the mDialog
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		
		if (mListener != null) {
			if (response != null) {
				try {
					parseResponse(response);
				} catch (JSONException e) {
					mListener.onFailure(mRequestCode, savedData);
					Log.e(TAG, e.getMessage(), e);
				}
			} else {
				if (Looper.myLooper() == Looper.getMainLooper()) {
					Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT).show();
				}
				
				mListener.onFailure(mRequestCode, savedData);
			}
		}
	}
	
	private void parseResponse(String response) throws JSONException {
		// on success, convert response into JSON object
		JSONObject object = new JSONObject(response);
		mListener.onSuccess(object, mRequestCode, savedData);
		
		/*
		switch (object.getInt("status")) {
			case STATUS_INVALID_SESSION:
				new Preference(mContext).setLoggedIn(false);
				Intent intent = new Intent(mContext, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				mContext.startActivity(intent);
				break;
			
			case STATUS_API_OBSOLETE:
				try {
					if (!((AppCompatActivity) mContext).isFinishing()) {
						updateApp();
					}
				} catch (ClassCastException e) {
					if (Looper.myLooper() == Looper.getMainLooper()) {
						Toast.makeText(
							mContext,
							R.string.obsolete_app_version,
							Toast.LENGTH_LONG
						).show();
					}
				}
				break;
			
			default:
				break;
		}*/
	}
	
	/**
	 * Post method request api
	 *
	 * @param url  url of the request
	 * @param json request json
	 * @return response from the server
	 * @throws IOException Network IO
	 */
	@NonNull
	private String doPostRequest(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
			.addHeader("x-api-key", "1Imk32wjX0Tdcu77tB7PHhgBoYh")
			.url(url)
			.post(body)
			.build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();
		
		String responseStr = response.body().string();
		
		if (BuildConfig.DEBUG) {
			
			Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
			JsonParser jp = new JsonParser();
			
			Log.e(TAG_REQUEST, url + "\n" + gson.toJson(jp.parse(json)));
			Log.e(TAG_RESPONSE, response.code() + " " + responseStr);
			
			try {
				Log.e(TAG_RESPONSE, gson.toJson(jp.parse(responseStr)));
			} catch (RuntimeException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		
		return responseStr;
	}
	
	/**
	 * Post method request api
	 *
	 * @param url url of the request
	 * @return response from the server
	 * @throws IOException Network IO
	 */
	@NonNull
	private String doGetRequest(String url) throws IOException {
		Request request = new Request.Builder()
			.addHeader("x-api-key", "1Imk32wjX0Tdcu77tB7PHhgBoYh")
			.url(url)
			.get()
			.build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();
		
		String responseStr = response.body().string();
		
		if (BuildConfig.DEBUG) {
			
			Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
			JsonParser jp = new JsonParser();
			
			Log.e(TAG_REQUEST, url);
			Log.e(TAG_RESPONSE, responseStr);
			
			try {
				Log.e(TAG_RESPONSE, gson.toJson(jp.parse(responseStr)));
			} catch (RuntimeException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		
		return responseStr;
	}
	
	private void updateApp() {
		new AlertDialog.Builder(mContext)
			.setTitle(R.string.title_update_app)
			.setMessage(mContext.getString(R.string.message_update_app, BuildConfig.VERSION_NAME))
			.setCancelable(false)
			.setNegativeButton(R.string.exit_app, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra(EXTRA_DATA, true);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			})
			.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent goToMarket = new Intent(Intent.ACTION_VIEW)
						.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
					mContext.startActivity(goToMarket);
				}
			})
			.create()
			.show();
	}
}