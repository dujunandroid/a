package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

public class AdItem extends BaseObject {

	/**
	 * 轮播广告
	 */
	private static final long serialVersionUID = 1L;
	
	public int BannerType;//1、券列表 2、券详情 3、商户列表 4、商户详情
	public String ImageUrl;
	public String Url;
	
	public static AdItem JsonToSelf(JSONObject obj){
		AdItem ad = new AdItem();
		if(obj != null){
			ad.BannerType = obj.optInt("BannerType");
			ad.ImageUrl = obj.optString("ImageUrl");
			ad.Url = obj.optString("Url");
		}
		return ad;
		
	}

}
