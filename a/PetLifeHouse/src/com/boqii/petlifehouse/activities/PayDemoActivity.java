package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.OnlinePaymentService;
import com.boqii.petlifehouse.baseservice.OnlinePaymentService.PayListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PayDemoActivity extends BaseActivity implements OnClickListener/*, PayListener*/, PayListener{

	private OnlinePaymentService payService;

//	private static final int RQF_PAY = 1;
//
//	private static final int RQF_LOGIN = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paydemo);
		payService = new OnlinePaymentService(this, this);
		((Button)findViewById(R.id.pay)).setOnClickListener(this);
		((Button)findViewById(R.id.pay2)).setOnClickListener(this);
		((Button)findViewById(R.id.pay3)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
//		final String payMessage = "partner=\"2088701092093630\"0026seller=\"2088701092093630\"0026out_trade_no=\"0000006590\"0026subject=\"波奇宠物\"0026body=\"波奇宠物产品\"0026total_fee=\"6.00\"0026notify_url=\"http%3a%2f%2f42.121.144.195%2fboqi%2fpayment%2fAlipayCallback%2f3\"0026sign=\"euFW6si6rGSktdHRc17Mt2MR2uYH7jcJFsC0zcPbxytIyDyhjvQSLRAWxWF6M9TJ9IMkXEFGm8TU6oONitY8iiTt2HfapRu79gFHzPp8fthK9Ulj6fpM2DTgPgIBGQMsWWZumvFnCl2tLbXMg6oiaDGGtSYAReW%2blbxSUtPdJf8%3d\"0026sign_type=\"RSA\"";
		final String payMessage = "partner=\"2088101568358171\"&seller_id=\"alipay-test09@alipay.com\"&out_trade_no=\"0819145412-6177\"&subject=\"《暗黑破坏神3:凯恩之书》\"&body=\"暴雪唯一官方授权中文版!玩家必藏!附赠暗黑精致手绘地图!绝不仅仅是一本暗黑的故事或画册，而是一个栩栩如生的游戏再现。是游戏玩家珍藏的首选。\"&total_fee=\"0.01\"&notify_url=\"http%3A%2F%2Fnotify.msp.hk%2Fnotify.htm\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&sign=\"lBBK%2F0w5LOajrMrji7DUgEqNjIhQbidR13GovA5r3TgIbNqv231yC1NksLdw%2Ba3JnfHXoXuet6XNNHtn7VE%2BeCoRO1O%2BR1KugLrQEZMtG5jmJIe2pbjm%2F3kb%2FuGkpG%2BwYQYI51%2BhA3YBbvZHVQBYveBqK%2Bh8mUyb7GM1HxWs9k4%3D\"&sign_type=\"RSA\"";
		switch (v.getId()) {
		case R.id.pay:
			payService.startAlipay(payMessage);

			break;
		case R.id.pay2:
//			payService.payUnion(payMessage);
			break;
		case R.id.pay3:
			startActivity(new Intent(this, AliWebPayActivity.class).putExtra("URL", "http://42.121.144.195/aspx/Paychannel.aspx?orderid=6124"));
			break;

		default:
			break;
		}
	}
//
//	@Override
//	public void alipaySuccess() {
//		ShowToast("alipay sussess");
//	}
//
//	@Override
//	public void orderDoneSuccess(int id, String xml) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void orderDoneFailed(String message) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void alipaySuccess() {
		// TODO Auto-generated method stub
		
	}

}
