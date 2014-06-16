package com.boqii.petlifehouse.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.utilities.Util;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareWeiboActivity extends BaseActivity implements TextWatcher, OnCheckedChangeListener, Callback, OnClickListener {

	private EditText mETxt;// 输入编辑框
	private TextView mTotalTxt;// 字数统计文本
	private int num = 140;// 字数限制
	private ArrayList<CheckBox> cbLst;// 所有checkbox分享平台
	public static String[] allName = new String[] { SinaWeibo.NAME, TencentWeibo.NAME, QZone.NAME };// checkbox的分享平台对应的名字NetEaseMicroBlog.NAME
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_weibo_layout);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}

	private void init() {
		mETxt = (EditText) findViewById(R.id.shareETxt);
		mTotalTxt = (TextView) findViewById(R.id.totalNumber);

		cbLst = new ArrayList<CheckBox>();
		cbLst.add((CheckBox) findViewById(R.id.sinaCB));
		cbLst.add((CheckBox) findViewById(R.id.tencentCB));
		cbLst.add((CheckBox) findViewById(R.id.qZoneCB));
		findViewById(R.id.backSW).setOnClickListener(this);
		// cbLst.add((CheckBox) findViewById(R.id.netEaseMicroCB));
		// 注册监听
		for (int i = 0; i < cbLst.size(); i++) {
			CheckBox cb = cbLst.get(i);
			cb.setOnCheckedChangeListener(this);
			cb.setTag(allName[i]);// 设置tag为分享平台的名字
			cb.getBackground().setColorFilter(Color.GRAY, Mode.MULTIPLY);
		}

		// 减掉设置的文本
		mTotalTxt.setText("还可输入" + (num - mETxt.getEditableText().length()) + "个字");
		// 添加编辑文本状态监听
		mETxt.addTextChangedListener(this);
	}

	public void onAction(View v) {
		String content = mETxt.getText().toString();
		switch (v.getId()) {
		case R.id.shareBtn:
			for (int i = 0; i < cbLst.size(); i++) {// 判断并分享到选中的平台
				if (cbLst.get(i).isChecked()) {
					ShareParams sp = null;
					switch (i) {
					case 0:
						sp = new ShareParams();
						sp.setTitle(getString(R.string.app_name));
						sp.setText(content);
						sp.setImagePath(Util.SHARE_IMG);
						sp.setTitleUrl("www.boqii.com");
						break;
					case 1:
						sp = new ShareParams();
						sp.setTitle(getString(R.string.app_name));
						sp.setTitleUrl("www.boqii.com");
						sp.setText(content);
						sp.setImagePath(Util.SHARE_IMG);
						break;
					case 2:
						sp = new ShareParams();
						sp.setTitle(getString(R.string.app_name));
						sp.setTitleUrl("www.boqii.com");
						sp.setText(content);
						sp.setImagePath(Util.SHARE_IMG);
						sp.setSite("波奇宠物App");
						sp.setSiteUrl("http://www.boqii.com");
						// sp.setComment("波奇宠物App评论");
						break;
					// case 3:
					// sp = new ShareParams();
					// sp.setText("测试分享的文本");
					// // sp.setImagePath(ShareActivity.TEST_IMAGE);
					// break;
					}
					if (sp != null) {
						Platform weibo = ShareSDK.getPlatform(this, cbLst.get(i).getTag() + "");
						weibo.setPlatformActionListener(new shareListener()); // 设置分享事件回调
						// 执行分享
						weibo.share(sp);
					}
				}
			}
			break;
		}
	}

	/**
	 * 判读并授权；
	 * 
	 * @param name
	 * @return
	 */
	private void isValid(CheckBox box) {
		String name = box.getTag().toString();// 得到存储的名字
		boolean b = false;// 用于判读是否授权
		Platform p = null;// 数据实体
		if (name.equals(allName[1])) {
			p = new TencentWeibo(this);
			b = p.isValid();
		} else if (name.equals(allName[2])) {
			p = new QZone(this);
			b = p.isValid();
			// } else if (name.equals(allName[3])) {
			// p = new NetEaseMicroBlog(this);
			// b = p.isValid();
		} else {
			p = new SinaWeibo(this);
			b = p.isValid();
		}
		if (!b) {// 如果没有授权则授权授权
			p.authorize();
			p.setPlatformActionListener(new authorizeListener());
		}
	}

	/********************************** 文本的监听 ***********************/
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;

	@Override
	public void afterTextChanged(Editable s) {
		int number = num - s.length();// 得到还可以输入的字数
		mTotalTxt.setText("还可输入" + number + "个字");
		selectionStart = mETxt.getSelectionStart();
		selectionEnd = mETxt.getSelectionEnd();
		if (temp.length() > num) {
			s.delete(selectionStart - 1, selectionEnd);
			int tempSelection = selectionEnd;
			mETxt.setText(s);
			mETxt.setSelection(tempSelection);// 设置光标在最后
		} else {
			mETxt.setSelection(selectionEnd);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;// 将改变了的文本赋给temp
	}

	/************************* 复选框选择改变事件 **************************/

	public CheckBox lastCheckBox;// 记录选择的复选框

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Drawable drawable = buttonView.getBackground();
		if (isChecked) {
			drawable.clearColorFilter();
			buttonView.setBackgroundDrawable(drawable);
			lastCheckBox = (CheckBox) buttonView;
			// 判断是否授权，如果没有授权则授权，否则什么也不做
			isValid((CheckBox) buttonView);

		} else {
			drawable.setColorFilter(Color.GRAY, Mode.MULTIPLY);
			buttonView.setBackgroundDrawable(drawable);
		}

	}

	/***************************** 回调 ******************************************/
	/**
	 * 分享的回调
	 * 
	 * @author Administrator
	 * 
	 */
	class shareListener implements PlatformActionListener {
		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(SHARE_CANCEL, ShareWeiboActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(SHARE_OK, ShareWeiboActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.obj = arg2;
			msg.what = SHARE_ERROR;
			UIHandler.sendMessage(msg, ShareWeiboActivity.this);
		}
	}

	/**
	 * 授权的回调
	 * 
	 * @author Administrator
	 * 
	 */
	class authorizeListener implements PlatformActionListener {
		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(AUTHOEIZE_CANCEL, ShareWeiboActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			Message msg = new Message();
			msg.obj = arg0.getName();
			;
			msg.what = AUTHOEIZE_OK;
			UIHandler.sendMessage(msg, ShareWeiboActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.obj = arg2;
			msg.what = AUTHOEIZE_CANCEL;
			UIHandler.sendMessage(msg, ShareWeiboActivity.this);
		}
	}

	int toatal = 0;

	/******************************* 回调的消息接受 ********************************************/
	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case SHARE_ERROR:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.share_error), Toast.LENGTH_SHORT).show();
			// System.out.println("share error：" + msg.obj);
			break;
		case SHARE_CANCEL:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
			break;
		case SHARE_OK:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.share_suc), Toast.LENGTH_SHORT).show();
			break;
		case AUTHOEIZE_ERROR:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.shouquan_error), Toast.LENGTH_SHORT).show();
			// System.out.println("share error：" + msg.obj);
			lastCheckBox.setChecked(false);
			break;
		case AUTHOEIZE_OK:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.shouquan_suc), Toast.LENGTH_SHORT).show();
			break;
		case AUTHOEIZE_CANCEL:
			Toast.makeText(ShareWeiboActivity.this, getString(R.string.shouquan_cancel), Toast.LENGTH_SHORT).show();
			lastCheckBox.setChecked(false);
			break;
		}

		return false;
	}

	public static final int SHARE_ERROR = -1;// 分享失败
	public static final int SHARE_OK = 1;// 分享成功
	public static final int SHARE_CANCEL = 0;// 分享取消
	public static final int AUTHOEIZE_ERROR = -11;// 授权失败
	public static final int AUTHOEIZE_OK = 11;// 授权成功
	public static final int AUTHOEIZE_CANCEL = 10;// 授权取消

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backSW:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}
}
