package com.boqii.petlifehouse.baseservice;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.service.purchase.AlixId;
import com.boqii.petlifehouse.service.purchase.BaseHelper;
import com.boqii.petlifehouse.service.purchase.MobileSecurePayHelper;
import com.boqii.petlifehouse.service.purchase.MobileSecurePayer;
import com.boqii.petlifehouse.service.purchase.ResultChecker;
import com.unionpay.upomp.yidatec.transactionmanage.SplashActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

public class OnlinePaymentService {
	private PayListener listener;
	private Context context;
	
	public OnlinePaymentService(Context context, PayListener payListener){
		this.context = context;
		this.listener = payListener;
	}
	
	public void payUnion(String xml){
		if(TextUtils.isEmpty(xml))
			return;
		Intent intent = new Intent(this.context,SplashActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("xml", xml);
		mBundle.putString("sign",context.getString(R.string.unionkey));
		intent.putExtras(mBundle);
		context.startActivity(intent);
	}
	
//	public void PayOrder(String UserId, int OrderId, String CouponNo, String payPassword, int UsedBalance, int PaymentType){
//		String result = NetworkService.getInstance(context).PayOrder(UserId, OrderId, CouponNo, payPassword, UsedBalance, PaymentType);
//		DataAnaly(result);
//	}
	
//	private void DataAnaly(String result) {
//		String info = "";
//		if(!TextUtils.isEmpty(result)){
//			try {
//				JSONObject o = new JSONObject(result);
//				if(o.getInt("ResponseStatus") == 0){
//					JSONObject data = o.getJSONObject("ResponseData");
//					info = data.getString("PaymentMessage");
//					int id = data.optInt("OrderID");
//					listener.orderDoneSuccess(id, info);
//					return;
//				}else{
//					return;
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				listener.orderDoneFailed("");
//				return;
//			}
//		}
//		listener.orderDoneFailed("");
//		return;		
//	}
	 
	public void startAlipay(String info){
		if(TextUtils.isEmpty(info))
			return;
//		String info = "";
//		try {
//			JSONObject o = new JSONObject(result);
//			info = o.getString("ResponseData");
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		// check to see if the MobileSecurePay is already installed.
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(context);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist)
			return;

		// start pay for this order.
		try {
			// prepare the order info.

			// start the pay.
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(info, mAliHandler, AlixId.RQF_PAY, (Activity)context);

			if (bRet) {
				// show the progress bar to indicate that we have started
				// paying.
				closeProgressForAlipay();
//				mAlipayProgress = BaseHelper.showProgress(context, null,
//						context.getString(R.string.paying), false, true);
			} else
				;
		} catch (Exception ex) {
			Toast.makeText(context,context.getString(R.string.remote_call_failed), Toast.LENGTH_LONG).show();
		}
		
	}

	private ProgressDialog mAlipayProgress = null;

	void closeProgressForAlipay() {
		try {
			if (mAlipayProgress != null) {
				mAlipayProgress.dismiss();
				mAlipayProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mAliHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				String memoDisplay = null;

				switch (msg.what) {
				case AlixId.RQF_PAY: {
					closeProgressForAlipay();

					try {
						String memo = "memo={";
						int imemoStart = strRet.indexOf("memo={");
						imemoStart += memo.length();
						int imemoEnd = strRet.indexOf("};result=");
						memo = strRet.substring(imemoStart, imemoEnd);
						memoDisplay = new String(memo);

						ResultChecker resultChecker = new ResultChecker(strRet);

						if (resultChecker.isPayOk()) { // alipay success
							listener.alipaySuccess();
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(context,context.getString(R.string.tips), memoDisplay, R.drawable.infoicon);
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public interface PayListener{
		void alipaySuccess();
//		void orderDoneSuccess(int id, String xml);
//		void orderDoneFailed(String message);
	}
}
