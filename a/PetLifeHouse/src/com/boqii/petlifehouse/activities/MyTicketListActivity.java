package com.boqii.petlifehouse.activities;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.MyTicket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.NetImageView;
import com.boqii.petlifehouse.widgets.XListView;
import com.boqii.petlifehouse.widgets.XListView.IXListViewListener;

public class MyTicketListActivity extends BaseActivity implements OnClickListener,OnItemClickListener ,IXListViewListener{

	private ProgressBar mBar;
	private XListView mMyTicketList;
	private TextView mNodata;
	private HttpManager mHManager;
	private ArrayList<MyTicket> mMTList;
	private MyTicketListAdapter mAdapter;
	private int id;
	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myticketlist);
		initView();
		id = getIntent().getIntExtra("ORDERID",0);
		initData(id,0,10);
	}

	void initView() {
		mHManager = new HttpManager(this);
		pd=new ProgressDialog(this);
		pd.setMessage(getString(R.string.content_login));
		mMTList=  new ArrayList<MyTicket>();
		mBar = (ProgressBar) findViewById(R.id.progressMTList);
		mBar.setVisibility(View.VISIBLE);
		findViewById(R.id.backMTList).setOnClickListener(this);
		mMyTicketList = (XListView) findViewById(R.id.myTickeList);
		mMyTicketList.setPullRefreshEnable(false);
		mMyTicketList.setPullLoadEnable(false);
		mMyTicketList.setXListViewListener(this);
		mMyTicketList.setOnItemClickListener(this);
		mNodata = (TextView) findViewById(R.id.nodataMTList);
		mAdapter = new MyTicketListAdapter(this, mMTList);
		mMyTicketList.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backMTList:
			finish();
			break;

		default:
			break;
		}

	}

	private void initData(final int OrderId,final int StartIndex,final int Number){
		mHManager.Excute(new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(MyTicketListActivity.this).GetMyTicketsInOrder(OrderId, StartIndex, Number);
			}
			
			@Override
			protected void onPostExecute(String result){
				if(!Util.isEmpty(result)){
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus") == Constants.RESPONSE_OK) {
							JSONArray array = obj.optJSONArray("ResponseData");
							if(array != null && array.length() > 0){
								mNodata.setVisibility(View.INVISIBLE);
								if(array.length()>9){
									mMyTicketList.setPullLoadEnable(false);
								}
								for (int i = 0; i < array.length(); i++) {
									mMTList.add(MyTicket.JsonToSelf(array.optJSONObject(i)));
								}
								mAdapter.notifyDataSetChanged();
							}else{
								if(mMTList.size()<=0){
									mNodata.setVisibility(View.VISIBLE);
								}
								ShowToast(getString(R.string.no_data));
							}
						}else{
							ShowToast(obj.optString("ResponseMsg"));
						}
					}catch (JSONException e) {
						e.printStackTrace();
					}finally{
						 onLoad();
						mBar.setVisibility(View.INVISIBLE);
					}
				}
			}
			
		});		
	}
	
	private void onLoad() {
		mMyTicketList.stopLoadMore();
	}
	
	@Override
	public void onRefresh() {// 顶部下拉刷新
	}

	@Override
	public void onLoadMore() {// 上拉到底加载更多
		initData(id,mMTList.size(),10);	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	
	public class MyTicketListAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<MyTicket> list;
		private LayoutInflater mInflater;
		private Bitmap defaultImg; 
		public MyTicketListAdapter(Context context, ArrayList<MyTicket> list){
			this.context = context;
			mInflater = LayoutInflater.from(context);
			this.list = list;
			defaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.nopic);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			ViewHolder holder;
			if(v == null){
				holder = new ViewHolder();
			}else{
				holder = (ViewHolder)v.getTag();
			}
			v = holder.getView(arg0);
			return v;
		}
		
		class ViewHolder{
			View itemView;
			NetImageView icon;
			TextView title, price, status, look;
			public ViewHolder(){
				itemView = mInflater.inflate(R.layout.myticket_item, null);
				itemView.setTag(this);
				icon = (NetImageView)itemView.findViewById(R.id.image);
				title = (TextView)itemView.findViewById(R.id.title);
				price = (TextView)itemView.findViewById(R.id.price);
				status = (TextView)itemView.findViewById(R.id.status);
				look = (TextView)itemView.findViewById(R.id.look);
			}
			public View getView(int position){
				DecimalFormat df=new DecimalFormat("#0.00");
				MyTicket myticket = list.get(position);
				icon.setImageUrl(Util.GetImageUrl(myticket.MyTicketImg, Util.dip2px(context, 100), Util.dip2px(context, 72)), Constants.PATH, defaultImg);
				title.setText(myticket.MyTicketTitle);
				price.setText(context.getString(R.string.price,df.format(myticket.MyTicketPrice)));
				status.setText(myticket.MyTicketStatus);
				look.setVisibility(View.VISIBLE);
				int index=Integer.parseInt(myticket.MyTicketStatus);
				switch (index) {
				case 1:
					look.setVisibility(View.VISIBLE);
					final int myticketid = myticket.MyTicketId;
					look.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							MyTicket t = new MyTicket();
							t.MyTicketId = myticketid;
							context.startActivity(new Intent(context, MyTicketDetailActivity.class).putExtra("MYTICKET", t));
						}
					});
					status.setText(context.getString(R.string.unused));
					break;
				case 2:
					status.setText(context.getString(R.string.used));
					break;
				case 3:
					status.setText(context.getString(R.string.outoftime));
					break;

				default:
					break;
				}
				return itemView;
			}
		}

	}
}
