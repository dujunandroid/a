package com.boqii.petlifehouse.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.adapter.MyTicketAdapter;
import com.boqii.petlifehouse.baseactivities.BaseApplication;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.MyTicket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.XListView;
import com.boqii.petlifehouse.widgets.XListView.IXListViewListener;

public class MyTicketFragment extends BaseFragment implements IXListViewListener {

	private XListView listview;
	private ProgressBar progress;
	private View view;
	private MyTicketAdapter mAdapter;
	private ArrayList<MyTicket> list;
	private int index;
	private HttpManager mManager;
	private BaseApplication app;
	private TextView no_data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (BaseApplication) getActivity().getApplication();
		if(getArguments().containsKey("INDEX"))
			index = getArguments().getInt("INDEX");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_list, container, false);
		initView();
		return view;
	}

	boolean isRefresh;
	private void initView() {
		mManager = new HttpManager(getActivity());
		list = new ArrayList<MyTicket>();
		no_data = (TextView)view.findViewById(R.id.nodata);
		no_data.setText(getString(R.string.ticket_no_data));
		no_data.setVisibility(View.GONE);
		listview = (XListView)view.findViewById(R.id.list);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(this);
		progress = (ProgressBar)view.findViewById(R.id.progresss);
		mAdapter = new MyTicketAdapter(getActivity(), list, index);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (list != null && list.size() > arg2 - 1) {
//					MyTicket o = list.get(arg2);
					//TODO deal with myticket
				}
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount >= totalItemCount - 1 && totalItemCount > 0){
					if(!isRefresh){
						initData();
					}
				}
			}
		});
		initData();
	}

	boolean isLoadDone;
	private void initData() {
		if(isLoadDone)
			return;
		isRefresh = true;
		mManager.Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(getActivity()).GetMyTicketList(app.user.UserID, index, list.size(), 10);
			}

			@Override
			protected void onPostExecute(String result) {
				isRefresh = false;
				if(!Util.isEmpty(result)){
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONArray array = obj.optJSONArray("ResponseData");
							if(array != null && array.length() > 0){
								for (int i = 0; i < array.length(); i++) {
									JSONObject data = array.optJSONObject(i);
									MyTicket o = MyTicket.JsonToSelf(data);
									list.add(o);
								}
								mAdapter.notifyDataSetChanged();
							}else{
								isLoadDone = true;
							}
							if(list != null && list.size() == 0){
								no_data.setVisibility(View.VISIBLE);
							}else
								no_data.setVisibility(View.GONE);
							progress.setVisibility(View.GONE);
						}else{
							Toast.makeText(getActivity(), obj.optString("ResponseMsg"), Toast.LENGTH_LONG).show();
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
	public void onRefresh() {
		isLoadDone = false;
		list.clear();
		initData();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
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
