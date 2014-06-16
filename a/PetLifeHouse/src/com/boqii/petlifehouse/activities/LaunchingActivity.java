package com.boqii.petlifehouse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.User;
import com.boqii.petlifehouse.utilities.CategoryAreaData;
import com.boqii.petlifehouse.utilities.CategoryData;
import com.boqii.petlifehouse.utilities.CategoryMerchantData;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class LaunchingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launching);
		BaseApplication app = (BaseApplication) getApplication();
		app.user = User.StringToSelf(Util.loadFile(this.getExternalFilesDir(null) + "/userinfo"));
		if(Util.isEmpty(app.user.UserID))
			app.user = User.StringToSelf(Util.loadFile(this.getFilesDir().getAbsolutePath() + "/userinfo"));
		mHandler.sendEmptyMessageDelayed(0, 2000);
		Util.initImagePath(this);
	}

	private PackageInfo getPackageInfo() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		return packInfo;
	}

	private void GetNewestVersion() {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(LaunchingActivity.this).GetLastAndroidVersion(getPackageInfo().versionCode);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							int VersionStatus = data.optInt("VersionStatus");
							switch (VersionStatus) {
							case 1:
								if(!isFinishing())
									showHardDialog(data.optString("UpdateMsg"), "http://www.boqii.com/resource/mobile/boqii.apk");
								break;
							case 2:
								if(!isFinishing())
									showSoftDialog(data.optString("UpdateMsg"), "http://www.boqii.com/resource/mobile/boqii.apk");
								break;
							case 3:
								startActivity(new Intent(LaunchingActivity.this, MainActivity.class));
								finish();
								break;

							default:
								break;
							}
						}else{
							Toast.makeText(LaunchingActivity.this, obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				
				}
			}
			
		});
	}
	
	/**
	 * 显示强制升级dialog
	 */
	public void showHardDialog(String msg, final String downloadUrl) {
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_hard, null);
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
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
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_soft, null);
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		dialog.setContentView(v);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.8);
		lp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
		((TextView)v.findViewById(R.id.message)).setText(msg);
		((TextView)v.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				startActivity(new Intent(LaunchingActivity.this, MainActivity.class));
				finish();
			}
		});
		((TextView)v.findViewById(R.id.sure)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				downApk(downloadUrl);
			}
		});
	}

	protected void downApk(String downloadUrl) {
		if(Util.isEmpty(downloadUrl))
			return;
		String uri = new String(downloadUrl);
		Uri u = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(u);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			CategoryAreaData.getinstance(LaunchingActivity.this).execute();
			CategoryData.getinstance(LaunchingActivity.this).execute();
			CategoryMerchantData.getinstance(LaunchingActivity.this).execute();
			GetNewestVersion();
			UploadChannel(getMetaDataValue("UMENG_CHANNEL", "BOQII"));
		}
		
	};
	
	private String getMetaDataValue(String name, String def) {
        String value = getMetaDataValue(name);
        return (value == null) ? def : value;
    }

    private String getMetaDataValue(String name) {
        Object value = null;
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 128);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException(
                    "Could not read the name in the manifest file.", e);
        }
        if (value == null) {
            throw new RuntimeException("The name '" + name
                    + "' is not defined in the manifest file's meta data.");
        }
        return value.toString();
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		startActivity(new Intent(LaunchingActivity.this, MainActivity.class));
//		finish();
	}

	protected void UploadChannel(final String string) {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(LaunchingActivity.this).UploadChannel(string);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
						}else{
							Toast.makeText(LaunchingActivity.this, obj.optString("ResponseMsg"), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				
				}
			}
			
		});
	}
	
}
