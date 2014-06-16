package com.boqii.petlifehouse.entities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Merchant extends BaseObject {

	/**
	 * 商户信息
	 */
	private static final long serialVersionUID = 1L;

	public int MerchantId;// 商户id	
	public String MerchantAddress;// 商户地址
	public String MerchantTele;// 商户电话
	public double MerchantLat;// 商户纬度
	public double MerchantLng;// 商户经度
	
	/********** 商户的详细信息属性 ***********/
	public ArrayList<Ticket> TicketList = new ArrayList<Ticket>();;// 服务券列表
	public String MerchantDesc;// 商户介绍
	public String MerchantTraffic;// 交通信息
	public String MerchantNear;// 周边小区
	public String MerchantServeUrl;// 服务页面url
	public String BusinessArea;//商圈
	public String MerchantImg;// 商户图片
	
	/*同一属性接口返回的字段名不一养的字段*/
	public String MerchantName;// 商户名字
	public int ScanNumber;// 浏览人数	
	public float ConsumePerPerson;// 人均消费
	public float MerchantDistance;// 商户距离
	public String Characteristic;//是否有券，是否检疫
	public Merchant(int merchantId, String merchantName,
			String merchantAddress, float merchantDistance,
			String merchantTele, double merchantLat, double merchantLng) {
		super();
		MerchantId = merchantId;
		MerchantName = merchantName;
		MerchantAddress = merchantAddress;
		MerchantDistance = merchantDistance;
		MerchantTele = merchantTele;
		MerchantLat = merchantLat;
		MerchantLng = merchantLng;
	}

	public Merchant() {
	}

	public static Merchant JsonToSelf(JSONObject obj) {
		Merchant m = new Merchant();
		m.BusinessArea="";
		if (obj != null) {
			m.MerchantId = obj.optInt("MerchantId");
			m.MerchantLat = obj.optDouble("MerchantLat");
			m.MerchantLng = obj.optDouble("MerchantLng");
			m.MerchantAddress = obj.optString("MerchantAddress");
			m.MerchantTele = obj.optString("MerchantTele");
			m.MerchantImg = obj.optString("MerchantImg");			
			m.MerchantDesc = obj.optString("MerchantDesc");
			m.MerchantTraffic = obj.optString("MerchantTraffic");
			m.MerchantNear = obj.optString("MerchantNear");
			m.MerchantServeUrl = obj.optString("MerchantServeUrl");
			m.BusinessArea=obj.optString("BusinessArea").equals("null")?"":obj.optString("BusinessArea");
			m.Characteristic=obj.optString("Characteristic");
			JSONArray array = obj.optJSONArray("TicketList");
			if(array != null && array.length() > 0){
				for (int i = 0; i < array.length(); i++) {
					m.TicketList.add(Ticket.JsonToSelf(array.optJSONObject(i)));
				}
			}

			m.MerchantName = obj.optString("MerchantName");
			if(m.MerchantName.equals("")){
				m.MerchantName = obj.optString("MerchantTitle");
			}
			m.ScanNumber = obj.optInt("ScanNumber");
			if(m.ScanNumber==0){
				m.ScanNumber = obj.optInt("ScanNumbers");
			}			
			m.MerchantDistance = obj.optInt("MerchantDistance", 0);
			if(m.MerchantDistance==0){
				m.MerchantDistance = obj.optInt("Distance", 0);
			}		
			m.ConsumePerPerson = obj.optInt("ConsumePerPerson");
			if(m.ConsumePerPerson==0){
				m.ConsumePerPerson = obj.optInt("AverageComsume");
			}
		}
		return m;
	}

	@Override
	public String toString() {
		return "Merchant [MerchantId=" + MerchantId + ", MerchantAddress="
				+ MerchantAddress + ", MerchantTele=" + MerchantTele
				+ ", MerchantLat=" + MerchantLat + ", MerchantLng="
				+ MerchantLng + ", TicketList=" + TicketList
				+ ", MerchantDesc=" + MerchantDesc + ", MerchantTraffic="
				+ MerchantTraffic + ", MerchantNear=" + MerchantNear
				+ ", MerchantServeUrl=" + MerchantServeUrl + ", BusinessArea="
				+ BusinessArea + ", MerchantImg=" + MerchantImg
				+ ", MerchantName=" + MerchantName + ", ScanNumber="
				+ ScanNumber + ", ConsumePerPerson=" + ConsumePerPerson
				+ ", MerchantDistance=" + MerchantDistance + "]";
	}

	
}
