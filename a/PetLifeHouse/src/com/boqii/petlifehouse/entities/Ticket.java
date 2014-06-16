package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

/**
 * 服务券
 */
public class Ticket extends BaseObject {

	private static final long serialVersionUID = 1L;

	public int TicketId;// 券的唯一标示
	public String TicketTitle;// 券的标题信息
	public String TicketImg;// 券的图片地址
	public float TicketPrice;// 当前价格
	public float TicketOriPrice;// 原价
	public float Distance;// 距离
	public String BusinessArea;// 商圈

	/**
	 * 以下为详情页所需属性
	 */
	public String ImageList;// 图片地址，多个用“，”隔开
	public String TicketSale;//折扣信息
	public String TicketLimit;// 限购信息
	public int TicketLimitNumber;//限购数量
	public String TicketDesc;// 服务券说明
	public int TicketBuyed;// 已购人数
	public int TicketRemain;// 剩余时间，以妙为单位
	public Merchant TicketMerchant = new Merchant();// 商户信息
	public String TicketDetail;// 服务券详情
	public String TicketRemind;// 特别提醒
	public String TicketShowUrl;// 项目展示web页url
	public int IsCollected;
	public String MyTicketDetail;// 服务券详情
	public String MyTicketRemind;// 特别提醒
	public static Ticket JsonToSelf(JSONObject obj) {
		Ticket t = new Ticket();
		if (obj != null) {
			t.TicketId = obj.optInt("TicketID");
			if(t.TicketId == 0)
				t.TicketId = obj.optInt("TicketId");
			t.Distance = obj.optInt("Distance", 0);
			t.ImageList = obj.optString("ImageList");
			t.BusinessArea = obj.optString("BusinessArea");
			t.TicketBuyed = obj.optInt("TicketBuyed");
			t.TicketDesc = obj.optString("TicketDesc");
			t.TicketDetail = obj.optString("TicketDetail");
			t.TicketImg = obj.optString("TicketImg");
			t.TicketLimit = obj.optString("TicketLimit");
			t.TicketLimitNumber = obj.optInt("TicketLimitNumber");
			t.TicketMerchant = Merchant.JsonToSelf(obj.optJSONObject("TicketMerchant"));
			t.TicketOriPrice = (float) obj.optDouble("TicketOriPrice");
			t.TicketPrice = (float) obj.optDouble("TicketPrice");
			t.TicketRemain = obj.optInt("TicketRemain");
			t.TicketRemind = obj.optString("TicketRemind");
			t.TicketShowUrl = obj.optString("TicketShowUrl");
			t.TicketTitle = obj.optString("TicketTitle");
			t.TicketSale = obj.optString("TicketSale");
			t.IsCollected = obj.optInt("IsCollected");
			t.MyTicketDetail = obj.optString("MyTicketDetail");
			t.MyTicketRemind = obj.optString("MyTicketRemind");
		}
		return t;
	}

}
