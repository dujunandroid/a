package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

/**
 * 订单
 */
public class Order extends BaseObject {

	private static final long serialVersionUID = 1L;

	public int OrderId;//订单id
	public Ticket mTicket = new Ticket();//服务券信息 Ticket.TicketId
	public int Number;//数量
	public String Mobile;//预留手机
	public Coupon mCoupon = new Coupon();//优惠券信息 Coupon.CouponPrice
	public float UsedBalance;//使用的余额
	public int PaymentType;//支付方式 1、支付宝客户端 2、支付宝网页 3、银联
	
	/**
	 * 以下为订单列表所需属性
	 */
	public String OrderTitle;//订单标题
	public float OrderPrice;//订单价格（总价）
	public String OrderImg;//订单图片
	public String OrderStatusText;//订单状态文字
	public int OrderStatus;//订单状态
	public String OrderTicketNo;//凭证号
	public String OrderTel;//手机号
	public String OrderCouponNo;//优惠券号
	public int TicketId;//服务表的id
	public int OrderTicketId;//我的服务券id
	public int TicketNumber;//订单里面服务券数量
	public static Order JsonToSelf(JSONObject obj){
		Order o = new Order();
		if (obj != null) {
			o.OrderId = obj.optInt("OrderId");
			o.Number = obj.optInt("Number");
			o.Mobile = obj.optString("Mobile");
			o.UsedBalance = (float) obj.optDouble("UsedBalance");
			o.PaymentType = obj.optInt("PaymentType");
			o.OrderTitle = obj.optString("OrderTitle");
			o.OrderPrice = (float) obj.optDouble("OrderPrice");
			o.OrderImg = obj.optString("OrderImg");
			o.OrderStatusText = obj.optString("OrderStatusText");
			o.OrderStatus = obj.optInt("OrderStatus");
			o.OrderTicketNo = obj.optString("OrderTicketNo");
			o.OrderTel = obj.optString("OrderTel");
			o.OrderCouponNo = obj.optString("OrderCouponNo");
			o.TicketId = obj.optInt("TicketId");
			o.OrderTicketId = obj.optInt("OrderTicketId");
			o.TicketNumber=obj.optInt("TicketNumber");
		}
		return o;
	}
}
