package com.boqii.petlifehouse.entities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 分类类型
 */
public class SortType extends BaseObject {

	private static final long serialVersionUID = 1L;
	public String TypeName;// 分类名字
	public int TypeId;// 分类id
	public ArrayList<SortType> TypeList;// 子分类列表

	public static SortType JsonToSelf(JSONObject obj) {
		SortType st = new SortType();
		//st.TypeId = obj.optInt("TypeId");		
		JSONArray array = obj.optJSONArray("TypeList");
		st.TypeList=new ArrayList<SortType>();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				SortType stSub=new SortType();
				stSub.TypeId = obj.optInt("SubTypeId");
				stSub.TypeName = obj.optString("SubTypeName");
				st.TypeList.add(stSub);
			}
		}
		st.TypeName = obj.optString("TypeName");
		return st;
	}

	@Override
	public String toString() {
		return "SortType [TypeName=" + TypeName + ", TypeId=" + TypeId
				+ ", TypeList=" + TypeList + "]";
	}
	
}
