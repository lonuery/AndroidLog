package com.lonuery.play;

import android.app.Application;

public class MyApplication extends Application{

	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new AndroidExceptionHandler(this));
		Logcat.getInstance(this).start();
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {
		Logcat.getInstance(this).stop();
		super.onTerminate();
	}
}
