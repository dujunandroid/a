package com.boqii.petlifehouse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseLocationActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.MyTicket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.NetImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MyTicketDetailActivity extends BaseLocationActivity implements
		OnClickListener {

	private PopupWindow mPopupWindow;
	private ProgressBar progress;
	private ScrollView mainPage;
	private HttpManager mHttpManager;
	private boolean isLocationed = false;
	private MyTicket current_myticket;
	private Bitmap defaultImg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myticketdetail);
		defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
		initView();
	}

	private void initView() {
		mHttpManager = new HttpManager(this);
		progress = (ProgressBar) findViewById(R.id.progress);
		mainPage = (ScrollView) findViewById(R.id.main_page);
		mainPage.setVisibility(View.GONE);
		((ImageView) findViewById(R.id.back)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.ticket_layout:
			startActivity(new Intent(this, TicketDetailActivity.class).putExtra("TICKETID", current_myticket.ticket.TicketId));
			break;
		case R.id.merchant_layout:
			startActivity(new Intent(this, MerchantDetailActivity.class).putExtra("MERCHANTID", current_myticket.TicketMerchant.MerchantId));
			break;
		case R.id.phone:
			getPopupWindowInstance();
			mPopupWindow.showAtLocation(mainPage, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.call:
			Util.CallUp(this, current_myticket.TicketMerchant.MerchantTele);
			break;
		case R.id.cancel:
			mPopupWindow.dismiss();
			break;

		default:
			break;
		}
		
	}/*
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
		mPopupWindow = new PopupWindow(popupWindow, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		popupWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		((Button) popupWindow.findViewById(R.id.call)).setText(current_myticket.TicketMerchant.MerchantTele);
		((Button) popupWindow.findViewById(R.id.call)).setOnClickListener(this);
		((Button) popupWindow.findViewById(R.id.cancel))
				.setOnClickListener(this);

	}

	@Override
	public void Success(double lat, double lng) {
		super.Success(lat, lng);
		isLocationed = true;
		initData(lat, lng);
	}

	private void initData(final double lat, final double lng) {
		current_myticket = new MyTicket();
		current_myticket = (MyTicket) getIntent().getSerializableExtra("MYTICKET");
		current_myticket.TicketMerchant.MerchantLat = lat;
		current_myticket.TicketMerchant.MerchantLng = lng;
		mHttpManager.Excute(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				return NetworkService.getInstance(MyTicketDetailActivity.this)
						.GetMyTicketDetail(getApp().user.UserID,
								current_myticket.MyTicketId, lat, lng);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {							
							current_myticket = MyTicket.JsonToSelf(obj
									.optJSONObject("ResponseData"));
							initMyTicket(current_myticket);
							mainPage.setVisibility(View.VISIBLE);
							progress.setVisibility(View.GONE);
						}else
							ShowToast(obj.optString("ResponseMsg"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected void initMyTicket(MyTicket ticket) {
		if (ticket == null)
			return;
		((RelativeLayout)findViewById(R.id.ticket_layout)).setOnClickListener(this);
		((NetImageView) findViewById(R.id.image)).setImageUrl(
				Util.GetImageUrl(ticket.ticket.TicketImg,
						Util.dip2px(this, 100), Util.dip2px(this, 72)),
				Constants.PATH, defaultImg);
		((TextView) findViewById(R.id.mytickettitle))
				.setText(ticket.ticket.TicketTitle);
		((TextView) findViewById(R.id.voucher_no)).setText(ticket.MyVoucherNo);
		((TextView) findViewById(R.id.voucher_status))
				.setText(ticket.MyTicketStatus);
		((ImageView) findViewById(R.id.phone)).setOnClickListener(this);
		((TextView) findViewById(R.id.merchant_name))
				.setText(ticket.TicketMerchant.MerchantName);
		((TextView) findViewById(R.id.merchant_address))
				.setText(ticket.TicketMerchant.MerchantAddress);
		((TextView) findViewById(R.id.merchant_distance))
				.setText(Util.GetDistanceToKM(ticket.TicketMerchant.MerchantDistance));
		((RelativeLayout)findViewById(R.id.merchant_layout)).setOnClickListener(this);
		if (!isLocationed)
			((TextView) findViewById(R.id.merchant_distance))
					.setVisibility(View.INVISIBLE);
		RelativeLayout detail = (RelativeLayout) findViewById(R.id.detail);
		((ImageView) detail.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_detail);
		((TextView) detail.findViewById(R.id.title))
				.setText(getString(R.string.order_detail));
		if (!Util.isEmpty(ticket.ticket.MyTicketDetail))
			((TextView) detail.findViewById(R.id.description)).setText(Html
					.fromHtml(ticket.ticket.MyTicketDetail));
		else
			((TextView) detail.findViewById(R.id.description)).setText(getString(R.string.no_data));
		RelativeLayout remind = (RelativeLayout) findViewById(R.id.remind);
		((ImageView) remind.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_remind);
		((TextView) remind.findViewById(R.id.title))
				.setText(getString(R.string.remind));
		if (!Util.isEmpty(ticket.ticket.MyTicketRemind))
			((TextView) remind.findViewById(R.id.description)).setText(Html
					.fromHtml(ticket.ticket.MyTicketRemind));
		else
			((TextView) remind.findViewById(R.id.description)).setText(getString(R.string.no_data));
	}

	@Override
	public void Fail() {
		super.Fail();
		isLocationed = false;
		initData(0.0, 0.0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
