package com.boqii.petlifehouse.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.adapter.MyImageAdapter;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseactivities.BaseLocationActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.AdGallery;
import com.boqii.petlifehouse.widgets.CustomScrollView;
import com.boqii.petlifehouse.widgets.CustomScrollView.OnScrollListener;
import com.boqii.petlifehouse.widgets.LineTextView;

public class TicketDetailActivity extends BaseLocationActivity implements OnClickListener, OnCheckedChangeListener, Callback, OnScrollListener {

	private AdGallery gallery;// 轮播图片
	private ArrayList<String> imageList = new ArrayList<String>();// 轮播图片数据集合
	private MyImageAdapter mImageAdapter;
	private LinearLayout dotLayout;// 轮播图片上的记录点
	private Ticket current_ticket;
	private PopupWindow mPopupWindow;
	private ProgressBar progress;
	private CustomScrollView mainPage;
	private boolean isLocationed = false;
	private View mViewTop;// 浮动层
	private View mViewMiddle;// 正常位置的层
	private HttpManager mHttpManager;
	private final int REQUEST_BUY_CODE = 1;
	private final int REQUEST_COLLECT = 2;
	

	private BaseApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_detail);
		app = (BaseApplication) getApplication();
		initView();
	}

	private void initData(double lat, double lng) {
		current_ticket = new Ticket();
		current_ticket.TicketId = getIntent().getIntExtra("TICKETID", 0);
		current_ticket.TicketMerchant.MerchantLat = lat;
		current_ticket.TicketMerchant.MerchantLng = lng;
		final String url = getIntent().getStringExtra("URL");
		mHttpManager.Excute(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				if (!Util.isEmpty(url)) {
					try {
						return NetworkService.getInstance(TicketDetailActivity.this).HttpConnect(new URL(url));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				// return getString(R.string.data_ticket_detail);
				return NetworkService.getInstance(TicketDetailActivity.this).GetTicketDetail(app.user.UserID, current_ticket.TicketId, current_ticket.TicketMerchant.MerchantLat,
						current_ticket.TicketMerchant.MerchantLng);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							current_ticket = Ticket.JsonToSelf(obj.optJSONObject("ResponseData"));
							initGalleryList(current_ticket.ImageList);
							initTicketDetail(current_ticket);
							mainPage.setVisibility(View.VISIBLE);
							progress.setVisibility(View.GONE);
						} else
							ShowToast(obj.optString("ResponseMsg"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected void initTicketDetail(Ticket ticket) {
		if (ticket == null)
			return;
		((CheckBox) findViewById(R.id.collect)).setChecked(ticket.IsCollected != 0);
		((CheckBox) findViewById(R.id.collect)).setOnCheckedChangeListener(this);
		// 正常的层赋值
		RelativeLayout ticket_info = (RelativeLayout) findViewById(R.id.ticket_info);
		((TextView) ticket_info.findViewById(R.id.price)).setText(getString(R.string.price, ticket.TicketPrice));
		((LineTextView) ticket_info.findViewById(R.id.oriPrice)).setText(getString(R.string.price, ticket.TicketOriPrice));
		((TextView) ticket_info.findViewById(R.id.sale)).setText(getString(R.string.sale, ticket.TicketSale));
		((TextView) ticket_info.findViewById(R.id.limit)).setText(ticket.TicketLimit);
		ImageView buy = (ImageView) ticket_info.findViewById(R.id.buy_btn);
		buy.setOnClickListener(this);
		if (ticket.TicketRemain > 0) {
			buy.setBackgroundResource(R.drawable.ic_buy);
			((TextView) findViewById(R.id.time)).setText(getString(R.string.remaintime, Util.TimeLeft(this, ticket.TicketRemain)));
		} else {
			buy.setBackgroundResource(R.drawable.ic_cannot_buy);
			((TextView) findViewById(R.id.time)).setVisibility(View.GONE);
		}
		// 悬浮层赋值
		RelativeLayout to_ticket_info =(RelativeLayout) findViewById(R.id.top_ticket_info);
		((TextView) to_ticket_info.findViewById(R.id.price)).setText(getString(R.string.price, ticket.TicketPrice));
		((LineTextView) to_ticket_info.findViewById(R.id.oriPrice)).setText(getString(R.string.price, ticket.TicketOriPrice));
		((TextView) to_ticket_info.findViewById(R.id.sale)).setText(getString(R.string.sale, ticket.TicketSale));
		((TextView) to_ticket_info.findViewById(R.id.limit)).setText(ticket.TicketLimit);
		ImageView top_buy = (ImageView) to_ticket_info.findViewById(R.id.buy_btn);
		top_buy.setOnClickListener(this);
		if (ticket.TicketRemain > 0) {
			top_buy.setBackgroundResource(R.drawable.ic_buy);
			((TextView) findViewById(R.id.time)).setText(getString(R.string.remaintime, Util.TimeLeft(this, ticket.TicketRemain)));
		} else {
			top_buy.setBackgroundResource(R.drawable.ic_cannot_buy);
			((TextView) findViewById(R.id.time)).setVisibility(View.GONE);
		}

		((TextView) findViewById(R.id.title)).setText(ticket.TicketTitle);
		((TextView) findViewById(R.id.desc)).setText(ticket.TicketDesc);
		((TextView) findViewById(R.id.buyed)).setText(getString(R.string.buyed, ticket.TicketBuyed));
		((ImageView) findViewById(R.id.phone)).setOnClickListener(this);
		((TextView) findViewById(R.id.merchant_name)).setText(ticket.TicketMerchant.MerchantName);
		((TextView) findViewById(R.id.merchant_address)).setText(ticket.TicketMerchant.MerchantAddress);
		float distance = ticket.TicketMerchant.MerchantDistance;
		String format = Util.GetDistanceToKM(distance);
		((TextView) findViewById(R.id.merchant_distance)).setText(format);
		if (!isLocationed)
			((TextView) findViewById(R.id.merchant_distance)).setVisibility(View.INVISIBLE);
		RelativeLayout detail = (RelativeLayout) findViewById(R.id.detail);
		((ImageView) detail.findViewById(R.id.icon)).setImageResource(R.drawable.ic_detail);
		((TextView) detail.findViewById(R.id.title)).setText(getString(R.string.order_detail));
		if (!Util.isEmpty(ticket.TicketDetail))
			((TextView) detail.findViewById(R.id.description)).setText(Html.fromHtml(ticket.TicketDetail));
		else
			((TextView) detail.findViewById(R.id.description)).setText(getString(R.string.no_data));

		RelativeLayout remind = (RelativeLayout) findViewById(R.id.remind);
		((ImageView) remind.findViewById(R.id.icon)).setImageResource(R.drawable.ic_remind);
		((TextView) remind.findViewById(R.id.title)).setText(getString(R.string.remind));
		if (!Util.isEmpty(ticket.TicketRemind))
			((TextView) remind.findViewById(R.id.description)).setText(Html.fromHtml(ticket.TicketRemind));
		else
			((TextView) remind.findViewById(R.id.description)).setText(getString(R.string.no_data));
		LinearLayout show = (LinearLayout) findViewById(R.id.show);
		((ImageView) show.findViewById(R.id.icon)).setImageResource(R.drawable.ic_show);
		((TextView) show.findViewById(R.id.title)).setText(getString(R.string.pro_show));
		show.setOnClickListener(this);
		LinearLayout intro = (LinearLayout) findViewById(R.id.intro);
		((ImageView) intro.findViewById(R.id.icon)).setImageResource(R.drawable.ic_trodu);
		((TextView) intro.findViewById(R.id.title)).setText(getString(R.string.merchant_intro));
		intro.setOnClickListener(this);
	}

	private void initView() {
		mHttpManager = new HttpManager(this);
		progress = (ProgressBar) findViewById(R.id.progress);
		mainPage = (CustomScrollView) findViewById(R.id.main_page);
		mainPage.setVisibility(View.GONE);
		// mainPage.setOnTouchListener(this);
		mainPage.setOnScrollListener(this);// 为滚动控件注册滚动监听事件
		mViewTop = findViewById(R.id.top_ticket_status);// 悬浮层
		mViewMiddle = findViewById(R.id.middle_ticket_status);// 正常位置的层
		mViewTop.setVisibility(View.INVISIBLE);// 初始化时就影藏
		((RelativeLayout) findViewById(R.id.merchant_layout)).setOnClickListener(this);
		((ImageView) findViewById(R.id.back)).setOnClickListener(this);
		((ImageView) findViewById(R.id.share)).setOnClickListener(this);
		initGallery();
	}

	protected void initGalleryList(String images) {
		imageList.clear();
		if (!Util.isEmpty(images)) {
			String[] list = images.split(",");
			for (String string : list) {
				imageList.add(string);
			}
		}
		mImageAdapter.notifyDataSetChanged();
		initDotLayout(imageList.size());
	}

	private void initDotLayout(int size) {
		if (size > 0) {
			dotLayout.removeAllViews();
			for (int i = 0; i < size; i++) {
				ImageView dot = new ImageView(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(11, 11);
				lp.leftMargin = Util.dip2px(this, 5);
				dot.setLayoutParams(lp);
				dot.setImageResource(R.drawable.ic_dot_black);
				dotLayout.addView(dot);
			}
			((ImageView) dotLayout.getChildAt(0)).setImageResource(R.drawable.ic_dot_red);
		}
	}

	protected void setDotImage(int arg0) {
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			if (i == arg0 % dotLayout.getChildCount()) {
				((ImageView) (dotLayout.getChildAt(i))).setImageResource(R.drawable.ic_dot_red);

			} else {
				((ImageView) (dotLayout.getChildAt(i))).setImageResource(R.drawable.ic_dot_black);

			}
		}
	}

	private void initGallery() {
		dotLayout = (LinearLayout) findViewById(R.id.dot_images);
		gallery = (AdGallery) findViewById(R.id.gallery);
		mImageAdapter = new MyImageAdapter(this, imageList);
		gallery.setAdapter(mImageAdapter);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (imageList.size() > 0)
					setDotImage(arg2 % imageList.size());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.share:
			Util.share(this, new OneKeyShareCallback());
			break;
		case R.id.buy_btn:
			TicketDetailBuyNow();
			if (current_ticket.TicketRemain > 0) {
				if (!Util.isEmpty(getApp().user.UserID)) {
					startActivity(new Intent(this, CommitOrderActivity.class).putExtra("TICKET", current_ticket));
				} else
					UserLoginForResult(REQUEST_BUY_CODE);
			} else {
			}
			break;
		case R.id.phone:
			if (Util.isEmpty(current_ticket.TicketMerchant.MerchantTele))
				return;
			TicketDetailMerchantTel();
			getPopupWindowInstance();
			mPopupWindow.showAtLocation(mainPage, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.show:
			TicketDetailShowPro();
			startActivity(new Intent(this, WebViewBuyActivity.class).putExtra("TICKET", current_ticket));
			break;
		case R.id.merchant_layout:
			TicketDetailMerchantDetail();
			startActivity(new Intent(this, MerchantDetailActivity.class).putExtra("MERCHANTID", current_ticket.TicketMerchant.MerchantId));
			break;
		case R.id.intro:
			TicketDetailMerchantDetailDown();
			startActivity(new Intent(this, MerchantDetailActivity.class).putExtra("MERCHANTID", current_ticket.TicketMerchant.MerchantId));
			break;
		case R.id.call:
			Util.CallUp(this, current_ticket.TicketMerchant.MerchantTele);
			break;
		case R.id.cancel:
			mPopupWindow.dismiss();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_BUY_CODE:
				startActivity(new Intent(this, CommitOrderActivity.class).putExtra("TICKET", current_ticket));
				break;
			case REQUEST_COLLECT:
				DoCollect(1);
				break;

			default:
				break;
			}
		} else {
			if (requestCode == REQUEST_COLLECT) {
				((CheckBox) findViewById(R.id.collect)).setChecked(false);
			}
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
		((Button) popupWindow.findViewById(R.id.call)).setText(current_ticket.TicketMerchant.MerchantTele);
		((Button) popupWindow.findViewById(R.id.call)).setOnClickListener(this);
		((Button) popupWindow.findViewById(R.id.cancel)).setOnClickListener(this);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		DoCollect(isChecked ? 1 : 2);

	}

	private void DoCollect(final int type) {
		if (!Util.isEmpty(getApp().user.UserID)) {
			mHttpManager.Excute(new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... params) {
					return NetworkService.getInstance(TicketDetailActivity.this).HandleCollection(getApp().user.UserID, current_ticket.TicketId, type);
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if (!Util.isEmpty(result)) {
						try {
							JSONObject obj = new JSONObject(result);
							if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
								ShowToast((type == 1) ? getString(R.string.collect_success) : getString(R.string.collect_delete_success));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} else {
			if(type ==1)
				UserLoginForResult(REQUEST_COLLECT);
		}
	}

	@Override
	public void Success(double lat, double lng) {
		isLocationed = true;
		super.Success(lat, lng);
		initData(lat, lng);
	}

	@Override
	public void Fail() {
		isLocationed = false;
		super.Fail();
		initData(0.0, 0.0);
	}

	/**
	 * 快捷分享的监听
	 */
	class OneKeyShareCallback implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			UIHandler.sendEmptyMessage(0, TicketDetailActivity.this);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			UIHandler.sendEmptyMessage(1, TicketDetailActivity.this);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			Message msg = new Message();
			msg.what = -1;
			msg.obj = arg2;
			UIHandler.sendMessage(msg, TicketDetailActivity.this);
		}

	}

	/**
	 * 快捷分享的方回调
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case -1:
			Toast.makeText(TicketDetailActivity.this, getString(R.string.share_error), Toast.LENGTH_LONG).show();
			// System.out.println("分享失败，原因：" + msg.obj);
			break;
		case 1:
			Toast.makeText(this, getString(R.string.share_suc), Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(TicketDetailActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	/******************* 悬浮操作 *********************************/
	
	private int buyLayoutTop;
	private int buyLayoutHeight;

	// 自定义scrollView的滚动监听
	@Override
	public void onScroll(int scrollY) {
		buyLayoutHeight = mViewMiddle.getHeight();
		buyLayoutTop = mViewMiddle.getTop();
		if (scrollY >= buyLayoutTop) {
			mViewTop.setVisibility(View.VISIBLE);
		} else if (scrollY <= buyLayoutTop + buyLayoutHeight) {
			mViewTop.setVisibility(View.INVISIBLE);
		}

	}

}
