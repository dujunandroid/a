package com.boqii.petlifehouse.activities;

import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Coupon;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CouponDetailActivity extends BaseActivity implements
		OnClickListener {

	private ProgressBar progress;
	private ScrollView mainPage;
	private HttpManager mHttpManager;
	private Coupon current_coupon;
	private BaseApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coupondetail);
		app = (BaseApplication)getApplication();
		initView();
		initData();
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
			//TODO
			break;
		case R.id.copy:
			CouponDetailCopy();
			ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(current_coupon.CouponNo);
			ShowToast(getString(R.string.copyed));
			break;

		default:
			break;
		}
		
	}

	private void initData() {
		current_coupon = new Coupon();
		current_coupon = (Coupon)getIntent().getSerializableExtra("COUPON");
		mHttpManager.Excute(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				// FIXME
				return NetworkService.getInstance(CouponDetailActivity.this).GetCouponDetail(app.user.UserID, current_coupon.CouponId);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							int status = current_coupon.CouponStatus;
							current_coupon = Coupon.JsonToSelf(obj.optJSONObject("ResponseData"));
							current_coupon.CouponStatus = status;
							initCoupon(current_coupon);
							mainPage.setVisibility(View.VISIBLE);
							progress.setVisibility(View.GONE);
						}else{
							ShowToast(obj.optString("ResponseMsg"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	protected void initCoupon(Coupon coupon) {
		if (coupon == null)
			return;
		((TextView)findViewById(R.id.mytickettitle)).setText(coupon.CouponTitle);
		((TextView)findViewById(R.id.price)).setText(getString(R.string.yuan, coupon.CouponPrice));
		((TextView)findViewById(R.id.ticketstatus)).setText((coupon.CouponStatus == 1) ? "未使用" : (coupon.CouponStatus == 2) ? "已使用" : "已过期");
		((TextView)findViewById(R.id.voucher_no)).setText(coupon.CouponNo);
		((Button)findViewById(R.id.copy)).setOnClickListener(this);
		((TextView)findViewById(R.id.least_use)).setText(coupon.CouponCondition);
		((TextView)findViewById(R.id.useable)).setText(coupon.CouponStartTime);
		((TextView)findViewById(R.id.unuseable)).setText(coupon.CouponEndTime);
		RelativeLayout detail = (RelativeLayout) findViewById(R.id.detail);
		((ImageView) detail.findViewById(R.id.icon)).setImageResource(R.drawable.ic_detail);
		((TextView) detail.findViewById(R.id.title)).setText(getString(R.string.coupon_detail));
		if (!Util.isEmpty(coupon.CouponDesc))
			((TextView) detail.findViewById(R.id.description)).setText(Html.fromHtml(coupon.CouponDesc));
		RelativeLayout remind = (RelativeLayout) findViewById(R.id.remind);
		((ImageView) remind.findViewById(R.id.icon)).setImageResource(R.drawable.ic_range);
		((TextView) remind.findViewById(R.id.title)).setText(getString(R.string.range));
		if (!Util.isEmpty(coupon.CouponRange))
			((TextView) remind.findViewById(R.id.description)).setText(Html.fromHtml(coupon.CouponRange));
		
		switch (coupon.CouponStatus) {
		case 1://未使用
			((LinearLayout)findViewById(R.id.use_order_layout)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.detail_layout)).setVisibility(View.VISIBLE);
			break;
		case 2://已使用
			((LinearLayout)findViewById(R.id.use_order_layout)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.detail_layout)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.image)).setBackgroundResource(R.drawable.ic_coupon_unable);
			((TextView)findViewById(R.id.order_no)).setText(coupon.CouponUsedOrder);
			((TextView)findViewById(R.id.usetime)).setText(coupon.CouponUsedTime);
			break;
		case 3://已过期
			((LinearLayout)findViewById(R.id.use_order_layout)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.detail_layout)).setVisibility(View.VISIBLE);
			((ImageView)findViewById(R.id.image)).setBackgroundResource(R.drawable.ic_coupon_unable);
			break;

		default:
			break;
		}
		
	}

}
