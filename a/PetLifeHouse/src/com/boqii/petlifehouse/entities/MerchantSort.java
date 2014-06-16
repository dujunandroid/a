package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

public class MerchantSort extends BaseObject {
	private static final long serialVersionUID = 1L;
	public String TypeName;// 分类名字
	public int TypeId;// 分类id
	
	public static MerchantSort JsonToSelf(JSONObject obj) {
		MerchantSort m=new MerchantSort();
		m.TypeId=obj.optInt("TypeId");
		m.TypeName=obj.optString("TypeName");
		return m;
	}
}
