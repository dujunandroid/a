package com.boqii.petlifehouse.activities;

import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class FindPasswordActivity extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}

	private InputMethodManager manager;
	private EditText mPhoneETxt;// 填写手机号码的编辑框
	private TextView mGetCFPTxt;// 获取验证码
	private EditText mCodeETxt;// 填写验证码的编辑框
	private Timer mTimer;// 计时器
	public static final String CODE_TAG = "Code";
	public static final String PHONENUMBER_TAG = "PhoneNumber";

	void initView() {
		findViewById(R.id.nextFPBtn).setOnClickListener(this);
		(mGetCFPTxt = (TextView) findViewById(R.id.getConfirmationFPBtn))
				.setOnClickListener(this);
		findViewById(R.id.backFP).setOnClickListener(this);
		mPhoneETxt = (EditText) findViewById(R.id.pFPETxt);
		mCodeETxt = (EditText) findViewById(R.id.confirmationFPEtxt);
	}

	@Override
	public void onClick(View v) {
		String phoneNumber = mPhoneETxt.getText().toString().trim();
		switch (v.getId()) {
		case R.id.nextFPBtn:
			String code = mCodeETxt.getText().toString().trim();
			checkAuthCode(code, phoneNumber);
			//System.out.println(code + "====" + phoneNumber + "---");
			break;
		case R.id.getConfirmationFPBtn:
			sendAuthCode(phoneNumber);
			break;
		case R.id.backFP:
			finish();
			break;
		}

	}

	// 检查code和其他信息是否匹配
	private void checkAuthCode(final String code, final String phoneNumber) {
		//System.out.println("checkAotuCode...");
		if (Util.isEmpty(phoneNumber)) {
			Toast.makeText(this, getString(R.string.user_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!Util.isMobileNO(phoneNumber)) {
			Toast.makeText(this,getString(R.string.no_phonenum),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(FindPasswordActivity.this)
						.CheckAuthCode(phoneNumber, code);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {// 如果验证成功跳转到修改密码
							ShowToast(getResources().getString(
									R.string.findpassword_check_ok));
							Intent mIntent = new Intent();
							mIntent.setClass(FindPasswordActivity.this,
									UpdatePasswordActivity.class);
							mIntent.putExtra(CODE_TAG, code);
							mIntent.putExtra(PHONENUMBER_TAG, phoneNumber);
							startActivity(mIntent);
						} else {
							ShowToast(obj.optString("ResponseMsg"));
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					ShowToast(getResources().getString(R.string.error_null));
					return;
				}
			}
		});
	}

	private void sendAuthCode(final String phoneNumber) {
		if (Util.isEmpty(phoneNumber)) {
			Toast.makeText(this, getString(R.string.phone_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!Util.isMobileNO(phoneNumber)) {
			Toast.makeText(this, getString(R.string.no_phonenum),
					Toast.LENGTH_SHORT).show();
			return;
		}
		mGetCFPTxt.setText(R.string.loading_code);
		mGetCFPTxt.setEnabled(false);
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String result = NetworkService.getInstance(
						FindPasswordActivity.this).SendAuthCode(phoneNumber);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {
							ShowToast(obj.optString("ResponseMsg"));
							mGetCFPTxt
									.setBackgroundResource(R.drawable.btn_sendauthcode_no);
							mGetCFPTxt.setText(120 + getResources().getString(
									R.string.again_code));
							mTimer = new Timer();// 计时器
							mTimer.schedule(new TimerTask() {
								@Override
								public void run() {
									Message message = new Message();
									message.what = 1;
									time--;
									message.arg1 = time;
									mHandler.sendMessage(message);// 发送消息
								}
							}, 1000, 1000);
						} else {
							mGetCFPTxt.setEnabled(true);
							mGetCFPTxt.setBackgroundResource(R.drawable.btn_sendauthcode);
							mGetCFPTxt.setText(getResources().getString(
									R.string.get_code));
							ShowToast(obj.optString("ResponseMsg"));
						}
					} catch (JSONException e) {
						//System.out.println("返回数据出错！");
						e.printStackTrace();
					}
				}
			}
		});
	}

	public int time = 120;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
			case 1:
				if (time <= 0) {
					mGetCFPTxt.setText(getResources().getString(
							R.string.get_code));
					mGetCFPTxt.setEnabled(true);
					mGetCFPTxt
							.setBackgroundResource(R.drawable.btn_sendauthcode);
				} else {
					mGetCFPTxt.setText(msg.arg1
							+ getResources().getString(R.string.again_code));
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mTimer) {
			mTimer.cancel();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
