package com.boqii.petlifehouse.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.TicketDetailActivity;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.LocationManager;
import com.boqii.petlifehouse.utilities.LocationManager.MyLocationListener;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.CategoryDialogView;
import com.boqii.petlifehouse.widgets.LineTextView;
import com.boqii.petlifehouse.widgets.NetImageView;
import com.boqii.petlifehouse.widgets.CategoryDialogView.CateGoryDialogCallBack;
import com.boqii.petlifehouse.widgets.XListView;
import com.boqii.petlifehouse.widgets.XListView.IXListViewListener;

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
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListTicketFragment extends BaseFragment implements OnItemClickListener, OnClickListener, CateGoryDialogCallBack, IXListViewListener, MyLocationListener {
	public static Bundle bundleTicket = new Bundle();
	private Bitmap defaultImg;
	public int orderId = 1;// 排序
	public int typeId = 0;// 分类
	private TextView category, order;// 标题类型、排序、区域
	private ArrayList<TextView> lst;
	double LatLng[] = new double[2];
	private LocationManager locationManager;

	public static ListTicketFragment newLListTicketFragment(Bundle bundle) {
		bundleTicket = bundle;
		return new ListTicketFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
	}

	private View view;
	private ArrayList<Ticket> ticketLst;
	private XListView mTicketLV;
	private NearTicketAdapter adapter;
	private ProgressBar mBar;// 加载进度框
	private CategoryDialogView categoryDialogView;// 自定义排序分类控件
	private Intent mIntent;
	private TextView mNodata;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_near_ticket, container, false);
		mIntent = getActivity().getIntent();
		lst = new ArrayList<TextView>();
		category = (TextView) view.findViewById(R.id.category);
		order = (TextView) view.findViewById(R.id.order);
		lst.add(category);
		lst.add(order);
		init();
		return view;
	}

	String keyWord = "";

	void init() {		
		mBar = (ProgressBar) view.findViewById(R.id.progressNT);
		((LinearLayout) view.findViewById(R.id.category_layout)).setOnClickListener(this);
		((LinearLayout) view.findViewById(R.id.order_layout)).setOnClickListener(this);
		categoryDialogView = (CategoryDialogView) view.findViewById(R.id.cate_gory_dialog_view);
		categoryDialogView.execute(this);
		ticketLst = new ArrayList<Ticket>();
		adapter = new NearTicketAdapter();
		mTicketLV = (XListView) view.findViewById(R.id.nearTickeList);
		mTicketLV.setXListViewListener(this);
		mTicketLV.setPullLoadEnable(false);
		mTicketLV.setAdapter(adapter);// 填充数据
		mTicketLV.setOnItemClickListener(this);// 子项点击监听
		mNodata = (TextView) view.findViewById(R.id.nodataNT);
		mNodata.setVisibility(View.INVISIBLE);
		keyWord = getActivity().getIntent().getStringExtra("SearchKey");
		if (keyWord == null) {
			keyWord = "";
		}
		this.typeId = mIntent.getIntExtra(HomeFragment.TYPE_CODE, 0);
		mBar.setVisibility(Gallery.VISIBLE);
		locationManager = new LocationManager(getActivity(), this);
		locationManager.StartLocation();
	}

	/**
	 * // 填充数据
	 * 
	 * @param lat维度
	 * @param lng经度
	 */
	private void initData(final String KeyWord, final int SortTypeId, final int OrderTypeId, final double Lat, final double Lng, final int StartIndex, final int Number) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String result = "";
				try {
					NetworkService service = NetworkService.getInstance(getActivity());// 获取网路操作实例
					result = service.SearchTicketList(KeyWord, SortTypeId, OrderTypeId, LatLng[0], LatLng[1], StartIndex, Number, 0);

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result != null) {
					return result;
				} else {
					return getString(R.string.near_date_ticket);
				}
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {

					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONArray array = obj.optJSONArray("ResponseData");// 得到数组
							if (array != null && array.length() > 0) {
								mNodata.setVisibility(View.INVISIBLE);
								for (int i = 0; i < array.length(); i++) {
									ticketLst.add(Ticket.JsonToSelf(array.getJSONObject(i)));// 将对应的数据添加到服务券集合中
								}
								mBar.setVisibility(Gallery.INVISIBLE);
								mTicketLV.setPullLoadEnable(true);
							} else {
								if (ticketLst.size() <= 0) {
									mNodata.setVisibility(View.VISIBLE);
									mTicketLV.setPullLoadEnable(false);
								}
								//Toast.makeText(getActivity(), getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
							}
							adapter.setData(ticketLst);
							adapter.notifyDataSetChanged();
							startIndex = ticketLst.size();// 记录数据的起始位置
							onLoad();
						}else{
							Toast.makeText(getActivity(), getString(R.string.net_error), Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						mBar.setVisibility(View.GONE);						
					}
				}
			}
		}.execute();

	}

	class NearTicketAdapter extends BaseAdapter {
		private ArrayList<Ticket> list = new ArrayList<Ticket>();

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.ticket_item, null);
				holder.mImageView = (NetImageView) convertView.findViewById(R.id.image);
				holder.mTitle = (TextView) convertView.findViewById(R.id.title);
				holder.mBUsinessareal = (TextView) convertView.findViewById(R.id.businessarea);
				holder.mDistance = (TextView) convertView.findViewById(R.id.distance);
				holder.mPrice = (TextView) convertView.findViewById(R.id.price);
				holder.mOriPrice = (LineTextView) convertView.findViewById(R.id.oriPrice);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Ticket ticket = list.get(position);

			if (ticket != null) {
				holder.mImageView.setImageUrl(Util.GetImageUrl(ticket.TicketImg, Util.dip2px(getActivity(), 100), Util.dip2px(getActivity(), 72)), Constants.PATH, defaultImg);
				holder.mTitle.setText(ticket.TicketTitle);
				holder.mBUsinessareal.setText(ticket.BusinessArea);
				holder.mDistance.setText(Util.GetDistanceToKM(ticket.Distance));
				// 保留小数位数，不足会补零
				DecimalFormat r = new DecimalFormat();
				r.applyPattern("#0.00");
				holder.mPrice.setText(getString(R.string.now_price) + r.format(ticket.TicketPrice));
				holder.mOriPrice.setText(getString(R.string.ori_Price) + r.format(ticket.TicketOriPrice));
			}
			return convertView;
		}

		public class ViewHolder {
			NetImageView mImageView;
			TextView mTitle;
			TextView mBUsinessareal;
			TextView mDistance;
			TextView mPrice;
			LineTextView mOriPrice;
		}

		public void setData(ArrayList<Ticket> list) {
			this.list = list;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
		index = index == 0 ? index : index - 1;// 因为有头部视图，因此用户点击的项位置需要减一
		Ticket ticket = ticketLst.get(index);
		Intent mIntent = new Intent();
		mIntent.setClass(getActivity(), TicketDetailActivity.class);
		mIntent.putExtra("TICKETID", ticket.TicketId);

		startActivity(mIntent);

	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.category_layout:
			ListTicketType();
			categoryDialogView.ShowDialog(1);
			break;
		case R.id.order_layout:
			ListTicketSort();
			categoryDialogView.ShowDialog(2);
			break;

		default:
			break;
		}
	}

	@Override
	public void categoryCallback(String name, int typeId) {
		this.typeId = typeId;
		if (ticketLst != null) {
			ticketLst.clear();
			adapter.notifyDataSetChanged();
		}
		category.setText(name);
		setTitelColor(lst, 0);
		mBar.setVisibility(Gallery.VISIBLE);
		initData(keyWord, this.typeId, this.orderId, 0, 0, 0, 10);
	}

	@Override
	public void orderCallBack(String name, int orderId) {
		this.orderId = orderId;
		if (ticketLst != null) {
			ticketLst.clear();
			adapter.notifyDataSetChanged();
		}
		order.setText(name);
		setTitelColor(lst, 1);
		mBar.setVisibility(Gallery.VISIBLE);
		initData(keyWord, this.typeId, this.orderId, 0, 0, 0, 10);
	}

	public int startIndex = 0;// 数据起始位置

	// 改变标题颜色
	private void setTitelColor(ArrayList<TextView> lst, int index) {
		for (int i = 0; i < lst.size(); i++) {
			if (i == index) {
				lst.get(i).setTextColor(getResources().getColor(R.color.text_yellow));
			} else {
				lst.get(i).setTextColor(getResources().getColor(R.color.TextColorBlack));
			}
		}
	}

	@Override
	public void onRefresh() {// 顶部下拉刷新
		if (ticketLst != null) {
			ticketLst.clear();
			adapter.notifyDataSetChanged();
		}
		initData(keyWord, this.typeId, this.orderId, 0, 0, 0, 10);

	}

	@Override
	public void onLoadMore() {// 上拉到底加载更多
		initData(keyWord, this.typeId, this.orderId, 0, 0, startIndex, 10);
	}

	private void onLoad() {
		long time = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int mHour = mCalendar.get(Calendar.HOUR);
		int mMinuts = mCalendar.get(Calendar.MINUTE);
		mTicketLV.stopRefresh();
		mTicketLV.stopLoadMore();
		mTicketLV.setRefreshTime(mHour + ":" + mMinuts);
	}

	@Override
	public void Success(double lat, double lng) {
		LatLng[0] = lat;
		LatLng[1] = lng;
		locationManager.StopLocation();
		initData(keyWord, this.typeId, this.orderId, 0, 0, startIndex, 10);
	}

	@Override
	public void Fail() {

	}
}
