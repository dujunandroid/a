package com.boqii.petlifehouse.entities;

import org.json.JSONObject;

import com.boqii.petlifehouse.utilities.Util;

public class User extends BaseObject {

	/**
	 * 用户
	 */
	private static final long serialVersionUID = 1L;

	public String NickName;// 昵称
	public String UserID = "";// id
	public float Balance;// 余额
	public String Password;// 密码
	public String Telephone;// 手机号
	public String UserName;// 账号
	public String Sex;// 性别
	public int AllOrderNum;// 全部订单数
	public int UnpayOrderNum;// 待付款订单
	public int PayedOrderNum;// 已付款订单

	public static User JsonToSelf(JSONObject obj, String userName,
			String passWord) {
		User u = new User();
		u.NickName = obj.optString("NickName");
		u.UserID = obj.optString("UserId");
		u.Balance = (float) obj.optDouble("Balance");
		u.Password = passWord;
		u.UserName = userName;
		u.Telephone = obj.optString("Telephone");
		u.Sex = obj.optString("Sex");
		u.AllOrderNum = obj.optInt("AllOrderNum");
		u.UnpayOrderNum = obj.optInt("UnpayOrderNum");
		u.PayedOrderNum = obj.optInt("PayedOrderNum");
		return u;
	}

	public static User StringToSelf(String userStr) {
		User u = new User();
		if(Util.isEmpty(userStr))
			return new User();
		String user[] = userStr.split(",");
		u.NickName = user[0];
		u.UserID = user[1];
		u.Balance = Float.parseFloat(user[2]);
		u.Password = user[3];
		u.UserName = user[4];
		u.Telephone = user[5];
		u.Sex = user[6];
		u.AllOrderNum = Integer.parseInt(user[7]);
		u.UnpayOrderNum = Integer.parseInt(user[8]);
		u.PayedOrderNum = Integer.parseInt(user[9]);
		//System.out.println("StringTOUser="+u);
		return u;
	}

	@Override
	public String toString() {
		return NickName + "," + UserID + "," + Balance + "," + Password + ","
				+ UserName + "," + Telephone + "," + Sex + "," + AllOrderNum
				+ "," + UnpayOrderNum + "," + PayedOrderNum;
	}

}
