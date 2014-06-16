package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

/**
 * 收藏
 */
public class Collection extends BaseObject {

	private static final long serialVersionUID = 1L;

	public Ticket ticket = new Ticket();// 服务券信息
	public String TicketStatus;// 服务券状态 剩余多久或已结束
	
	public static Collection JsonToSelf(JSONObject obj){
		Collection c = new Collection();
		c.ticket = Ticket.JsonToSelf(obj);
		c.TicketStatus = obj.optString("TicketStatus");
		return c;
	}
	
}
