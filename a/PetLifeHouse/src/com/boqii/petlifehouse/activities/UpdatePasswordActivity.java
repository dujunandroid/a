package com.boqii.petlifehouse.activities;

import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class UpdatePasswordActivity extends BaseActivity implements
		OnClickListener {

	private EditText mPassWordEditText;
	private Intent mIntent;
	private CheckBox mSPWCBox;
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_password);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initView();
	}

	void initView() {
		mPassWordEditText = (EditText) findViewById(R.id.passWordUPEtxt);
		findViewById(R.id.backUP).setOnClickListener(this);
		findViewById(R.id.okUPBtn).setOnClickListener(this);
		(mSPWCBox = (CheckBox) findViewById(R.id.showPassWordUP))
				.setOnCheckedChangeListener(changeListener);
		mIntent = getIntent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okUPBtn:
			String passWord = mPassWordEditText.getText().toString().trim();
			String authCode = mIntent
					.getStringExtra(FindPasswordActivity.CODE_TAG);
			String phoneNumber = mIntent
					.getStringExtra(FindPasswordActivity.PHONENUMBER_TAG);
			if (!Util.isEmpty(authCode) && !Util.isEmail(phoneNumber)) {
				updatePassWord(phoneNumber, authCode, passWord);
			} else {
				ShowToast(getString(R.string.illegal_operation));
			}
			break;
		case R.id.backUP:
			finish();
			break;
		}
	}

	private void updatePassWord(final String phoneNumber,
			final String authCode, final String passWord) {
		if (Util.isEmpty(passWord)) {
			ShowToast(getString(R.string.password_null));
			return;
		}
		if (passWord.length() < 6) {
			ShowToast(getString(R.string.password_6));
			return;
		}

		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				//System.out.println(phoneNumber + "-----" + authCode + "===");

				try {
					return NetworkService.getInstance(
							UpdatePasswordActivity.this).ModifyPassword(
							phoneNumber, authCode, Util.getMD5(passWord));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
//				System.out.println(phoneNumber + "=phone,code=" + authCode
//						+ ",password=" + passWord);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						int status = obj.optInt("ResponseStatus", -1);
						if (status == Constants.RESPONSE_OK) {// 如果验证成功跳转到修改密码
							// ShowToast(getResources().getString(
							// R.string.update_password_ok));
							ShowToast(obj.optString("ResponseMsg"));
							
							Intent mIntent = new Intent();
							mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							mIntent.setClass(UpdatePasswordActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
							finish();
						} else {
							ShowToast(obj.optString("ResponseMsg"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked == true) {
				mSPWCBox.setButtonDrawable(R.drawable.icon_view_checked);
				mPassWordEditText
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				mPassWordEditText.setSelection(mPassWordEditText.getText().length());
			} else {
				mSPWCBox.setButtonDrawable(R.drawable.icon_view_unchecked);
				mPassWordEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				mPassWordEditText.setSelection(mPassWordEditText.getText().length());
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
