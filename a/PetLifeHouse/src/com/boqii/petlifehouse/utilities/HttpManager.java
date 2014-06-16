package com.boqii.petlifehouse.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class HttpManager {

	
	private Context context;
	
	public HttpManager(Context context){
		this.context = context;
	}
	
	public void Excute(AsyncTask<Void, Void, String> task){
		if(!Util.isNetworkAvailable(context) && !Util.isWiFiActive(context)){
			Toast.makeText(context, "网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
			return;
		}
		task.execute();
	}
	
	public void Excute(AsyncTask<String, Void, String> task, String... params){
		if(!Util.isNetworkAvailable(context) && !Util.isWiFiActive(context)){
			Toast.makeText(context, "网络异常，请稍后再试", Toast.LENGTH_SHORT).show();
			return;
		}
		task.execute(params);
	}
	
}
