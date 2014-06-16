package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

/**
 * 我的服务券，区别于服务券
 */
public class MyTicket extends BaseObject {

	private static final long serialVersionUID = 1L;

	public int MyTicketId;// 我的服务券id
	public String MyTicketTitle;// 标题
	public float MyTicketPrice;// 价格
	public String MyTicketImg;// 图片地址
	public String MyTicketStatus;// 状态
	public String MyVoucherNo;// 凭证号

	/**
	 * 一下为服务券详情新增属性
	 */
	public Ticket ticket = new Ticket();//对应ticket信息
	public int MyTicketRemain;// 剩余可用次数
	public Merchant TicketMerchant = new Merchant();// 适用商户信息
	public String MyTicketDetail;// 服务券详情
	public String MyTicketRemind;// 特别提醒
	
	public static MyTicket JsonToSelf(JSONObject obj){
		MyTicket m = new MyTicket();
		m.MyTicketId = obj.optInt("MyTicketId");
		m.MyTicketTitle = obj.optString("MyTicketTitle");
		m.MyTicketPrice = (float) obj.optDouble("MyTicketPrice");
		m.MyTicketImg = obj.optString("MyTicketImg");
		m.ticket = Ticket.JsonToSelf(obj.optJSONObject("TicketInfo"));
		m.MyVoucherNo = obj.optString("MyTicketNo");
		m.MyTicketStatus = obj.optString("MyTicketStatus");
		m.MyTicketRemain = obj.optInt("MyTicketRemain");
		m.TicketMerchant = Merchant.JsonToSelf(obj.optJSONObject("TicketMerchant"));
		m.MyTicketDetail = obj.optString("MyTicketDetail");
		m.MyTicketRemind = obj.optString("MyTicketRemind");
		return m;
	}

}
