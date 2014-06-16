package com.boqii.petlifehouse.utilities;

import android.util.Log;

public class Logger {
	//TODO change DEBUG to false when release
	private static Logger instance = null;
	
	public synchronized static Logger getInstance(){
		if(instance == null){
			synchronized (Logger.class) {
				if(instance == null)
					instance = new Logger();
			}
		}
		return instance;
	}
	
	public void v(String tag, String message){
		if(Config.DEBUG){
			Log.v(tag, message);
		}
	}

}
