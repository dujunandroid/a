package com.boqii.petlifehouse.utilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.baseservice.NetworkService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class CategoryAreaData {

	private static Context context;
	private JSONArray categoryData;
	private static CategoryAreaData instance = null;
	
	public JSONArray getAreaCategory(){
		if(categoryData == null){
			String cg = getSharedPreferences().getString("CategoryAreaData", "");
			if(!cg.equals("")){
				try {
					categoryData = new JSONArray(cg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				categoryData = getAreaCategoryByCache();
			}
		}
		return categoryData;
	}

	public static CategoryAreaData getinstance(Context mcontext){
		context = mcontext;
		if(instance == null){
			instance = new CategoryAreaData();
		}
		return instance;
	}
	
	public void execute(){
		getCategoryAreaDataByNet();
	}
	
	private void getCategoryAreaDataByNet(){
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(context).GetAreaType();
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					JSONObject o;
					try {
						o = new JSONObject(result);
						categoryData = o.optJSONArray("ResponseData");
						if(categoryData != null){
							saveCategoryAreaData();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.execute();

	}

	private JSONArray getAreaCategoryByCache() {
		try {
			InputStream in = context.getAssets().open("CategoryAreaData");
			if(in != null){
				byte[] b = new byte[1024];
				ByteArrayOutputStream outputs = new ByteArrayOutputStream();
				int len = -1;
				while((len = in.read(b)) != -1){
					outputs.write(b, 0, len);
				}
				return new JSONObject(outputs.toString()).getJSONArray("ResponseData");
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	private void saveCategoryAreaData(){
		getSharedPreferences().edit().putString("CategoryAreaData", categoryData.toString()).commit();
	}

	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("CategoryAreaData",Context.MODE_APPEND);
	}
}
