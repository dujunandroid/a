package com.boqii.petlifehouse.activities;

import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.app.sdk.AliPay;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Coupon;
import com.boqii.petlifehouse.entities.Order;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PayOrderActivity extends BaseActivity implements OnClickListener,
		TextWatcher, OnCheckedChangeListener{

//	private OnlinePaymentService payService;
	

	private Order order;
	private HttpManager mHttpManager;
	private TextView coupon_price, balance_used, need_pay;
	private BaseApplication app;
	private float needPay;
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payorder);
		progress = new ProgressBar(this);
		mHttpManager = new HttpManager(this);
//		payService = new OnlinePaymentService(this, this);
		app = getApp();
		initView();
	}

	private void initView() {
		order = (Order) this.getIntent().getSerializableExtra("ORDER");
		((ImageView) findViewById(R.id.back)).setOnClickListener(this);
		((TextView) findViewById(R.id.ticket_name)).setText(!Util.isEmpty(order.mTicket.TicketTitle) ? order.mTicket.TicketTitle : order.OrderTitle);
		((TextView) findViewById(R.id.price)).setText(getString(R.string.price, order.OrderPrice));
		((TextView) findViewById(R.id.message)).setText(getString(R.string.order_info, !Util.isEmpty(order.Mobile) ? order.Mobile : order.OrderTel));
		((EditText) findViewById(R.id.coupon_no)).addTextChangedListener(this);
		((CheckBox) findViewById(R.id.coupon_check)).setOnCheckedChangeListener(this);
		((CheckBox)findViewById(R.id.balance_check)).setOnCheckedChangeListener(this);
		((Button)findViewById(R.id.sure)).setOnClickListener(this);
		((TextView)findViewById(R.id.pay)).setOnClickListener(this);
		((CheckBox)findViewById(R.id.alipay_check)).setOnCheckedChangeListener(this);
		((CheckBox)findViewById(R.id.alipay_web_check)).setOnCheckedChangeListener(this);
		DecimalFormat df=new DecimalFormat("#0.00");
		((TextView)findViewById(R.id.mybalance)).setText(getString(R.string.yuan, df.format(app.user.Balance)));
		((TextView)findViewById(R.id.topay)).setText(getString(R.string.price, order.OrderPrice));
		coupon_price = (TextView)findViewById(R.id.coupon_price);
		balance_used = (TextView)findViewById(R.id.balance_used);
		need_pay = (TextView)findViewById(R.id.needpay);
		initPriceText();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.sure:
			String password = ((EditText)findViewById(R.id.password)).getText().toString();
			if(!Util.isEmpty(password))
				CheckPassword(((EditText)findViewById(R.id.password)).getText().toString());
			else{
				Toast.makeText(this, getString(R.string.password), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.pay:
			boolean balance_check = ((CheckBox)findViewById(R.id.balance_check)).isChecked();
			if(needPay > 0 && !balance_check && !((CheckBox)findViewById(R.id.alipay_check)).isChecked()){
				ShowToast(getString(R.string.select_payment));
				return;
			}
			PayOrder();
			break;

		default:
			break;
		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
//			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case 1:
			case 2: 
				String result = (String)msg.obj;
				if(!Util.isEmpty(result) && result.contains("resultStatus={9000}")){
					ShowToast(getString(R.string.pay_success));
					getApp().user.Balance -= balanceUsed;
					startActivity(new Intent(PayOrderActivity.this, MyOrderActivity.class).putExtra("INDEX", 3));
				}else{
					String message = result.substring(result.indexOf("memo={") + 6, result.indexOf("};result"));
					Toast.makeText(PayOrderActivity.this, message, Toast.LENGTH_SHORT).show();
				}
//				Toast.makeText(PayOrderActivity.this, result.getResult(),
//						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	private void PayOrder() {
		progress.setVisibility(View.VISIBLE);
		mHttpManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				boolean coupon_check = ((CheckBox)findViewById(R.id.coupon_check)).isChecked();
				boolean balance_check = ((CheckBox)findViewById(R.id.balance_check)).isChecked();
				int paymentType = 0;
				if(((CheckBox)findViewById(R.id.alipay_check)).isChecked())
					paymentType = 1;
				if(((CheckBox)findViewById(R.id.alipay_web_check)).isChecked())
					paymentType = 2;
				String password = ((EditText)findViewById(R.id.password)).getText().toString();
				return NetworkService.getInstance(PayOrderActivity.this).PayOrder(app.user.UserID, order.OrderId, coupon_check ? (order.mCoupon!= null) ? order.mCoupon.CouponNo : "" : "", balance_check ? password : "", balance_check ? 1 : 0, paymentType);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				progress.setVisibility(View.GONE);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							final String paymessage = data.optString("PayMessage");
							if (data != null) {
								int type = data.optInt("Type");
								switch (type) {
								case 0:// 余额支付
									getApp().user.Balance -= balanceUsed;
									ShowToast(getString(R.string.pay_success));
									startActivity(new Intent(PayOrderActivity.this, MyOrderActivity.class).putExtra("INDEX", 3));
									break;
								case 1:// 支付宝客户端
									new Thread(){
										@Override
										public void run() {
											//获取Alipay对象，构造参数为当前activity和handler实例对象
											AliPay alipay = new AliPay(PayOrderActivity.this, mHandler);
											//调用pay方法，将订单信息传入
											String result = alipay.pay(paymessage);
											Message msg = new Message();
											msg.what = 1;
											msg.obj = result;
											mHandler.sendMessage(msg);
										}
										
									}.start();
									break;
								case 2:// 支付宝网页
									startActivity(new Intent(PayOrderActivity.this, AliWebPayActivity.class).putExtra("URL", paymessage));
									break;

								default:
									break;
								}
							}
						}else{
							Toast.makeText(PayOrderActivity.this, obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	boolean isFinish = false;//是否已经完成优惠券输入
	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		if(s.toString().length() < 11)
			isFinish = false;
		else if(s.toString().length() == 11)
			isFinish = true;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().length() > 11) {
			((EditText) findViewById(R.id.coupon_no)).setText(s.subSequence(0,11));
			((EditText) findViewById(R.id.coupon_no)).setSelection(s.toString().length() - 1);
		}else if(s.toString().length() == 11){
			if(!isFinish){
				CheckCoupon(s.toString());
			}
		}

	}

	public void collapseSoftInputMethod(EditText inputText){
		InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0); 
	}
	
	private void CheckPassword(final String password) {
		progress.setVisibility(View.VISIBLE);
		mHttpManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(PayOrderActivity.this).CheckPayPassword(app.user.UserID, Util.getMD5(password));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				progress.setVisibility(View.GONE);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							((LinearLayout)findViewById(R.id.password_layout)).setVisibility(View.GONE);
							collapseSoftInputMethod((EditText)findViewById(R.id.password));
							initPriceText();
						}else{
							Toast.makeText(PayOrderActivity.this, obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void CheckCoupon(final String coupon) {
		order.mCoupon.CouponNo = coupon;
		progress.setVisibility(View.VISIBLE);
		mHttpManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(PayOrderActivity.this).CheckCoupon(app.user.UserID, order.OrderId, coupon);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				progress.setVisibility(View.GONE);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							Coupon coupon = Coupon.JsonToSelf(data);
							coupon.CouponNo = order.mCoupon.CouponNo;
							order.mCoupon = coupon;
							initPriceText();
						}else{
							Toast.makeText(getApplicationContext(), obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	float balanceUsed;
	/**
	 * 计算应付，优惠券，余额，还需支付等金额
	 */
	protected void initPriceText() {
		boolean coupon_check = ((CheckBox)findViewById(R.id.coupon_check)).isChecked();
		DecimalFormat df=new DecimalFormat("#0.00");
		if(order == null)
			return;
		float couponPrice = coupon_check ? (order.mCoupon != null) ? order.mCoupon.CouponPrice : 0.0f : 0.00f;
		coupon_price.setText("-" + getString(R.string.price, df.format(couponPrice)));
		boolean balance_check = ((CheckBox)findViewById(R.id.balance_check)).isChecked();
				
		balanceUsed = balance_check ? Math.min(app.user.Balance, order.OrderPrice - (coupon_check ? (order.mCoupon != null) ? order.mCoupon.CouponPrice : 0 : 0)) : 0.00f;
		balance_used.setText("-" + getString(R.string.price, df.format(balanceUsed)));
		((TextView)findViewById(R.id.usebalance)).setText(getString(R.string.price, df.format(balanceUsed)));
		needPay = order.OrderPrice - couponPrice - balanceUsed;
		need_pay.setText(getString(R.string.price, df.format(needPay)));
		((CheckBox)findViewById(R.id.alipay_check)).setChecked(!(needPay <= 0));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.coupon_check:
			if(isChecked)
				PayUseCoupon();
			else
				PayRemoveCoupon();
			((EditText) findViewById(R.id.coupon_no)).setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
			initPriceText();
			break;
		case R.id.balance_check:
			if(isChecked)
				PayUseBalance();
			else
				PayRemoveBalance();
			((LinearLayout)findViewById(R.id.password_layout)).setVisibility(isChecked ? View.VISIBLE : View.GONE);
			((EditText)findViewById(R.id.password)).setText("");
			initPriceText();
			break;
		case R.id.alipay_check:
			if(isChecked)
				((CheckBox)findViewById(R.id.alipay_web_check)).setChecked(false);
			break;
		case R.id.alipay_web_check:
			if(isChecked)
				((CheckBox)findViewById(R.id.alipay_check)).setChecked(false);
			break;

		default:
			break;
		}
	}

}
