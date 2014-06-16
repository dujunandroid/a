package com.boqii.petlifehouse.baseactivities;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	private String Flag = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Flag = getActivity().getClass().getName();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(Flag);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(Flag);
	}

	public void DogXihu(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_dogSrv_xihu");
	}
	
	public void DogJiyang(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_dogSrv_jiyang");
	}
	
	public void DogZaoxing(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_dogSrv_zaoxing");
	}
	
	public void DogJueyu(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_dogSrv_jueyu");
	}
	
	public void DogGengduo(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_dogSrv_more");
	}
	
	public void CatXihu(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_catSrv_xihu");
	}
	
	public void CatJiyang(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_catSrv_jiyang");
	}
	
	public void CatYiliao(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_catSrv_yiliao");
	}
	
	public void CatJueyu(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_catSrv_jueyu");
	}
	
	public void CatGengduo(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_catSrv_more");
	}
	
	public void Banner(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_banner");
	}
	
	public void Near(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_nearSrv");
	}
	
	public void Hot(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_hot");
	}
	
	public void LowPrice(){
		MobclickAgent.onEvent(getActivity(), "lifeIndex_lowPrice");
	}
	
	public void NearMerchant(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_nearMerchant");
	}
	
	public void NearTicket(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_nearSrv");
	}
	
	public void MapIcon(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_nearMap");
	}
	
	public void NearTicketType(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_srvList_classification");
	}
	
	public void NearTicketSort(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_srvList_sort");
	}
	
	public void NearMerchantType(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_merchantList_classification");
	}
	
	public void NearMerchantSort(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_merchantList_sort");
	}
	
	public void MapFocus(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_map_merchant");
	}
	
	public void MapGoToMerchant(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_map_gotoMerchant");
	}
	
	public void MapCallTel(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_map_call");
	}
	
	public void MapMerchantDetail(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_map_merchantDetail");
	}
	
	public void MapList(){
		MobclickAgent.onEvent(getActivity(), "lifeNear_map_srvList");
	}
	
	public void ListTicket(){
		MobclickAgent.onEvent(getActivity(), "lifeList_srv");
	}
	
	public void ListMerchant(){
		MobclickAgent.onEvent(getActivity(), "lifeList_merchant");
	}
	
	public void ListTicketType(){
		MobclickAgent.onEvent(getActivity(), "lifeList_srv_classification");
	}
	
	public void ListTicketSort(){
		MobclickAgent.onEvent(getActivity(), "lifeList_srv_sort");
	}
	
	public void ListMerchantType(){
		MobclickAgent.onEvent(getActivity(), "lifeList_merchant_classification");
	}
	
	public void ListMerchantArea(){
		MobclickAgent.onEvent(getActivity(), "lifeList_merchant_area");
	}
	
	public void ListMerchantSort(){
		MobclickAgent.onEvent(getActivity(), "lifeList_merchant_sort");
	}
	
	public void TicketDetailBuyNow(){
		MobclickAgent.onEvent(getActivity(), "lifeSrvDetail_buyNow");
	}
	
	public void TicketDetailMerchantDetail(){
		MobclickAgent.onEvent(getActivity(), "lifeSrvDetail_merchant");
	}
	
	public void TicketDetailMerchantTel(){
		MobclickAgent.onEvent(getActivity(), "lifeSrvDetail_merchant_call");
	}
	
	public void TicketDetailShowPro(){
		MobclickAgent.onEvent(getActivity(), "lifeSrvDetail_showPro");
	}
	
	public void TicketDetailMerchantDetailDown(){
		MobclickAgent.onEvent(getActivity(), "lifeSrvDetail_toMerchant");
	}
	
	public void MerchantDetailAddress(){
		MobclickAgent.onEvent(getActivity(), "lifeMerchantDetail_address");
	}
	
	public void MerchantDetailTel(){
		MobclickAgent.onEvent(getActivity(), "lifeMerchantDetail_call");
	}
	
	public void MerchantDetailTicketDetail(){
		MobclickAgent.onEvent(getActivity(), "lifeMerchantDetail_srv");
	}
	
	public void MerchantDetailEnviroment(){
		MobclickAgent.onEvent(getActivity(), "lifeMerchantDetail_environment");
	}
	
	public void PayUseCoupon(){
		MobclickAgent.onEvent(getActivity(), "lifePay_useTicket");
	}
	
	public void PayRemoveCoupon(){
		MobclickAgent.onEvent(getActivity(), "lifePay_removeTicket");
	}
	
	public void PayUseBalance(){
		MobclickAgent.onEvent(getActivity(), "lifePay_useBalance");
	}
	
	public void PayRemoveBalance(){
		MobclickAgent.onEvent(getActivity(), "lifePay_removeBalance");
	}
	
	public void UserCenterLogin(){
		MobclickAgent.onEvent(getActivity(), "userCenter_login");
	}
	
	public void UserCenterAll(){
		MobclickAgent.onEvent(getActivity(), "userCenter_allOrder");
	}
	
	public void UserCenterUnpay(){
		MobclickAgent.onEvent(getActivity(), "userCenter_payingOrder");
	}
	
	public void UserCenterPayed(){
		MobclickAgent.onEvent(getActivity(), "userCenter_paidOrder");
	}
	
	public void UserCenterInfo(){
		MobclickAgent.onEvent(getActivity(), "userCenter_userInformation");
	}
	
	public void UserCenterOrder(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder");
	}
	
	public void UserCenterMyTicket(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrv");
	}
	
	public void UserCenterMyCoupon(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myTicket");
	}
	
	public void UserCenterMyCollection(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myCollection");
	}
	
	public void UserCenterLogout(){
		MobclickAgent.onEvent(getActivity(), "userCenter_userInformation_loginOff");
	}
	
	public void OrderListAll(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder_all");
	}
	
	public void OrderListUnpay(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder_paying");
	}
	
	public void OrderListPayed(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder_paid");
	}
	
	public void OrderListRefunding(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder_refunding");
	}
	
	public void OrderListRefunded(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrvOrder_refunded");
	}
	
	public void MyTicketListUnused(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrv_notUsed");
	}
	
	public void MyTicketListUsed(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrv_used");
	}
	
	public void MyTicketListOutoftime(){
		MobclickAgent.onEvent(getActivity(), "userCenter_mySrv_overdue");
	}
	
	public void MyCouponListUnused(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myTicket_notUsed");
	}
	
	public void MyCouponListUsed(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myTicket_used");
	}
	
	public void MyCouponListOutoftime(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myTicket_overdue");
	}
	
	public void CouponDetailCopy(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myTicketDetail_copy");
	}
	
	public void CollectionListDelete(){
		MobclickAgent.onEvent(getActivity(), "userCenter_myCollection_delete");
	}
	
	public void LoginForget(){
		MobclickAgent.onEvent(getActivity(), "userLogin_forget");
	}
	
	public void LoginRegister(){
		MobclickAgent.onEvent(getActivity(), "userLogin_register");
	}
	
	public void LoginWeibo(){
		MobclickAgent.onEvent(getActivity(), "userLogin_weibo");
	}
	
	public void LoginQQ(){
		MobclickAgent.onEvent(getActivity(), "userLogin_qq");
	}
	
	public void LoginAlipay(){
		MobclickAgent.onEvent(getActivity(), "userLogin_alipay");
	}
	
	public void FindPasswordPhone(){
		MobclickAgent.onEvent(getActivity(), "forgetPassword_phoneNum");
	}
	
	public void FindPasswordEmail(){
		MobclickAgent.onEvent(getActivity(), "forgetPassword_mail");
	}
}
