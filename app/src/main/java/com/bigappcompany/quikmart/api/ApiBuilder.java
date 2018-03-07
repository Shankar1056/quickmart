package com.bigappcompany.quikmart.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.bigappcompany.quikmart.BuildConfig;
import com.bigappcompany.quikmart.util.Preference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 12 Apr 2017 at 12:47 PM
 */

public class ApiBuilder {
	private Context context;
	private CharSequence progressMessage;
	private OnApiListener listener;
	private int requestCode;
	private String url;
	private Bundle savedData;
	private JsonObject requestBody;
	private boolean isGET = false;
	private boolean isDelete = false;
	
	ApiBuilder(Context context) {
		this.context = context;
	}
	
	public ApiBuilder setProgressMessage(CharSequence progressMessage) {
		this.progressMessage = progressMessage;
		return this;
	}
	
	public ApiBuilder setProgressMessage(@StringRes int resId) {
		this.progressMessage = context.getString(resId);
		return this;
	}
	
	public ApiBuilder setSavedData(Bundle data) {
		this.savedData = data;
		return this;
	}
	
	public ApiBuilder setResponseListener(OnApiListener listener) {
		this.listener = listener;
		return this;
	}
	
	public ApiBuilder setRequestCode(int requestCode) {
		this.requestCode = requestCode;
		return this;
	}
	
	public ApiBuilder setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public ApiBuilder setGET() {
		isGET = true;
		return this;
	}
	
	public ApiBuilder setDELETE() {
		isDelete = true;
		return this;
	}
	
	public ApiBuilder setRequestBody(JsonObject object) {
		this.requestBody = object;
		return this;
	}
	
	public void exec() {
		ApiTask task = progressMessage == null ? new ApiTask(context, listener, isGET, isDelete, requestCode, savedData)
			: new ApiTask(context, progressMessage, listener, isGET, isDelete, requestCode, savedData);
		
		if (requestBody == null) {
			requestBody = new JsonObject();
		}
		
		requestBody.addProperty("session_token", new Preference(context).getSessionKey());
		requestBody.addProperty("source", "ANDROID");
		requestBody.addProperty("app_version", BuildConfig.VERSION_NAME);
		requestBody.addProperty("app_version_code", BuildConfig.VERSION_CODE);
		
		stripNullProperties();
		
		task.execute(url, requestBody.toString());
	}
	
	private void stripNullProperties() {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> data = new Gson().fromJson(requestBody, type);
		
		for (Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() == null) {
				it.remove();
			} else if (entry.getValue().getClass().equals(ArrayList.class)) {
				if (((ArrayList<?>) entry.getValue()).size() == 0) {
					it.remove();
				}
			}
		}
		
		requestBody = (JsonObject) new JsonParser().parse(new Gson().toJson(data));
	}
}