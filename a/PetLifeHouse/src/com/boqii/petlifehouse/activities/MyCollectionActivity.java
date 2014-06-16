package com.boqii.petlifehouse.activities;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.adapter.CollectionAdapter;
import com.boqii.petlifehouse.adapter.CollectionAdapter.DeleteListener;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Collection;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.XListView;
import com.boqii.petlifehouse.widgets.XListView.IXListViewListener;

public class MyCollectionActivity extends BaseActivity implements
		DeleteListener, OnClickListener, IXListViewListener {

	private XListView listview;
	private ProgressBar progress;
	private CollectionAdapter mAdapter;
	private ArrayList<Collection> list;
	private HttpManager mManager;
	private BaseApplication app;
	private TextView no_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycollection);
		app = (BaseApplication) getApplication();
		initView();
	}

	boolean isRefresh;

	private void initView() {
		no_data = (TextView) findViewById(R.id.nodata);
		no_data.setText(getString(R.string.collect_no_data));
		no_data.setVisibility(View.GONE);
		((ImageView) findViewById(R.id.back)).setOnClickListener(this);
		mManager = new HttpManager(this);
		list = new ArrayList<Collection>();
		listview = (XListView) findViewById(R.id.list);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		progress = (ProgressBar) findViewById(R.id.progresss);
		mAdapter = new CollectionAdapter(this, list, this);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (list != null && list.size() > arg2 - 1) {
					Collection c = list.get(arg2 - 1);
					if (c != null) {
						startActivity(new Intent(MyCollectionActivity.this,
								TicketDetailActivity.class).putExtra(
								"TICKETID", c.ticket.TicketId));
					}
				}
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount >= totalItemCount - 1
						&& totalItemCount > 0) {
					if (!isRefresh) {
						initData();
					}
				}
			}
		});
		initData();
	}

	boolean isLoadDone;

	private void initData() {
		if (isLoadDone)
			return;
		isRefresh = true;
		mManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(MyCollectionActivity.this)
						.GetMyCollectionList(app.user.UserID, list.size(), 10);
			}

			@Override
			protected void onPostExecute(String result) {
				isRefresh = false;
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONArray array = obj.optJSONArray("ResponseData");
							if (array != null && array.length() > 0) {
								for (int i = 0; i < array.length(); i++) {
									JSONObject data = array.optJSONObject(i);
									Collection c = Collection.JsonToSelf(data);
									list.add(c);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								isLoadDone = true;
							}
							if (list != null && list.size() == 0) {
								no_data.setVisibility(View.VISIBLE);
							} else
								no_data.setVisibility(View.GONE);
							progress.setVisibility(View.GONE);
						} else {
							Toast.makeText(MyCollectionActivity.this,
									obj.optString("ResponseMsg"),
									Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				RefreshDone();
			}
		});
	}

	@Override
	public void Delete(final int position) {
		if (list != null && list.size() > position) {
			CollectionListDelete();
			Collection c = list.get(position);
			final int ticketid = c.ticket.TicketId;
			mManager.Excute(new AsyncTask<Void, Void, String>() {

				@Override
				protected String doInBackground(Void... params) {
					return NetworkService
							.getInstance(MyCollectionActivity.this)
							.HandleCollection(app.user.UserID, ticketid, 2);
				}

				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (!Util.isEmpty(result)) {
						try {
							JSONObject obj = new JSONObject(result);
							if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
								list.remove(position);
								mAdapter.notifyDataSetChanged();
								ShowToast(getString(R.string.delete_success));
							} else {
//								mAdapter.HideLayout(position);
								mAdapter.notifyDataSetChanged();
//								ShowToast(obj.optString("ResponseMsg"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		isLoadDone = false;
		list.clear();
		initData();
	}

	@Override
	public void onLoadMore() {
		
	}
	
	private void RefreshDone(){
		long time=System.currentTimeMillis();
		Calendar mCalendar=Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int mHour=mCalendar.get(Calendar.HOUR);
		int mMinuts=mCalendar.get(Calendar.MINUTE);
		listview.stopRefresh();
		listview.setRefreshTime(mHour+":"+mMinuts);
	}
}
