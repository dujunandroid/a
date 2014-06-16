package com.boqii.petlifehouse.activities;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.User;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		initView();
	}
	private InputMethodManager  manager;
	private Timer mTimer;
	private EditText mAccountETxt;// 账号框
	private EditText mPasswordETxt;// 密码框
	private EditText mCodeETxt;// 短信验证码输入框
	private CheckBox mAgreeRCBox;// 是否同意
	private CheckBox mShowPasswordRCBox;// 密码显示按钮
//	private Button mRegisterBtn;
	private TextView mConfiramtionBtn;// 获取验证码按钮
	private ProgressDialog pd;
	void initView() {
		pd=new ProgressDialog(this);
		pd.setTitle(getString(R.string.register));
		pd.setMessage(getString(R.string.register_loading));
		pd.setCanceledOnTouchOutside(false);
		findViewById(R.id.backRBtn).setOnClickListener(this);
		findViewById(R.id.toLoginRBtn).setOnClickListener(this);
		findViewById(R.id.registerBtn).setOnClickListener(this);
		findViewById(R.id.protocolTxt).setOnClickListener(this);// 协议
		(mConfiramtionBtn = (TextView) findViewById(R.id.getConfirmationBtn))
				.setOnClickListener(this);// 发送验证短信
		(mShowPasswordRCBox = (CheckBox) findViewById(R.id.showPassWordRCBox))
				.setOnCheckedChangeListener(agreeListener);// 监听是否明文显示密码
		(mAgreeRCBox = (CheckBox) findViewById(R.id.agreeRBtn))
				.setOnCheckedChangeListener(agreeListener);// 监听是否同意
		mAccountETxt = (EditText) findViewById(R.id.accountRETxt);
		mPasswordETxt = (EditText) findViewById(R.id.passWordREtxt);
		mCodeETxt = (EditText) findViewById(R.id.confirmationREtxt);
	}

	private OnCheckedChangeListener agreeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.agreeRBtn:// 如果是同意按钮
				if (isChecked) {
					mAgreeRCBox
							.setBackgroundResource(R.drawable.button_checked);
//					mRegisterBtn.setEnabled(true);
//					mRegisterBtn.setBackgroundResource(R.drawable.bg_button);
				} else {
					mAgreeRCBox
							.setBackgroundResource(R.drawable.button_unchecked);				
//					mRegisterBtn.setEnabled(false);
				}
				break;

			case R.id.showPassWordRCBox:// 明文密码按钮
				if (isChecked == true) {
					mShowPasswordRCBox
							.setButtonDrawable(R.drawable.icon_view_checked);
					mPasswordETxt
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					mPasswordETxt.setSelection(mPasswordETxt.getText().length());
				} else {
					//System.out.println("隐藏");
					mShowPasswordRCBox
							.setButtonDrawable(R.drawable.icon_view_unchecked);
					mPasswordETxt.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordETxt.setSelection(mPasswordETxt.getText().length());
				}
				break;
			}

		}
	};

	

	/*// 检查code和其他信息是否符合要求，符合要求后注册此接口修改密码时才有故注释
	private void checkAotuCode(final String code, final String phoneNumber,
			final String password) {
		System.out.println("checkAotuCode...");
		checkPhoneNumber(phoneNumber);// 检查号码
		if (Util.isEmpty(password)) {
			Toast.makeText(this, "Password can not be empty",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.length() < 6) {
			Toast.makeText(this, "The password cannot be less than 6",
					Toast.LENGTH_SHORT).show();
			return;
		}
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(RegisterActivity.this)
						.CheckAuthCode(phoneNumber, code);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {
							System.out.println("checkAotuCode succ");
							Userregister(phoneNumber, password);// 登录
						}else{
							ShowToast(obj.optString("ResponseMsg"));
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					ShowToast(getResources().getString(R.string.error_null));
					return;
				}
			}
		});
	}*/

	private void sendAotuCode(final String phoneNumber) {
		if (Util.isEmpty(phoneNumber)) {
			Toast.makeText(this, getString(R.string.user_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!Util.isMobileNO(phoneNumber)) {
			Toast.makeText(this, getString(R.string.no_phonenum),
					Toast.LENGTH_SHORT).show();
			return;
		}
		//System.out.println("sendAotuCode...");
		mConfiramtionBtn.setText(R.string.loading_code);
		mConfiramtionBtn.setEnabled(false);
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String result = NetworkService.getInstance(
						RegisterActivity.this).SendAuthCode(phoneNumber);
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
							mConfiramtionBtn
									.setBackgroundResource(R.drawable.btn_sendauthcode_no);
							mConfiramtionBtn.setText(120 + getResources().getString(R.string.again_code));							
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
							ShowToast(obj.optString("ResponseMsg"));
							mConfiramtionBtn.setEnabled(true);
							mConfiramtionBtn.setBackgroundResource(R.drawable.btn_sendauthcode);
							mConfiramtionBtn.setText(getResources().getString(R.string.get_code));
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	// 注册
	private void userRegister(final String phoneNumber, final String password,final String authCode) {
		//System.out.println("Userregister...");
		if (Util.isEmpty(phoneNumber)) {
			Toast.makeText(this, getString(R.string.user_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!Util.isMobileNO(phoneNumber)) {
			Toast.makeText(this, getString(R.string.no_phonenum),
					Toast.LENGTH_SHORT).show();
			return;
		}// 检查号码
		if (Util.isEmpty(password)) {
			Toast.makeText(this, getString(R.string.password_null),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.length() < 6) {
			Toast.makeText(this,getString(R.string.password_6),
					Toast.LENGTH_SHORT).show();
			return;
		}
		pd.show();
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				//System.out.println(phoneNumber+"==="+password);
				return NetworkService.getInstance(RegisterActivity.this)
						.UserRegister(phoneNumber, password,authCode);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						//System.out.println(result+"=register");
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							User user = new User();// 得到用户信息并赋值
							user.NickName = data.optString("NickName");
							user.UserID = data.optString("UserId");
							BaseApplication application = (BaseApplication) getApplication();
							application.user = user;
							ShowToast(getString(R.string.register_suc));							
							finish();						
						}else{
							ShowToast(obj.optString("ResponseMsg"));						
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}finally{
						pd.dismiss();
					}
				}else{				
					ShowToast(getResources().getString(R.string.error_null));
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backRBtn:
			finish();
			break;
		case R.id.registerBtn:		
			if(mAgreeRCBox.isChecked()){
				String code = mCodeETxt.getText().toString().trim();
				String phoneNumber = mAccountETxt.getText().toString().trim();
				String password = mPasswordETxt.getText().toString().trim();
				try {
					password = Util.getMD5(password);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				userRegister(phoneNumber, password,code);
			}else{
				ShowToast(getString(R.string.toast));
			}
			
			break;
		case R.id.protocolTxt:// 协议
			Intent mPIntent = new Intent();
			mPIntent.setClass(this, ProtocolsActivity.class);
			startActivity(mPIntent);
			break;
		case R.id.toLoginRBtn:
			finish();
			break;
		case R.id.getConfirmationBtn:// 发送短信
			//mConfiramtionBtn.setText(getResources().getString(R.string.loading_code));
			String phoneNumberC = mAccountETxt.getText().toString().trim();
			sendAotuCode(phoneNumberC);
			break;
		}
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
					mConfiramtionBtn.setText(getResources().getString(R.string.get_code));
					mConfiramtionBtn.setEnabled(true);
					mConfiramtionBtn.setBackgroundResource(R.drawable.btn_sendauthcode);
				} else {
					mConfiramtionBtn.setText(msg.arg1 + getResources().getString(R.string.again_code));
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
