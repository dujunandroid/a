package com.boqii.petlifehouse.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Merchant;
import com.boqii.petlifehouse.fragments.NearFragment;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.LocationManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.utilities.LocationManager.MyLocationListener;
import com.boqii.petlifehouse.widgets.NetImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MerchantDetailActivity extends BaseActivity implements OnClickListener,
			MyLocationListener ,Callback{

	private Merchant current_merchant;
	private PopupWindow mPopupWindow;
	private ProgressBar progress;
	private ScrollView	mainPage;
	private LocationManager mLocationManager;
	private boolean isLocationed = false;
	private Bitmap defaultImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_detail);
		defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
		initView();
		initLocation();
	}

	private void initLocation() {
		mLocationManager = new LocationManager(this, this);
		mLocationManager.StartLocation();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.StopLocation();// 停止定位
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
        LayoutInflater layoutInflater = LayoutInflater.from(this); 
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
        ((Button)popupWindow.findViewById(R.id.call)).setText(current_merchant.MerchantTele);
        ((Button)popupWindow.findViewById(R.id.call)).setOnClickListener(this);
        ((Button)popupWindow.findViewById(R.id.cancel)).setOnClickListener(this);
        
    }

	private void initView() {
		progress = (ProgressBar)findViewById(R.id.progress);
		mainPage = (ScrollView)findViewById(R.id.main_page);
		mainPage.setVisibility(View.GONE);
		((ImageView)findViewById(R.id.back)).setOnClickListener(this);
		((ImageView)findViewById(R.id.share)).setOnClickListener(this);
	}

	private void initData(double lat, double lng) {
		current_merchant = new Merchant();
		current_merchant.MerchantId = getIntent().getIntExtra("MERCHANTID", 0);
		current_merchant.MerchantLat = lat;
		current_merchant.MerchantLng = lng;
		final String url = getIntent().getStringExtra("URL");
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				// FIXME
				if(!Util.isEmpty(url)){
					try {
						return NetworkService.getInstance(MerchantDetailActivity.this).HttpConnect(new URL(url));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				//TODO replace the location lat and lng
//				return getString(R.string.data_ticket_detail);
				return NetworkService.getInstance(MerchantDetailActivity.this).GetMerchantDetail(current_merchant.MerchantId, current_merchant.MerchantLat, current_merchant.MerchantLng);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							current_merchant = Merchant.JsonToSelf(obj.optJSONObject("ResponseData"));
							initMerchantDetail(current_merchant);
							mainPage.setVisibility(View.VISIBLE);
							progress.setVisibility(View.GONE);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected void initMerchantDetail(Merchant merchant) {
		((TextView)findViewById(R.id.merchant_name)).setText(merchant.MerchantName);
		((NetImageView)findViewById(R.id.image)).setImageUrl(Util.GetImageUrl(merchant.MerchantImg, Util.dip2px(this, 100), Util.dip2px(this, 72)), Constants.PATH, defaultImg);
		((TextView)findViewById(R.id.businessarea)).setText(merchant.BusinessArea);
		if(!isLocationed)
		{
			((TextView)findViewById(R.id.distance)).setVisibility(View.INVISIBLE);
		}else{
			float distance=merchant.MerchantDistance;
			String format=Util.GetDistanceToKM(distance);
			((TextView)findViewById(R.id.distance)).setText(format);
			if(Util.isEmpty(format))
				((TextView)findViewById(R.id.distance)).setVisibility(View.INVISIBLE);
		}
		((TextView)findViewById(R.id.price)).setText(getString(R.string.perpersion, merchant.ConsumePerPerson));
		((TextView)findViewById(R.id.scan_number)).setText(getString(R.string.scan, merchant.ScanNumber));
		LinearLayout address = (LinearLayout)findViewById(R.id.address);
		address.setOnClickListener(this);
		((ImageView)address.findViewById(R.id.icon)).setImageResource(R.drawable.ic_address);
		((TextView)address.findViewById(R.id.title)).setText(merchant.MerchantAddress);
		LinearLayout phone = (LinearLayout)findViewById(R.id.telephone);
		phone.setOnClickListener(this);
		((ImageView)phone.findViewById(R.id.icon)).setImageResource(R.drawable.ic_telephone);
		((TextView)phone.findViewById(R.id.title)).setText(merchant.MerchantTele);
		if(merchant.TicketList != null && merchant.TicketList.size() > 0){
			LayoutInflater mLayoutInflater = LayoutInflater.from(this);
			LinearLayout ticketList = (LinearLayout)findViewById(R.id.ticket_list);
			ticketList.setVisibility(View.VISIBLE);
			for (int i = 0; i < merchant.TicketList.size(); i++) {
				mLayoutInflater.inflate(R.layout.layout_ticket_short_item, ticketList);
				LinearLayout ticketLayout = (LinearLayout) ticketList.getChildAt(ticketList.getChildCount() - 1);
				((ImageView)ticketLayout.findViewById(R.id.icon)).setImageResource(R.drawable.ticket_icon);
				((TextView)ticketLayout.findViewById(R.id.price)).setText(getString(R.string.price, merchant.TicketList.get(i).TicketPrice));
				((TextView)ticketLayout.findViewById(R.id.title)).setText(merchant.TicketList.get(i).TicketTitle);
				LayoutParams params = (LayoutParams) ticketLayout.getLayoutParams();
				params.topMargin = Util.dip2px(this, 5);
				ticketLayout.setLayoutParams(params);
				final int ticketId = merchant.TicketList.get(i).TicketId;
				ticketLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MerchantDetailTicketDetail();
						startActivity(new Intent(MerchantDetailActivity.this, TicketDetailActivity.class).putExtra("TICKETID", ticketId));
					}
				});
			}
		}
		RelativeLayout merchant_desc = (RelativeLayout)findViewById(R.id.merchant_desc);
		((ImageView)merchant_desc.findViewById(R.id.icon)).setImageResource(R.drawable.ic_desc);
		((TextView)merchant_desc.findViewById(R.id.title)).setText(getString(R.string.merchant_intro));
		if(!Util.isEmpty(current_merchant.MerchantDesc))
			((TextView)merchant_desc.findViewById(R.id.description)).setText(Html.fromHtml(current_merchant.MerchantDesc));
		else
			((TextView)merchant_desc.findViewById(R.id.description)).setText(getString(R.string.no_data));
		RelativeLayout merchant_traffic = (RelativeLayout)findViewById(R.id.merchant_traffic);
		((ImageView)merchant_traffic.findViewById(R.id.icon)).setImageResource(R.drawable.ic_traffic);
		((TextView)merchant_traffic.findViewById(R.id.title)).setText(getString(R.string.traffic));
		if(!Util.isEmpty(current_merchant.MerchantTraffic))
			((TextView)merchant_traffic.findViewById(R.id.description)).setText(Html.fromHtml(current_merchant.MerchantTraffic));
		else
			((TextView)merchant_traffic.findViewById(R.id.description)).setText(getString(R.string.no_data));
		RelativeLayout merchant_near = (RelativeLayout)findViewById(R.id.merchant_near);
		((ImageView)merchant_near.findViewById(R.id.icon)).setImageResource(R.drawable.ic_area);
		((TextView)merchant_near.findViewById(R.id.title)).setText(getString(R.string.area_near));
		if(!Util.isEmpty(current_merchant.MerchantNear))
			((TextView)merchant_near.findViewById(R.id.description)).setText(Html.fromHtml(current_merchant.MerchantNear));
		else
			((TextView)merchant_near.findViewById(R.id.description)).setText(getString(R.string.no_data));
		LinearLayout envir = (LinearLayout)findViewById(R.id.envir);
		((ImageView)envir.findViewById(R.id.icon)).setImageResource(R.drawable.ic_envir);
		((TextView)envir.findViewById(R.id.title)).setText(getString(R.string.envir));
		envir.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.share:
			Util.share(this,new OneKeyShareCallback());
			break;
		case R.id.address:
			MerchantDetailAddress();
			ArrayList<Merchant> list = new ArrayList<Merchant>();
			list.add(current_merchant);
			startActivity(new Intent(this, MapPatternActivity.class).putExtra(NearFragment.RESULT_CODE, list));
			break;
		case R.id.telephone:
			if(Util.isEmpty(current_merchant.MerchantTele))
				return;
			MerchantDetailTel();
			getPopupWindowInstance();
			mPopupWindow.showAtLocation(mainPage, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.envir:
			MerchantDetailEnviroment();
			startActivity(new Intent(this, WebViewActivity.class).putExtra("URL", current_merchant.MerchantServeUrl).putExtra("TITLE", current_merchant.MerchantName));
			break;
		case R.id.call:
			Util.CallUp(this, current_merchant.MerchantTele);
			break;
		case R.id.cancel:
			mPopupWindow.dismiss();
			break;

		default:
			break;
		}
	}

	@Override
	public void Success(double lat, double lng) {
		isLocationed = true;
		mLocationManager.StopLocation();
		initData(lat, lng);
	}

	@Override
	public void Fail() {
		isLocationed = false;
		ShowToast(getString(R.string.location_fail));
		initData(0.0, 0.0);
	}
	
	/**
	 * 快捷分享的监听
	 */
	class OneKeyShareCallback implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(0, MerchantDetailActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(1, MerchantDetailActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.what = -1;
			msg.obj = arg2;
			UIHandler.sendMessage(msg, MerchantDetailActivity.this);
		}

	}

	/**
	 * 快捷分享的方回调
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case -1:
			Toast.makeText(MerchantDetailActivity.this, getString(R.string.share_error), Toast.LENGTH_LONG).show();
			//System.out.println("分享失败，原因：" + msg.obj);
			break;
		case 1:
			Toast.makeText(this, getString(R.string.share_suc), Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(MerchantDetailActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}
	
}
