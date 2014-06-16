package com.boqii.petlifehouse.baseactivities;

import cn.jpush.android.api.JPushInterface;

import com.boqii.petlifehouse.entities.User;
import com.boqii.petlifehouse.exception.CrashLog;
import com.boqii.petlifehouse.utilities.Config;

import android.app.Application;

public class BaseApplication extends Application {

	public User user = new User();
	@Override
	public void onCreate() {

		super.onCreate();
		JPushInterface.setDebugMode(Config.DEBUG);
		JPushInterface.init(this);
		if(Config.DEBUG){
			Thread.setDefaultUncaughtExceptionHandler(new CrashLog(
					this.getApplicationContext()));
		}
	}
	
}
