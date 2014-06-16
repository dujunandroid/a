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

public class CategoryData {

	private static Context context;
	private JSONArray categoryData;
	private static CategoryData instance = null;
	
	public JSONArray getTicketCategory(){
		if(categoryData == null){
			String cg = getSharedPreferences().getString("CategoryData", "");
			if(!cg.equals("")){
				try {
					categoryData = new JSONArray(cg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				categoryData = getTicketCategoryByCache();
			}
		}
		return categoryData;
	}

	public static CategoryData getinstance(Context mcontext){
		context = mcontext;
		if(instance == null){
			instance = new CategoryData();
		}
		return instance;
	}
	
	public void execute(){
		getCategoryDataByNet();
	}
	
	private void getCategoryDataByNet(){
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(context).GetSortType();
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					JSONObject o;
					try {
						o = new JSONObject(result);
						categoryData = o.optJSONArray("ResponseData");
						if(categoryData != null){
							saveCategoryData();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.execute();

	}

	private JSONArray getTicketCategoryByCache() {
		try {
			InputStream in = context.getAssets().open("CategoryData");
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
	
	private void saveCategoryData(){
		getSharedPreferences().edit().putString("CategoryData", categoryData.toString()).commit();
	}

	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("CategoryData",Context.MODE_APPEND);
	}
}
