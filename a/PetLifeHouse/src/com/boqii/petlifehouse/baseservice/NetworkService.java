package com.boqii.petlifehouse.baseservice;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.boqii.petlifehouse.baseservice.ClientHelper;
import com.boqii.petlifehouse.baseservice.ClientHelper.TimeOutListener;
import com.boqii.petlifehouse.baseservice.RequestParameters;
import com.boqii.petlifehouse.utilities.Config;

public class NetworkService {
	public static final String SERVER_URL = Config.DEBUG ? "http://vettest.boqii.com/mobileApi" : "http://vet.boqii.com/mobileApi";
	private static NetworkService instance;
	private static ClientHelper clientHelper;
	private static timeoutListener listener;
	private static Context context;

	public static NetworkService getInstance(Context c) {
		context = c;
		instance = new NetworkService();
		clientHelper = ClientHelper.getInstance();
		clientHelper.setTimeoutListener(new TimeOutListener() {

			@Override
			public void timeoutListener() {
				listener.TimeOutListener();
			}
		});
		return instance;
	}

	public String getUrl(){
		return SERVER_URL;
	}

	/**
	 * 用户登录
	 * @param UserName 用户名
	 * @param Password 密码
	 * @return
	 */
	public String UserLogin(String UserName, String Password){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "UserLogin");
		params.add("UserName", UserName);
		params.add("Password", Password);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取最新android版本号
	 * @return
	 */
	public String GetLastAndroidVersion(int Version){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetLastAndroidVersion");
		params.add("Version", Version);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取主页数据
	 * @return
	 */
	public String GetHomeData(){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetHomeData");
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	/*
		String result = "";
		String url = getUrl();
		URL u;
		try {
			u = new URL(url);
			result = HttpConnect(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;*/
	}

	/**
	 * 获取附近列表 (弃用)
	 * @param Lat
	 * @param Lng
	 * @param SortType
	 * @param OrderType
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	/*public String GetNearList(double Lat, double Lng, int SortType, int OrderType, int StartIndex, int Number){
		String result = "";
		String url = getUrl("GetNearList");
		RequestParameters params = new RequestParameters();
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		params.add("SortType", SortType);
		params.add("OrderType", OrderType);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.GET);
		return result;
	}*/

	/**
	 * 获取分类类型
	 * @return
	 */
	public String GetSortType(){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetSortType");
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
		/*String result = "";
		String url = getUrl();
		URL u;
		try {
			u = new URL(url);
			result = HttpConnect(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;*/
	}

	
	/**
	 * 商户类型
	 * @return
	 */
	public String GetMerchantSortType(){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMerchantSortType");
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	
	/**
	 * 获取区域分类
	 * @return
	 */
	public String GetAreaType(){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetAreaType");
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
		/*String result = "";
		String url = getUrl();
		URL u;
		try {
			u = new URL(url);
			result = HttpConnect(u);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;*/
	}

	/**
	 * 搜索服务券列表
	 * @param SortTypeId 分类类型
	 * @param OrderTypeId 排序类型
	 * @param Lat
	 * @param Lng
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String SearchTicketList(String KeyWord, int SortTypeId, int OrderTypeId, double Lat, double Lng, int StartIndex, int Number,float Scope){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "SearchTicketList");
		params.add("KeyWord", KeyWord);
		params.add("SortTypeId", SortTypeId);
		params.add("OrderTypeId", OrderTypeId);
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		if(Scope!=0){
			params.add("Scope", Scope);
		}		
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 搜索商户列表
	 * @param AreaId
	 * @param SortTypeId
	 * @param OrderTypeId
	 * @param Lat
	 * @param Lng
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String SearchMerchantList(String KeyWord, int AreaId, int Type,int SortTypeId, int OrderTypeId, double Lat, double Lng, int StartIndex, int Number,float Scope){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "SearchMerchantList");
		params.add("KeyWord", KeyWord);
		params.add("AreaId", AreaId);
		params.add("SortTypeId", SortTypeId);
		params.add("OrderTypeId", OrderTypeId);
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		params.add("Type", Type);
		if(Scope!=0){
			params.add("Scope", Scope);
		}		
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取我的订单列表
	 * @param OrderType 1、全部订单 2、待付款 3、已付款 4、退款中 5、已退款
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String GetMyOrderList(String UserId, int OrderType, int StartIndex, int Number){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyOrderList");
		params.add("UserId", UserId);
		params.add("OrderType", OrderType);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取我的服务券列表
	 * @param OrderType 1、未使用 2、已使用 3、已过期
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String GetMyTicketList(String UserId, int OrderType, int StartIndex, int Number){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyTicketList");
		params.add("UserId", UserId);
		params.add("OrderType", OrderType);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取我的服务券详情
	 * @param MyTicketId
	 * @param Lat
	 * @param Lng
	 * @return
	 */
	public String GetMyTicketDetail(String UserId, int MyTicketId, double Lat, double Lng){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyTicketDetail");
		params.add("UserId", UserId);
		params.add("MyTicketId", MyTicketId);
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取我的优惠券列表
	 * @param OrderType
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String GetMyCouponList(String UserId, int OrderType, int StartIndex, int Number){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyCouponList");
		params.add("UserId", UserId);
		params.add("OrderType", OrderType);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取优惠券详情
	 * @param CouponId
	 * @return
	 */
	public String GetCouponDetail(String UserId, int CouponId){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetCouponDetail");
		params.add("UserId", UserId);
		params.add("CouponId", CouponId);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取我的收藏列表
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String GetMyCollectionList(String UserId, int StartIndex, int Number){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyCollectionList");
		params.add("UserId", UserId);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 添加或删除收藏
	 * @param TicketId
	 * @param Method 1、添加 2、删除
	 * @return
	 */
	public String HandleCollection(String UserId, int TicketId, int Method){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "HandleCollection");
		params.add("UserId", UserId);
		params.add("TicketId", TicketId);
		params.add("Method", Method);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取服务券详情
	 * @param TicketId
	 * @param Lat
	 * @param Lng
	 * @return
	 */
	public String GetTicketDetail(String UserId, int TicketId, double Lat, double Lng){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetTicketDetail");
		params.add("UserId", UserId);
		params.add("TicketId", TicketId);
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 获取商户详情
	 * @param MerchantId
	 * @param Lat
	 * @param Lng
	 * @return
	 */
	public String GetMerchantDetail(int MerchantId, double Lat, double Lng){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMerchantDetail");
		params.add("MerchantId", MerchantId);
		params.add("Lat", Lat);
		params.add("Lng", Lng);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 提交服务券订单
	 * @param TicketId
	 * @param Number
	 * @param Mobile
	 * @return
	 */
	public String CommitOrder(String UserId, int TicketId, int Number, String Mobile){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "CommitOrder");
		params.add("UserId", UserId);
		params.add("TicketId", TicketId);
		params.add("Number", Number);
		params.add("Mobile", Mobile);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 验证优惠券
	 * @param OrderId
	 * @param CouponNo
	 * @return
	 */
	public String CheckCoupon(String UserId, int OrderId, String CouponNo){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "CheckCoupon");
		params.add("UserId", UserId);
		params.add("OrderId", OrderId);
		params.add("CouponNo", CouponNo);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 验证余额
	 * @param Balance
	 * @param Password
	 * @return
	 */
	public String CheckPayPassword(String UserId, String Password){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "CheckPayPassword");
		params.add("UserId", UserId);
		params.add("Password", Password);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 支付服务券订单
	 * @param OrderId
	 * @param CouponNo
	 * @param UsedBalance
	 * @param PaymentType 支付方式：1、支付宝客户端 2、支付宝网页 3、银联
	 * @return
	 */
	public String PayOrder(String UserId, int OrderId, String CouponNo, String payPassword, int UsedBalance, int PaymentType){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "PayOrder");
		params.add("UserId", UserId);
		params.add("OrderId", OrderId);
		params.add("DiscountCode", CouponNo);
		params.add("IsBalance", UsedBalance);
		params.add("PayPassword", payPassword);
		params.add("Type", PaymentType);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 用户注册
	 * @param UserName
	 * @param Password
	 * @return
	 */
	public String UserRegister(String UserName, String Password,String AuthCode){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "UserRegister");
		params.add("UserName", UserName);
		params.add("Password", Password);
		params.add("AuthCode", AuthCode);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 快登
	 * @param UserId 第三方平台返回的用户id
	 * @param ChannelType 平台枚举
	 * @return
	 */
	public String FastLogin(String UserId, String ChannelType){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "FastLogin");
		params.add("UID", UserId);
		params.add("ChannelType", ChannelType);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	/**
	 * 找回密码
	 * @param Account
	 * @param AccountType 账号类型：1、手机 2、邮箱
	 * @return
	 */
	public String FindPassword(String Account, int AccountType){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "FindPassword");
		params.add("Account", Account);
		params.add("AccountType", AccountType);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	/**
	 * 检查验证码
	 * @param Account
	 * @param AuthCode
	 * @return
	 */
	public String CheckAuthCode(String Account, String AuthCode){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "CheckAuthCode");
		params.add("Account", Account);
		params.add("AuthCode", AuthCode);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}

	
	/**
	 * 发送验证码
	 * @param phoneNumber手机号码
	 * @return
	 */
	public String SendAuthCode(String phoneNumber){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "SendAuthCode");
		params.add("Account", phoneNumber);
		result=clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	/**
	 * 修改密码
	 * @param Account
	 * @param Password
	 * @return
	 */
	public String ModifyPassword(String Account,String Authcode, String Password){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "ModifyPassword");
		params.add("Account", Account);
		params.add("Password", Password);
		params.add("Authcode", Authcode);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	/**
	 * 获取用户信息
	 * @param UserId
	 * @return
	 */
	public String GetUserInfo(String UserId){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetUserData");
		params.add("UserId", UserId);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	/**
	 * 得到我的服务券列表
	 * @param OrderId
	 * @param StartIndex
	 * @param Number
	 * @return
	 */
	public String GetMyTicketsInOrder(int OrderId,int StartIndex,int Number){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "GetMyTicketsInOrder");
		params.add("OrderId", OrderId);
		params.add("StartIndex", StartIndex);
		params.add("Number", Number);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	/**
	 * 上传渠道号
	 * @param channel
	 * @return
	 */
	public String UploadChannel(String channel){
		String result = "";
		String url = getUrl();
		RequestParameters params = new RequestParameters(context);
		params.add("Act", "UploadChannel");
		params.add("Channel", channel);
		result = clientHelper.execute(url, params, ClientHelper.POST);
		return result;
	}
	
	public String HttpConnect(URL url) {
		String result = "";
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			conn.setConnectTimeout(20000);
			InputStream inputStream = conn.getInputStream();
			if (conn.getResponseCode() == 408) {
				listener.TimeOutListener();
			}
			byte[] b = new byte[1024];
			int readedLength = -1;
			ByteArrayOutputStream outputS = new ByteArrayOutputStream();
			while ((readedLength = inputStream.read(b)) != -1) {
				outputS.write(b, 0, readedLength);
			}
			result = outputS.toString();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public interface timeoutListener {
		void TimeOutListener();
	}
	
	public void setListener(timeoutListener listener) {
		NetworkService.listener = listener;
	}

}
