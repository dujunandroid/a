package com.boqii.petlifehouse.activities;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.User;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class LoginActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		setContentView(R.layout.login);
		sp = getSharedPreferences(LOGIN_SP, MODE_PRIVATE);
		editor = sp.edit();// 初始化
		editor.clear();// 进入登录页就清空所有账号信息
		editor.commit();// 提交修改
		new QZone(this).removeAccount();
		new SinaWeibo(this).removeAccount(); 
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}

	private InputMethodManager manager;
	public static final String LOGIN_SP = "LoginSP";
	public static final String USER_INFO = "UserInfo";
	private EditText mPassWordETxt;// 密码输入框
	private EditText mAccountETxt;// 账户输入框
	private CheckBox showPassWordView;// 是否显示密码框
	private SharedPreferences sp;// 存储数据对象
	private Editor editor;// 编辑存储的对象
	private ProgressDialog pd;
	private static final int ALIWEB = 1;
	void initView() {
		pd=new ProgressDialog(LoginActivity.this);
		pd.setTitle(getString(R.string.login));
		pd.setMessage(getString(R.string.content_login));
		pd.setCanceledOnTouchOutside(false);
		mPassWordETxt = (EditText) findViewById(R.id.passWordEtxt);
		mAccountETxt = (EditText) findViewById(R.id.accountEtxt);
		(showPassWordView = (CheckBox) findViewById(R.id.showPassWord))
				.setOnCheckedChangeListener(changeListener);// 注册checkbox默认改变监听
		findViewById(R.id.loginBtn).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.toRegisterBtn).setOnClickListener(this);
		findViewById(R.id.findPassworld).setOnClickListener(this);
		findViewById(R.id.toRegisterBtn).setOnClickListener(this);
		findViewById(R.id.qqLogin).setOnClickListener(this);
		findViewById(R.id.sianLogin).setOnClickListener(this);
		findViewById(R.id.alipayLogin).setOnClickListener(this);
	}

	/**
	 * 登陆授权
	 */
	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		// 判断是否已授权如果授权，终止并跳转到消息2。否则进入授权页面
		if (plat.isValid()) {
			// System.out.println("已授权！");
			String userId = plat.getDb().getUserId();// 得到用户id
			if (userId != null) {// 判断id是否存在
				UIHandler.sendEmptyMessage(2, lcb);
				login(plat.getName(), userId, null);// 执行登陆
				return;
			}
		}
		plat.setPlatformActionListener(new LoginListener());
		// plat.SSOSetting(true);
		plat.showUser(null);
		pd.show();
	}

	/* 使用第三方信息登陆到应用 */
	private void login(String plat, String userId,
			HashMap<String, Object> userInfo) {
		if (plat.equals(QZone.NAME)) {
			plat = "qq";
			fastLogin(userId, plat);
		} else if (plat.equals(SinaWeibo.NAME)) {
			plat = "sina";
			fastLogin(userId, plat);
		} else {
			fastLogin(userId, plat);
		}
	}

	private void fastLogin(final String UserId, final String ChannelType) {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(LoginActivity.this)
						.FastLogin(UserId, ChannelType);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							//System.out.println("data==" + data);
							BaseApplication application = (BaseApplication) getApplication();
							User user = new User();
							user = User.JsonToSelf(data, "", "");
							application.user = user;
							// //返回数据标识是否登录成功
							Intent mIntent = new Intent();
							mIntent.putExtra(LOGIN_RESULT, true);
							setResult(RESULT_OK, mIntent);
							String userInfo = user.toString();
							SaveUserInfo(userInfo);
							finish();
						} else {
							ShowToast(obj.optString("ResponseMsg"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(pd!=null){
							pd.dismiss();
						}
					}
				}
			}
		});
	}
	
	private void SaveUserInfo(String userinfo){
		Util.saveFile(userinfo, this.getExternalFilesDir(null) + "/userinfo");
		Util.saveFile(userinfo, this.getFilesDir().getAbsolutePath() + "/userinfo");
	}
	
	
	/*********************************** 授权的监听和回调 *************************************************/
	LoginCallBack lcb = new LoginCallBack();

	/**
	 * 登陆的监听
	 */
	class LoginListener implements PlatformActionListener {

		@Override
		public void onCancel(Platform platform, int action) {
			if (action == Platform.ACTION_USER_INFOR) {
				UIHandler.sendEmptyMessage(0, lcb);
			}
		}

		// 登陆成功
		@Override
		public void onComplete(Platform platform, int action,
				HashMap<String, Object> res) {
			if (action == Platform.ACTION_USER_INFOR) {
				Message message = new Message();
				message.what = 1;// 登陆成功后提示消息1
				message.obj = res;
				UIHandler.sendMessage(message, lcb);				
				login(platform.getName(), platform.getDb().getUserId(), res);// 进入登陆页面				
			}
//			System.out.println(res);
//			System.out.println("getToken()="+platform.getDb().getToken());
//			System.out.println("UserId()="+platform.getDb().getUserId());
//			System.out.println("exportData="+platform.getDb().exportData());
			
		}

		@Override
		public void onError(Platform arg0, int action, Throwable t) {
			if (action == Platform.ACTION_USER_INFOR) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = t;
				UIHandler.sendMessage(msg, lcb);
			}
			t.printStackTrace();
		}

	}

	/**
	 * 登陆的回调
	 */
	class LoginCallBack implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				pd.dismiss();
				Toast.makeText(LoginActivity.this, getString(R.string.check_error),
						Toast.LENGTH_LONG).show();
				//System.out.println("share error：" + msg.obj);
				break;
			case 1:
				Toast.makeText(LoginActivity.this, getString(R.string.check_ok),
						Toast.LENGTH_SHORT).show();			
				break;
			case 2:
				Toast.makeText(LoginActivity.this, getString(R.string.have_user),
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				pd.dismiss();
				Toast.makeText(LoginActivity.this, getString(R.string.cancel_check),
						Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	public static final String LOGIN_RESULT = "LoginResult";

	/**
	 * 登录
	 */
	private void login(final String account, final String passWord) {
		if (account.isEmpty()) {
			Toast.makeText(this, R.string.user_null, Toast.LENGTH_SHORT).show();
			return;
		}
		if (passWord.isEmpty()) {
			Toast.makeText(this, R.string.password_null, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		pd.show();
		// 登陆
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(LoginActivity.this)
							.UserLogin(account, Util.getMD5(passWord));
				} catch (Exception e) {
					e.printStackTrace();
					return "";
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							//System.out.println(data);
							BaseApplication application = (BaseApplication) getApplication();
							User user = new User();
							user = User.JsonToSelf(data, account, passWord);
							application.user = user;
							// //返回数据标识是否登录成功
							Intent mIntent = new Intent();
							mIntent.putExtra(LOGIN_RESULT, true);
							setResult(RESULT_OK, mIntent);
							String userInfo = user.toString();
							SaveUserInfo(userInfo);
							finish();
						} else if (status == 1) {
							Toast.makeText(LoginActivity.this,
									R.string.login_lose, Toast.LENGTH_LONG)
									.show();
						} else if (status == 2) {
							Toast.makeText(LoginActivity.this,
									R.string.no_user, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}finally{
						if(pd!=null){
							pd.dismiss();
						}					
					}
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.loginBtn:
			String account = mAccountETxt.getText().toString();
			String password = mPassWordETxt.getText().toString();
			login(account, password);
			break;
		case R.id.findPassworld:
			LoginForget();
			Intent mIntentFP = new Intent();
			mIntentFP.setClass(this, FindPasswordActivity.class);
			startActivity(mIntentFP);
			break;
		case R.id.toRegisterBtn:
			LoginRegister();
			Intent mIntent = new Intent();
			mIntent.setClass(this, RegisterActivity.class);
			startActivity(mIntent);
			break;
		case R.id.qqLogin:
			LoginQQ();
			authorize(new QZone(this));
			break;
		case R.id.sianLogin:
			LoginWeibo();
			authorize(new SinaWeibo(this));
			break;
		case R.id.alipayLogin:// 支付宝登陆
			LoginAlipay();
			startActivityForResult(new Intent(this, AliWebLoginActivity.class).putExtra("URL", Constants.AlipayLoginUrl), ALIWEB);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == ALIWEB){
				pd.show();
				Bundle b = data.getExtras();
				String userid = b.getString("USERID");
				login("alipay", userid, null);
			}
		}
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			if (isChecked == true) {

				showPassWordView
						.setButtonDrawable(R.drawable.icon_view_checked);
				mPassWordETxt
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				mPassWordETxt.setSelection(mPassWordETxt.getText().length());
			} else {
				showPassWordView
						.setButtonDrawable(R.drawable.icon_view_unchecked);
				mPassWordETxt.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				mPassWordETxt.setSelection(mPassWordETxt.getText().length());
			}

		}
	};

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
