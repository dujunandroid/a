package com.boqii.petlifehouse.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.MerchantDetailActivity;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Merchant;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.LocationManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.utilities.LocationManager.MyLocationListener;
import com.boqii.petlifehouse.widgets.CategoryListMerchantDialog;
import com.boqii.petlifehouse.widgets.CategoryListMerchantDialog.CateGoryDialogMerchantCallBack;
import com.boqii.petlifehouse.widgets.NetImageView;
import com.boqii.petlifehouse.widgets.XListView;
import com.boqii.petlifehouse.widgets.XListView.IXListViewListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListMerchantFragment extends BaseFragment implements
		OnItemClickListener, CateGoryDialogMerchantCallBack,OnClickListener, 
		IXListViewListener,MyLocationListener{
	private TextView category, order, area;// 标题类型、排序、区域
	private ArrayList<TextView> lst;
	private View view;
	private ArrayList<Merchant> mMerchantLst;// 商户列表信息集合
	private NearMerchantAdaper adaperM;// 商户列表适配器
	private XListView mMerchantLV;// 商户列表
	private ProgressBar mBar;// 加载进度框
	public static Bundle bundleLM = new Bundle();
	private CategoryListMerchantDialog categoryMerchantDialog;
	private Bitmap defaultImg;
	private TextView mNodata;
	double LatLng[] = new double[2];
	private LocationManager locationManager;
	public static ListMerchantFragment newListMerchantFragment(Bundle bundle) {
		bundleLM = bundle;
		ListMerchantFragment fragment = new ListMerchantFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultImg = BitmapFactory.decodeResource(getResources(),
				R.drawable.nopic);
		
	}

	// 改变标题颜色
	private void setTitelColor(ArrayList<TextView> lst, int index) {
		for (int i = 0; i < lst.size(); i++) {
			if (i == index) {
				lst.get(i).setTextColor(
						getResources().getColor(R.color.text_yellow));
			} else {
				lst.get(i).setTextColor(
						getResources().getColor(R.color.TextColorBlack));
			}
		}
	}

	String keyWord = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_list_merchant, container,
				false);
		lst = new ArrayList<TextView>();
		category = (TextView) view.findViewById(R.id.category);
		lst.add(category);
		order = (TextView) view.findViewById(R.id.order);
		lst.add(order);
		area = (TextView) view.findViewById(R.id.area);
		lst.add(area);
		init();
		// getAreaType();
		keyWord = getActivity().getIntent().getStringExtra("SearchKey");
		if (keyWord == null) {
			keyWord = "";
		}
		mBar.setVisibility(Gallery.VISIBLE);
		locationManager = new LocationManager(getActivity(), this);
		locationManager.StartLocation();
		return view;
	}

	void init() {		
		mMerchantLV = (XListView) view.findViewById(R.id.listMerchantList);
		mMerchantLV.setXListViewListener(this);
		mMerchantLV.setPullLoadEnable(false);//是否启用加载更多
		mMerchantLst = new ArrayList<Merchant>();
		adaperM = new NearMerchantAdaper();
		mMerchantLV.setAdapter(adaperM);
		mMerchantLV.setOnItemClickListener(this);
		mNodata=(TextView) view.findViewById(R.id.nodataLM);
		mNodata.setVisibility(View.INVISIBLE);
		mBar = (ProgressBar) view.findViewById(R.id.progressLM);
		((LinearLayout) view.findViewById(R.id.category_layout))
				.setOnClickListener(this);
		((LinearLayout) view.findViewById(R.id.order_layout))
				.setOnClickListener(this);
		(view.findViewById(R.id.area_layout)).setOnClickListener(this);
		categoryMerchantDialog = (CategoryListMerchantDialog) view
				.findViewById(R.id.category_merchant_dialog);
		categoryMerchantDialog.execute(this);
	}

	void initData(final String KeyWord, final int AreaId, final int Type,final int SortTypeId,
			final int OrderTypeId, final double Lat, final double Lng,
			final int StartIndex, final int Number) {// 填充实体类的数据		
		new HttpManager(getActivity())
				.Excute(new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {
						String result = "";
						try {
							NetworkService service = NetworkService
									.getInstance(getActivity());// 获取网路操作实例
							result = service.SearchMerchantList(KeyWord,
									AreaId, Type,SortTypeId, OrderTypeId,  LatLng[0], LatLng[1],
									StartIndex, Number,0);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (result != null && result.length() > 0) {
							return result;
						} else {
							return getString(R.string.near_date_merchant);
						}

					}

					@Override
					protected void onPostExecute(String result) {
						super.onPostExecute(result);
						if (!Util.isEmpty(result)) {					
							try {
								JSONObject obj = new JSONObject(result);
								if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
									JSONArray array = obj
											.optJSONArray("ResponseData");
									if (array != null && array.length() > 0) {
										//System.out.println("array=="+array);
										mNodata.setVisibility(View.INVISIBLE);
										for (int i = 0; i < array.length(); i++) {
											mMerchantLst.add(Merchant
													.JsonToSelf(array
															.getJSONObject(i)));
										}									
										mMerchantLV.setPullLoadEnable(true);
										Bundle bundle = new Bundle();
										bundle.putSerializable(
												NearFragment.RESULT_CODE,
												mMerchantLst);
										NearFragment.newInstance(bundle);

									} else {
										if(mMerchantLst.size()<=0){
											mNodata.setVisibility(View.VISIBLE);
											mMerchantLV.setPullLoadEnable(false);
										}
//										Toast.makeText(getActivity(),
//												getResources().getString(R.string.no_more_data),Toast.LENGTH_SHORT).show();
									}
									adaperM.setData(mMerchantLst);
									adaperM.notifyDataSetChanged();										
									startIndex = mMerchantLst.size();// 记录数据的起始位置
									onLoad();
								}else{
									Toast.makeText(getActivity(), getString(R.string.net_error), Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								mBar.setVisibility(View.GONE);// 隐藏进度框							
							}
						}
					}
				});
	}

//	private void getAreaType() {
//		new HttpManager(getActivity())
//				.Excute(new AsyncTask<Void, Void, String>() {
//
//					@Override
//					protected String doInBackground(Void... params) {
//
//						return NetworkService.getInstance(getActivity())
//								.GetAreaType();
//					}
//
//					@Override
//					protected void onPostExecute(String result) {
//						super.onPostExecute(result);
//						if (!Util.isEmpty(result)) {
//							try {
//								JSONObject obj = new JSONObject(result);
//								if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
//									JSONArray array = obj
//											.getJSONArray("ResponseData");
//									System.out.println(array);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				});
//	}

	class NearMerchantAdaper extends BaseAdapter {// 填充商户列表的适配器
		private ArrayList<Merchant> list = new ArrayList<Merchant>();

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
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.merchant_item, null);
				holder.mImage = (NetImageView) convertView
						.findViewById(R.id.image);
				holder.mTitle = (TextView) convertView.findViewById(R.id.title);
				holder.mTicketIcon = (ImageView) convertView
						.findViewById(R.id.ticket_icon);
				holder.mCheckIcon = (ImageView) convertView
						.findViewById(R.id.check_icon);
				holder.mCertificate=(ImageView) convertView.findViewById(R.id.certificate_icon);
				holder.mBusinessarea = (TextView) convertView
						.findViewById(R.id.businessarea);
				holder.mDistance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.mPrice = (TextView) convertView.findViewById(R.id.price);
				holder.mScanNumber = (TextView) convertView
						.findViewById(R.id.scan_number);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Merchant merchant = list.get(position);
			if (merchant != null) {
				holder.mImage.setImageUrl(Util.GetImageUrl(merchant.MerchantImg, Util.dip2px(getActivity(), 100), Util.dip2px(getActivity(), 72)), Constants.PATH,
						defaultImg);
				String[] icon = merchant.Characteristic.split(",");
				holder.mTicketIcon.setVisibility(View.GONE);
				holder.mCheckIcon.setVisibility(View.GONE);
				holder.mCertificate.setVisibility(View.GONE);
				for (int i = 0; i < icon.length; i++) {
					// 0,无，1,券，2,疫,3认证
					if (icon[i].equals("1")) {
						holder.mTicketIcon.setVisibility(View.VISIBLE);					
					} else if (icon[i].equals("2")) {
						holder.mCheckIcon.setVisibility(View.VISIBLE);					
					} else{
						holder.mCertificate.setVisibility(View.VISIBLE);
					}
				}
				holder.mTitle.setText(merchant.MerchantName);
				holder.mBusinessarea.setText(merchant.BusinessArea);
				// 距离多远的值
				String distance = Util.GetDistanceToKM(merchant.MerchantDistance);
				holder.mDistance.setText(distance);
				// 人均消费
				String consumePerPerson = String.format(
						getString(R.string.consumption),
						merchant.ConsumePerPerson);
				holder.mPrice.setText(consumePerPerson);
				// 浏览次数
				String scanNumber = String.format(getString(R.string.scan),
						merchant.ScanNumber);
				holder.mScanNumber.setText(scanNumber);

			}
			return convertView;
		}

		class ViewHolder {
			NetImageView mImage;
			TextView mTitle;
			ImageView mTicketIcon;
			ImageView mCheckIcon;
			ImageView mCertificate;
			TextView mBusinessarea;
			TextView mDistance;
			TextView mPrice;
			TextView mScanNumber;
		}

		public void setData(ArrayList<Merchant> list) {
			this.list = list;
		}
	}



	public int startIndex = 0;// 数据起始位置
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
		index=index==0?index:index-1;//因为有头部视图，因此用户点击的项位置需要减一
		Merchant merchant = new Merchant();
		merchant = mMerchantLst.get(index);
		Intent mIntent = new Intent();
		mIntent.setClass(getActivity(), MerchantDetailActivity.class);
		mIntent.putExtra("MERCHANTID", merchant.MerchantId);
		startActivity(mIntent);
	}

	public int typeId = 0, orderId = 1, areaCityId = 0,type=2;

	@Override
	public void categoryCallback(String name, int typeId) {// 分类点击事件回调
		// System.out.println(name + "=Type=" + typeId);
		this.typeId = typeId;
		if(mMerchantLst!=null){
			mMerchantLst.clear();
			adaperM.notifyDataSetChanged();
		}		
		category.setText(Util.getStrFormatSize(name, 5));
		setTitelColor(lst, 0);
		mBar.setVisibility(Gallery.VISIBLE);
		initData(keyWord, areaCityId, this.type,this.typeId, this.orderId, 0, 0, 0, 10);
	}

	@Override
	public void orderCallBack(String name, int typeId) {// 排序点击事件回调
		// System.out.println(name + "=order=" + typeId);
		this.orderId = typeId;
		if(mMerchantLst!=null){
			mMerchantLst.clear();
			adaperM.notifyDataSetChanged();
		}
		order.setText(Util.getStrFormatSize(name, 5));
		setTitelColor(lst, 1);
		mBar.setVisibility(Gallery.VISIBLE);
		initData(keyWord, areaCityId, this.type,this.typeId, this.orderId, 0, 0, 0, 10);
	}

	@Override
	public void AreaCallBack(String areaName, int areaCityId,int type) {// 地区点击事件回调
		// Toast.makeText(getActivity(), areaName, Toast.LENGTH_SHORT).show();
		if(mMerchantLst!=null){
			mMerchantLst.clear();
			adaperM.notifyDataSetChanged();
		}
		this.areaCityId = areaCityId;
		this.type=type;
		area.setText(Util.getStrFormatSize(areaName, 5));
		setTitelColor(lst, 2);
		mBar.setVisibility(Gallery.VISIBLE);
		//System.out.println("areaId="+areaCityId+"...type="+type);
		initData(keyWord, this.areaCityId, this.type,this.typeId, this.orderId, 0, 0, 0,
				10);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.category_layout:
			ListMerchantType();
			categoryMerchantDialog.ShowDialog(1);
			break;
		case R.id.order_layout:
			ListMerchantArea();
			categoryMerchantDialog.ShowDialog(2);
			break;
		case R.id.area_layout:
			ListMerchantSort();
			categoryMerchantDialog.ShowDialog(3);
			break;
		}
	}
	
	public void onRefresh() {// 顶部下拉刷新
		if (mMerchantLst != null) {
			mMerchantLst.clear();
			adaperM.notifyDataSetChanged();
		}
		//System.out.println("startindex="+startIndex);
		initData(keyWord, this.areaCityId,this.type, this.typeId, this.orderId, 0, 0, 0, 10);
	}

	@Override
	public void onLoadMore() {// 上拉到底加载更多
		//System.out.println("startIndex="+startIndex);
		initData(keyWord, this.areaCityId, this.type,this.typeId, this.orderId, 0, 0, startIndex,10 );
	}

	private void onLoad() {
		long time=System.currentTimeMillis();
		Calendar mCalendar=Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int mHour=mCalendar.get(Calendar.HOUR);
		int mMinuts=mCalendar.get(Calendar.MINUTE);
		mMerchantLV.stopRefresh();
		mMerchantLV.stopLoadMore();
		mMerchantLV.setRefreshTime(mHour+":"+mMinuts);
	}
	
	@Override
	public void Success(double lat, double lng) {
		LatLng[0] = lat;
		LatLng[1] = lng;
		locationManager.StopLocation();
		initData(keyWord, this.areaCityId, this.type,this.typeId, this.orderId, 0, 0, startIndex, 10);
	}

	@Override
	public void Fail() {

	}
}
