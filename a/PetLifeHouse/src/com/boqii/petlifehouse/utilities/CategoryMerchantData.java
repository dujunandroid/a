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

public class CategoryMerchantData {

	private static Context context;
	private JSONArray categoryMerchantData;
	private static CategoryMerchantData instance = null;
	
	public JSONArray getMerchantCategory(){
		if(categoryMerchantData == null){
			String cg = getSharedPreferences().getString("CategoryMerchantData", "");
			if(!cg.equals("")){
				try {
					categoryMerchantData = new JSONArray(cg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				categoryMerchantData = getMerchantCategoryByCache();
			}
		}
		return categoryMerchantData;
	}

	public static CategoryMerchantData getinstance(Context mcontext){
		context = mcontext;
		if(instance == null){
			instance = new CategoryMerchantData();
		}
		return instance;
	}

	private JSONArray getMerchantCategoryByCache() {
		try {
			InputStream in = context.getAssets().open("CategoryMerchantData");
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
	
	public void execute(){
		getCategoryMerchantDataByNet();
	}
	
	private void getCategoryMerchantDataByNet(){
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(context).GetMerchantSortType();
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					JSONObject o;
					try {
						o = new JSONObject(result);
						categoryMerchantData = o.optJSONArray("ResponseData");
						if(categoryMerchantData != null){
							saveCategoryMerchantData();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.execute();

	}
	
	private void saveCategoryMerchantData(){
		getSharedPreferences().edit().putString("CategoryMerchantData", categoryMerchantData.toString()).commit();
	}

	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("CategoryMerchantData",Context.MODE_APPEND);
	}
}
