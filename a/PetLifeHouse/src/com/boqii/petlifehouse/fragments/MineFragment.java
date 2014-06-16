package com.boqii.petlifehouse.fragments;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.LoginActivity;
import com.boqii.petlifehouse.activities.MyCollectionActivity;
import com.boqii.petlifehouse.activities.MyCouponActivity;
import com.boqii.petlifehouse.activities.MyOrderActivity;
import com.boqii.petlifehouse.activities.MyTicketActivity;
import com.boqii.petlifehouse.activities.UserInfoActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class MineFragment extends BaseFragment implements OnClickListener {

	private BaseApplication app;

	private final int REQUEST_CODE_ALL = 1;
	private final int REQUEST_CODE_UNPAY = 2;
	private final int REQUEST_CODE_PAYED = 3;
	private final int REQUEST_CODE_USERINFO = 4;
	private final int REQUEST_CODE_ORDER = 5;
	private final int REQUEST_CODE_TICKET = 6;
	private final int REQUEST_CODE_COUPON = 7;
	private final int REQUEST_CODE_COLLECT = 8;
	

	private HttpManager mManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mManager = new HttpManager(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		app = (BaseApplication)getActivity().getApplication();
		
		if(!Util.isEmpty(app.user.UserID)){
			view.findViewById(R.id.login).setVisibility(View.GONE);
			view.findViewById(R.id.logined).setVisibility(View.VISIBLE);
			initUserInfo();
			
			GetUserInfo();
		}else{
			view.findViewById(R.id.login).setVisibility(View.VISIBLE);
			view.findViewById(R.id.logined).setVisibility(View.GONE);
		}

	}

	private void initUserInfo() {
		username.setText(app.user.NickName);
		DecimalFormat df=new DecimalFormat("#0.00");
		balance.setText(getString(R.string.price, df.format(app.user.Balance)));
		String all = getString(R.string.all) + "(" + "<font color='red'>" + app.user.AllOrderNum + "</font>" + ")";
		allOrder.setText(Html.fromHtml(all));
		String unpay = getString(R.string.unpay) + "(" + "<font color='red'>" + app.user.UnpayOrderNum + "</font>" + ")";
		unpayOrder.setText(Html.fromHtml(unpay));
		String payed = getString(R.string.payed) + "(" + "<font color='red'>" + app.user.PayedOrderNum + "</font>" + ")";
		payedOrder.setText(Html.fromHtml(payed));		
	}

	private void GetUserInfo() {
		mManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(getActivity()).GetUserInfo(app.user.UserID);
			}

			@Override
			protected void onPostExecute(String result) {
				if(!Util.isEmpty(result)){
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							app.user.NickName = data.optString("NickName");
							app.user.Sex = data.optString("Sex");
							app.user.Telephone = data.optString("Telephone");
							app.user.Balance = (float)data.optDouble("Balance");
							app.user.AllOrderNum = data.optInt("AllOrderNum");
							app.user.UnpayOrderNum = data.optInt("UnpayOrderNum");
							app.user.PayedOrderNum = data.optInt("PayedOrderNum");
							initUserInfo();
						}else{
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private View view;
	private TextView username, balance, allOrder, unpayOrder, payedOrder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.user_info, container, false);
		init(view);
		return view;
	}

	void init(View view) {
		view.findViewById(R.id.login).setOnClickListener(this);
		view.findViewById(R.id.logined).setOnClickListener(this);
		username = (TextView) view.findViewById(R.id.username);
		balance = (TextView) view.findViewById(R.id.account);
		allOrder = (TextView) view.findViewById(R.id.all);
		unpayOrder = (TextView) view.findViewById(R.id.unpay);
		payedOrder = (TextView) view.findViewById(R.id.payed);
		allOrder.setOnClickListener(this);
		unpayOrder.setOnClickListener(this);
		payedOrder.setOnClickListener(this);
		LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.user_info);
		LinearLayout layout2 = (LinearLayout) view
				.findViewById(R.id.myticketorder);
		LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.myticket);
		LinearLayout layout4 = (LinearLayout) view.findViewById(R.id.mycoupon);
		LinearLayout layout5 = (LinearLayout) view.findViewById(R.id.mycollect);
		((ImageView) layout1.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_user_info);
		((TextView) layout1.findViewById(R.id.title))
				.setText(getString(R.string.user_info));
		((ImageView) layout2.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_myticketorder);
		((TextView) layout2.findViewById(R.id.title))
				.setText(getString(R.string.myticketorder));		
		((ImageView) layout3.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_myticket);
		((TextView) layout3.findViewById(R.id.title))
				.setText(getString(R.string.myticket));
		((ImageView) layout4.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_mycoupon);
		((TextView) layout4.findViewById(R.id.title))
				.setText(getString(R.string.mycoupon));
		((ImageView) layout5.findViewById(R.id.icon))
				.setImageResource(R.drawable.ic_mycollect);
		((TextView) layout5.findViewById(R.id.title))
				.setText(getString(R.string.mycollect));
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		layout4.setOnClickListener(this);
		layout5.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			Intent i = new Intent();
			switch (requestCode) {
			case REQUEST_CODE_USERINFO:
				i.setClass(getActivity(), UserInfoActivity.class);
				break;
			case REQUEST_CODE_ALL:
				i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 1);
				break;
			case REQUEST_CODE_UNPAY:
				i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 2);
				break;
			case REQUEST_CODE_PAYED:
				i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 3);
				break;
			case REQUEST_CODE_ORDER:
				i.setClass(getActivity(), MyOrderActivity.class);
				break;
			case REQUEST_CODE_TICKET:
				i.setClass(getActivity(), MyTicketActivity.class);
				break;
			case REQUEST_CODE_COUPON:
				i.setClass(getActivity(), MyCouponActivity.class);
				break;
			case REQUEST_CODE_COLLECT:
				i.setClass(getActivity(), MyCollectionActivity.class);
				break;

			default:
				break;
			}
			startActivity(i);
		}
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.login:
			UserCenterLogin();
			i.setClass(getActivity(), LoginActivity.class);
			break;
		case R.id.logined:
		case R.id.user_info:
			UserCenterInfo();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_USERINFO);
				return;
			}
			i.setClass(getActivity(), UserInfoActivity.class);
			break;
		case R.id.all:
			UserCenterAll();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_ALL);
				return;
			}
			i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 1);
			break;
		case R.id.unpay:
			UserCenterUnpay();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_UNPAY);
				return;
			}
			i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 2);
			break;
		case R.id.payed:
			UserCenterPayed();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_PAYED);
				return;
			}
			i.setClass(getActivity(), MyOrderActivity.class).putExtra("INDEX", 3);
			break;
		case R.id.myticketorder:
			UserCenterOrder();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_ORDER);
				return;
			}
			i.setClass(getActivity(), MyOrderActivity.class);
			break;
		case R.id.myticket:
			UserCenterMyTicket();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_TICKET);
				return;
			}
			i.setClass(getActivity(), MyTicketActivity.class);
			break;
		case R.id.mycoupon:
			UserCenterMyCoupon();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_COUPON);
				return;
			}
			i.setClass(getActivity(), MyCouponActivity.class);
			break;
		case R.id.mycollect:
			UserCenterMyCollection();
			if(Util.isEmpty(app.user.UserID)){
				i.setClass(getActivity(), LoginActivity.class);
				startActivityForResult(i, REQUEST_CODE_COLLECT);
				return;
			}
			i.setClass(getActivity(), MyCollectionActivity.class);
			break;

		default:
			break;
		}
		startActivity(i);
	}
}
