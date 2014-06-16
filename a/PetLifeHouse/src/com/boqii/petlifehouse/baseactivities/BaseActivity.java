package com.boqii.petlifehouse.baseactivities;

import com.boqii.petlifehouse.activities.LoginActivity;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class BaseActivity extends Activity {

	private BaseApplication app;
	
	public void DogXihu(){
		MobclickAgent.onEvent(this, "lifeIndex_dogSrv_xihu");
	}
	
	public void DogJiyang(){
		MobclickAgent.onEvent(this, "lifeIndex_dogSrv_jiyang");
	}
	
	public void DogZaoxing(){
		MobclickAgent.onEvent(this, "lifeIndex_dogSrv_zaoxing");
	}
	
	public void DogJueyu(){
		MobclickAgent.onEvent(this, "lifeIndex_dogSrv_jueyu");
	}
	
	public void DogGengduo(){
		MobclickAgent.onEvent(this, "lifeIndex_dogSrv_more");
	}
	
	public void CatXihu(){
		MobclickAgent.onEvent(this, "lifeIndex_catSrv_xihu");
	}
	
	public void CatJiyang(){
		MobclickAgent.onEvent(this, "lifeIndex_catSrv_jiyang");
	}
	
	public void CatYiliao(){
		MobclickAgent.onEvent(this, "lifeIndex_catSrv_yiliao");
	}
	
	public void CatJueyu(){
		MobclickAgent.onEvent(this, "lifeIndex_catSrv_jueyu");
	}
	
	public void CatGengduo(){
		MobclickAgent.onEvent(this, "lifeIndex_catSrv_more");
	}
	
	public void Banner(){
		MobclickAgent.onEvent(this, "lifeIndex_banner");
	}
	
	public void Near(){
		MobclickAgent.onEvent(this, "lifeIndex_nearSrv");
	}
	
	public void Hot(){
		MobclickAgent.onEvent(this, "lifeIndex_hot");
	}
	
	public void LowPrice(){
		MobclickAgent.onEvent(this, "lifeIndex_lowPrice");
	}
	
	public void NearMerchant(){
		MobclickAgent.onEvent(this, "lifeNear_nearMerchant");
	}
	
	public void NearTicket(){
		MobclickAgent.onEvent(this, "lifeNear_nearSrv");
	}
	
	public void MapIcon(){
		MobclickAgent.onEvent(this, "lifeNear_nearMap");
	}
	
	public void NearTicketType(){
		MobclickAgent.onEvent(this, "lifeNear_srvList_classification");
	}
	
	public void NearTicketSort(){
		MobclickAgent.onEvent(this, "lifeNear_srvList_sort");
	}
	
	public void NearMerchantType(){
		MobclickAgent.onEvent(this, "lifeNear_merchantList_classification");
	}
	
	public void NearMerchantSort(){
		MobclickAgent.onEvent(this, "lifeNear_merchantList_sort");
	}
	
	public void MapFocus(){
		MobclickAgent.onEvent(this, "lifeNear_map_merchant");
	}
	
	public void MapGoToMerchant(){
		MobclickAgent.onEvent(this, "lifeNear_map_gotoMerchant");
	}
	
	public void MapCallTel(){
		MobclickAgent.onEvent(this, "lifeNear_map_call");
	}
	
	public void MapMerchantDetail(){
		MobclickAgent.onEvent(this, "lifeNear_map_merchantDetail");
	}
	
	public void MapList(){
		MobclickAgent.onEvent(this, "lifeNear_map_srvList");
	}
	
	public void ListTicket(){
		MobclickAgent.onEvent(this, "lifeList_srv");
	}
	
	public void ListMerchant(){
		MobclickAgent.onEvent(this, "lifeList_merchant");
	}
	
	public void ListTicketType(){
		MobclickAgent.onEvent(this, "lifeList_srv_classification");
	}
	
	public void ListTicketSort(){
		MobclickAgent.onEvent(this, "lifeList_srv_sort");
	}
	
	public void ListMerchantType(){
		MobclickAgent.onEvent(this, "lifeList_merchant_classification");
	}
	
	public void ListMerchantArea(){
		MobclickAgent.onEvent(this, "lifeList_merchant_area");
	}
	
	public void ListMerchantSort(){
		MobclickAgent.onEvent(this, "lifeList_merchant_sort");
	}
	
	public void TicketDetailBuyNow(){
		MobclickAgent.onEvent(this, "lifeSrvDetail_buyNow");
	}
	
	public void TicketDetailMerchantDetail(){
		MobclickAgent.onEvent(this, "lifeSrvDetail_merchant");
	}
	
	public void TicketDetailMerchantTel(){
		MobclickAgent.onEvent(this, "lifeSrvDetail_merchant_call");
	}
	
	public void TicketDetailShowPro(){
		MobclickAgent.onEvent(this, "lifeSrvDetail_showPro");
	}
	
	public void TicketDetailMerchantDetailDown(){
		MobclickAgent.onEvent(this, "lifeSrvDetail_toMerchant");
	}
	
	public void MerchantDetailAddress(){
		MobclickAgent.onEvent(this, "lifeMerchantDetail_address");
	}
	
	public void MerchantDetailTel(){
		MobclickAgent.onEvent(this, "lifeMerchantDetail_call");
	}
	
	public void MerchantDetailTicketDetail(){
		MobclickAgent.onEvent(this, "lifeMerchantDetail_srv");
	}
	
	public void MerchantDetailEnviroment(){
		MobclickAgent.onEvent(this, "lifeMerchantDetail_environment");
	}
	
	public void PayUseCoupon(){
		MobclickAgent.onEvent(this, "lifePay_useTicket");
	}
	
	public void PayRemoveCoupon(){
		MobclickAgent.onEvent(this, "lifePay_removeTicket");
	}
	
	public void PayUseBalance(){
		MobclickAgent.onEvent(this, "lifePay_useBalance");
	}
	
	public void PayRemoveBalance(){
		MobclickAgent.onEvent(this, "lifePay_removeBalance");
	}
	
	public void UserCenterLogin(){
		MobclickAgent.onEvent(this, "userCenter_login");
	}
	
	public void UserCenterAll(){
		MobclickAgent.onEvent(this, "userCenter_allOrder");
	}
	
	public void UserCenterUnpay(){
		MobclickAgent.onEvent(this, "userCenter_payingOrder");
	}
	
	public void UserCenterPayed(){
		MobclickAgent.onEvent(this, "userCenter_paidOrder");
	}
	
	public void UserCenterInfo(){
		MobclickAgent.onEvent(this, "userCenter_userInformation");
	}
	
	public void UserCenterOrder(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder");
	}
	
	public void UserCenterMyTicket(){
		MobclickAgent.onEvent(this, "userCenter_mySrv");
	}
	
	public void UserCenterMyCoupon(){
		MobclickAgent.onEvent(this, "userCenter_myTicket");
	}
	
	public void UserCenterMyCollection(){
		MobclickAgent.onEvent(this, "userCenter_myCollection");
	}
	
	public void UserCenterLogout(){
		MobclickAgent.onEvent(this, "userCenter_userInformation_loginOff");
	}
	
	public void OrderListAll(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder_all");
	}
	
	public void OrderListUnpay(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder_paying");
	}
	
	public void OrderListPayed(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder_paid");
	}
	
	public void OrderListRefunding(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder_refunding");
	}
	
	public void OrderListRefunded(){
		MobclickAgent.onEvent(this, "userCenter_mySrvOrder_refunded");
	}
	
	public void MyTicketListUnused(){
		MobclickAgent.onEvent(this, "userCenter_mySrv_notUsed");
	}
	
	public void MyTicketListUsed(){
		MobclickAgent.onEvent(this, "userCenter_mySrv_used");
	}
	
	public void MyTicketListOutoftime(){
		MobclickAgent.onEvent(this, "userCenter_mySrv_overdue");
	}
	
	public void MyCouponListUnused(){
		MobclickAgent.onEvent(this, "userCenter_myTicket_notUsed");
	}
	
	public void MyCouponListUsed(){
		MobclickAgent.onEvent(this, "userCenter_myTicket_used");
	}
	
	public void MyCouponListOutoftime(){
		MobclickAgent.onEvent(this, "userCenter_myTicket_overdue");
	}
	
	public void CouponDetailCopy(){
		MobclickAgent.onEvent(this, "userCenter_myTicketDetail_copy");
	}
	
	public void CollectionListDelete(){
		MobclickAgent.onEvent(this, "userCenter_myCollection_delete");
	}
	
	public void LoginForget(){
		MobclickAgent.onEvent(this, "userLogin_forget");
	}
	
	public void LoginRegister(){
		MobclickAgent.onEvent(this, "userLogin_register");
	}
	
	public void LoginWeibo(){
		MobclickAgent.onEvent(this, "userLogin_weibo");
	}
	
	public void LoginQQ(){
		MobclickAgent.onEvent(this, "userLogin_qq");
	}
	
	public void LoginAlipay(){
		MobclickAgent.onEvent(this, "userLogin_alipay");
	}
	
	public void FindPasswordPhone(){
		MobclickAgent.onEvent(this, "forgetPassword_phoneNum");
	}
	
	public void FindPasswordEmail(){
		MobclickAgent.onEvent(this, "forgetPassword_mail");
	}
	
	public void ShowToast(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	};
	
	public BaseApplication getApp(){
		if(app == null)
			app = (BaseApplication) getApplication();
		return app;
	}
	
	public void UserLogin(){
		startActivity(new Intent(this, LoginActivity.class));
	}
	
	public void UserLoginForResult(int requestCode){
		startActivityForResult(new Intent(this, LoginActivity.class), requestCode);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onStart();
		MobclickAgent.onPageStart(getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName());
		MobclickAgent.onPause(this);
	}

}
