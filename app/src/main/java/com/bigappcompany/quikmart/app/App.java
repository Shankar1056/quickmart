package com.bigappcompany.quikmart.app;

import android.app.Application;

import com.bigappcompany.quikmart.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 03 Jul 2017 at 5:06 PM
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
			.setDefaultFontPath(getString(R.string.font_light))
			.setFontAttrId(R.attr.fontPath)
			.build()
		);
	}
}
