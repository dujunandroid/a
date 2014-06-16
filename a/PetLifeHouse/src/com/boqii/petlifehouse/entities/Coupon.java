package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

/**
 * 优惠券
 */
public class Coupon extends BaseObject {

	private static final long serialVersionUID = 1L;

	public int CouponId;//id
	public String CouponNo;//优惠券号
	public float CouponPrice;//优惠券价格
	
	//详情所需字段
	public int CouponStatus;//优惠券状态 1、未使用 2、已使用 3、已过期
	public String CouponTitle;//优惠券title
	public String CouponCondition;//使用条件
	public String CouponStartTime;//生效时间
	public String CouponEndTime;//失效时间
	public String CouponDesc;//说明
	public String CouponRange;//使用范围
	public String CouponUsedOrder;//使用订单号
	public String CouponUsedTime;//使用时间

	
	public static Coupon JsonToSelf(JSONObject obj) {
		Coupon c = new Coupon();
		if (obj != null) {
			c.CouponId = obj.optInt("CouponId");
			c.CouponNo = obj.optString("CouponNo");
			c.CouponPrice = (float) obj.optDouble("CouponPrice");
			c.CouponStatus = obj.optInt("CouponStatus");
			c.CouponTitle = obj.optString("CouponTitle");
			c.CouponCondition = obj.optString("CouponCondition");
			c.CouponStartTime = obj.optString("CouponStartTime");
			c.CouponEndTime = obj.optString("CouponEndTime");
			c.CouponDesc = obj.optString("CouponDesc");
			c.CouponRange = obj.optString("CouponRange");
			c.CouponUsedOrder = obj.optString("CouponUsedOrder");
			c.CouponUsedTime = obj.optString("CouponUsedTime");
		}
		return c;
	}
}
