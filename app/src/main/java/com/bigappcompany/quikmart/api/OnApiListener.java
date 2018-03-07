package com.bigappcompany.quikmart.api;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 2:19 PM
 */

public interface OnApiListener {
	void onSuccess(JSONObject response, int requestCode, Bundle savedData) throws JSONException;
	
	void onFailure(int requestCode, Bundle savedData);
}
