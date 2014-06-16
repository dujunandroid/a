package com.boqii.petlifehouse.baseservice;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.Util;

import android.content.Context;
import android.os.Build;



/**
 * RequestParameters
 * @author  DuJun
 */
public class RequestParameters {

	private HashMap<String, String> mParameters;
	private List<String> mKeys;
	private Context context;
	
	
	public RequestParameters(Context c){
		this.context = c;
		mParameters = new HashMap<String, String>();
		mKeys = new ArrayList<String>();
		try {
			add("AppVersion", Util.getVersionName(context));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			add("AppVersion", "1.0.0");
			e.printStackTrace();
		}
		add("Format", "json");
		add("Model", Build.BRAND + "_" + Build.MODEL);
		add("SystemName", "Android");
		add("SystemVersion", Build.VERSION.RELEASE);
		add("UDID", Util.id(context));
		add("Version", "1.0");
	}

	public void add(String key, int value){
		if(!this.mKeys.contains(key)){
			this.mKeys.add(key);
		}
		this.mParameters.put(key, value + "");
	}
	
	public void add(String key, float value){
		if(!this.mKeys.contains(key)){	
			this.mKeys.add(key);
		}
		this.mParameters.put(key, value + "");
	}
	
	public void add(String key, double value){
		if(!this.mKeys.contains(key)){	
			this.mKeys.add(key);
		}
		this.mParameters.put(key, value + "");
	}
	
	public void add(String key, String value){
		if(!this.mKeys.contains(key)){	
			this.mKeys.add(key);
		}
		this.mParameters.put(key, value);
	}
	
	
	public void remove(String key){
		mKeys.remove(key);
		this.mParameters.remove(key);
	}
	
	public void remove(int i){
		String key = this.mKeys.get(i);
		this.mParameters.remove(key);
		mKeys.remove(key);
	}
	
	
	public int getLocation(String key){
		if(this.mKeys.contains(key)){
			return this.mKeys.indexOf(key);
		}
		return -1;
	}
	
	public String getKey(int location){
		if(location >= 0 && location < this.mKeys.size()){
			return this.mKeys.get(location);
		}
		return "";
	}
	
	
	public String getValue(String key){
		String rlt = this.mParameters.get(key);
		return rlt;
	}
	
	public String getValue(int location){
		String key = this.mKeys.get(location);
		String rlt = this.mParameters.get(key);
		return rlt;
	}
	
	
	public int size(){
		return mKeys.size();
	}
	
	public void addAll(RequestParameters parameters){
		for(int i = 0; i < parameters.size(); i++){
			this.add(parameters.getKey(i), parameters.getValue(i));
		}
		
	}
	
	public String getRquestParam(){
		String[] s = new String[mKeys.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = mKeys.get(i);//获取所有key的集合
		}
		String[] keys = Util.Sort(s);//首字母排序
		
		String key,value;
		StringBuffer strs = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		for(int i = 0,len = keys.length;i < len;i++){
			key = keys[i];
			value = getValue(key);
			if(i != 0){
				sb.append("&");
			}
			strs.append(value);
			sb.append(key+"="+value);
		}
		try {
			sb.append("&Sign=").append(Util.getMD5(strs.append(Constants.CODE).toString()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public void clear(){
		this.mKeys.clear();
		this.mParameters.clear();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = "";
		for (int i = 0; i < mKeys.size(); i++) {
			result += mKeys.get(i) + "---";
		}
		return result;
	}
	
}
