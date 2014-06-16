package com.boqii.petlifehouse.entities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Area extends BaseObject {

	/**
	 * 区域
	 */
	private static final long serialVersionUID = 1L;
	public String AreaName;//区域名字
	public int AreaId;//区域id
	public String Remark;//备注
	public int Type;//区域类型
	public ArrayList<Area> AreaList;//子区域列表
	
	
	public static Area JsonToSelf(JSONObject obj) throws JSONException {
		Area a=new Area();
		a.AreaName=obj.optString("AreaName");
		a.AreaList=new ArrayList<Area>();
		JSONArray array=obj.optJSONArray("AreaList");
		for (int i = 0; i < array.length(); i++) {
			JSONObject subObj= (JSONObject) array.get(i);			
			Area sA=new Area();
			sA.AreaId=subObj.getInt("SubAreaId");
			sA.AreaName=subObj.getString("SubAreaName");
			a.AreaList.add(sA);
		}
		return a;
	}

	@Override
	public String toString() {
		return "Area [AreaName=" + AreaName + ", AreaId=" + AreaId
				+ ", Remark=" + Remark + ", AreaList=" + AreaList + "]";
	}
		
}
