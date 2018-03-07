package com.bigappcompany.quikmart.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.bigappcompany.quikmart.api.ApiTask;
import com.bigappcompany.quikmart.api.ApiUrl;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RegistrationIntentService extends IntentService {
	private static final String TAG = "RegIntentService";
	
	public RegistrationIntentService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		try {
			String token = FirebaseInstanceId.getInstance().getToken();
			Log.d(TAG, "Refreshed token: " + token);
			// [END get_token]
			sendRegistrationToServer(token);
			
			// Subscribe to topic channels
			
			// You should store a boolean that indicates whether the generated token has been
			// sent to your server. If the boolean is false, send the token to your server,
			// otherwise your server should have already received the token.
			// [END register_for_gcm]
		} catch (Exception e) {
			Log.e(TAG, "Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
		}
	}
	
	/**
	 * Persist registration to third-party servers.
	 * <p/>
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(String token) {
		JsonObject object = new JsonObject();
		object.addProperty("push_token", token);
		
		ApiTask.builder(this)
			.setUrl(ApiUrl.GCM_TOKEN_UPDATE)
			.setRequestBody(object)
			.exec();
	}
}