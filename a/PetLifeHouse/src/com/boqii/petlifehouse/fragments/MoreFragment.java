package com.boqii.petlifehouse.fragments;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.utilities.Config;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MoreFragment extends BaseFragment implements OnClickListener, Callback, OnCheckedChangeListener {

	private PopupWindow mPopupWindow;
	private ScrollView mainPage;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		view = inflater.inflate(R.layout.more, container, false);
		init();
		return view;
	}

	private PackageInfo getPackageInfo() throws Exception {
		PackageManager packageManager = getActivity().getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		return packInfo;
	}

	private void init() {
		mainPage = (ScrollView) view.findViewById(R.id.main_page);
		((CheckBox) view.findViewById(R.id.wificheck)).setOnCheckedChangeListener(this);
		((CheckBox) view.findViewById(R.id.msgcheck)).setOnCheckedChangeListener(this);
		try {
			((TextView) view.findViewById(R.id.version)).setText(getString(R.string.version) + getPackageInfo().versionName);
			((TextView) view.findViewById(R.id.version_code)).setText("v" + getPackageInfo().versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		((LinearLayout) view.findViewById(R.id.version_update)).setOnClickListener(this);
		((LinearLayout) view.findViewById(R.id.market)).setOnClickListener(this);
		((LinearLayout) view.findViewById(R.id.share)).setOnClickListener(this);
		((LinearLayout) view.findViewById(R.id.call_layout)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.version_update:
			GetNewestVersion();
			break;
		case R.id.market:
			try {
				Intent market = new Intent(Intent.ACTION_VIEW);
				market.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
				startActivity(market);
			} catch (Exception e) {
				Toast.makeText(getActivity(), getString(R.string.market_install), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.call_layout:
			getPopupWindowInstance();
			mPopupWindow.showAtLocation(mainPage, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.share:
			Util.share(getActivity(), new OneKeyShareCallback());
			break;
		case R.id.call:
			Util.CallUp(getActivity(), "400-820-6098");
			break;
		case R.id.cancel:
			mPopupWindow.dismiss();
			break;
		default:
			break;
		}

	}

	/*
	 * 获取PopupWindow实例
	 */
	private void getPopupWindowInstance() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	/*
	 * 创建PopupWindow
	 */
	private void initPopuptWindow() {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View popupWindow = layoutInflater.inflate(R.layout.call_phone, null);

		// 创建一个PopupWindow
		// 参数1：contentView 指定PopupWindow的内容
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		popupWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		((Button) popupWindow.findViewById(R.id.call)).setText("400-820-6098");
		((Button) popupWindow.findViewById(R.id.call)).setOnClickListener(this);
		((Button) popupWindow.findViewById(R.id.cancel)).setOnClickListener(this);

	}

	/**
	 * 显示强制升级dialog
	 */
	public void showHardDialog(String msg, final String downloadUrl) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_hard, null);
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		dialog.setContentView(v);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		lp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		((TextView)v.findViewById(R.id.message)).setText(msg);
		((TextView)v.findViewById(R.id.update)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				downApk(downloadUrl);
			}
		});
	}

	/**
	 * 显示可选升级dialog
	 */
	public void showSoftDialog(String msg, final String downloadUrl) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_soft, null);
		((TextView)v.findViewById(R.id.message)).setText(msg);
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		dialog.setContentView(v);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		lp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		// Builder builder = new AlertDialog.Builder(getActivity());
		// builder.setView(v);
		// final AlertDialog dialog = builder.create();
		dialog.show();
		((TextView) v.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		((TextView) v.findViewById(R.id.sure)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downApk(downloadUrl);
			}
		});
	}

	protected void downApk(String downloadUrl) {
		if (Util.isEmpty(downloadUrl))
			return;
		String uri = new String(downloadUrl);
		Uri u = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(u);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void GetNewestVersion() {

		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage(getString(R.string.get_newest_version));
		progress.show();
		new HttpManager(getActivity()).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(getActivity()).GetLastAndroidVersion(getPackageInfo().versionCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				progress.dismiss();
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							int VersionStatus = data.optInt("VersionStatus");
							switch (VersionStatus) {
							case 1:
								showHardDialog(data.optString("UpdateMsg"), "http://www.boqii.com/resource/mobile/boqii.apk");
								break;
							case 2:
								showSoftDialog(data.optString("UpdateMsg"), "http://www.boqii.com/resource/mobile/boqii.apk");
								break;
							case 3:
								((TextView) view.findViewById(R.id.version)).setText(getString(R.string.already_newest));
								Toast.makeText(getActivity(), getString(R.string.already_newest), Toast.LENGTH_SHORT).show();
								break;

							default:
								break;
							}
						} else {
							Toast.makeText(getActivity(), obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

		});
	}

	/*********************************** 分享的监听和回调 ************************************/

	/**
	 * 快捷分享的监听
	 */
	class OneKeyShareCallback implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(0, MoreFragment.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(1, MoreFragment.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.what = -1;
			msg.obj = arg2;
			UIHandler.sendMessage(msg, MoreFragment.this);
		}

	}

	/**
	 * 快捷分享的方回调
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case -1:
			Toast.makeText(getActivity(), getString(R.string.share_error), Toast.LENGTH_LONG).show();
			break;
		case 1:
			//Toast.makeText(getActivity(), getString(R.string.share_suc), Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(getActivity(), getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		ShareSDK.stopSDK(getActivity());
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.wificheck:
			Config.IsCanDownloadPicWhenNoWifi = !isChecked;
			break;
		case R.id.msgcheck:
			if (!isChecked)
				JPushInterface.stopPush(getActivity().getApplicationContext());
			else
				JPushInterface.resumePush(getActivity().getApplicationContext());
			break;

		default:
			break;
		}
	}
}
