package com.boqii.petlifehouse.activities;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Merchant;
import com.boqii.petlifehouse.fragments.NearFragment;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

public class MapPatternActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnCameraChangeListener,
		OnClickListener ,OnMapLoadedListener{

	private MapView mapView;
	private AMap aMap;
	private ArrayList<Marker> mList;// 图标集合
	private ArrayList<Merchant> mMListAll;// 商户集合
	private Button mSearchBtn;// 移动地图时的搜索按钮
	private int width=1;//屏幕宽度
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_pattern);
		mapView = (MapView) findViewById(R.id.mMapView);
		mapView.onCreate(savedInstanceState);
		width = getWindowManager().getDefaultDisplay().getWidth();
		initData();
		initView();
	}

	/**
	 * 加载传过来的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Merchant> initData() {
		mMListAll = new ArrayList<Merchant>();// 商户集合
		Bundle bundle = getIntent().getExtras();
		mMListAll = (ArrayList<Merchant>) bundle
				.getSerializable(NearFragment.RESULT_CODE);
		return mMListAll;
	}

	private UiSettings mUiSettings;
	private ImageView mBabkBtn;
	private ImageView mToListBtn;

	/**
	 * 初始化AMap对象及其他
	 */
	private void initView() {
		
		(mBabkBtn = (ImageView) findViewById(R.id.backMap))
				.setOnClickListener(this);
		(mToListBtn = (ImageView) findViewById(R.id.maptoListBtn))
				.setOnClickListener(this);
		mSearchBtn = (Button) findViewById(R.id.SearchBtn);
		mSearchBtn.setOnClickListener(listener);
		if (mMListAll.size() == 1) {
			mBabkBtn.setVisibility(Gallery.VISIBLE);
			mToListBtn.setVisibility(Gallery.INVISIBLE);
		} else {
			mBabkBtn.setVisibility(Gallery.VISIBLE);
			mToListBtn.setVisibility(Gallery.VISIBLE);
		}
		mList = new ArrayList<Marker>();
		if (aMap == null) {
			aMap = mapView.getMap();	
			aMap.setOnMapLoadedListener(this);
			setUpMap();
		}
	}

	private void setUpMap() {
		// 设置定位资源，即设置定位监听 ，设置后定位按钮才可以点击
		aMap.setLocationSource(this);
		aMap.setMapType(AMap.MAP_TYPE_NORMAL); // 设置地图模式：标准或卫星
		mUiSettings = aMap.getUiSettings();// 得到地图控件设置对象
		mUiSettings.setZoomControlsEnabled(true);// 设置缩放控件是否显示，默认显示
		mUiSettings.setMyLocationButtonEnabled(true);// 设置定位控件是否显示，默认显示
		aMap.setMyLocationEnabled(true);// 设置定位控件是否可用，默认可用( 是否可触发定位并显示定位层 )
		mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置高德logo显示位置，默认左边
		//mUiSettings.setCompassEnabled(true);// 显示地图默认的指南针
		mUiSettings.setScaleControlsEnabled(true);// 是否显示比例尺
		mUiSettings.setRotateGesturesEnabled(false);//不允许旋转		
		aMap.setOnMarkerClickListener(this);// 标记点击监听
		aMap.setOnCameraChangeListener(this);// 地图拖动监听
		myLocationMarker();// 添加定位标记
	}

	/**
	 * 添加定位标记
	 */
	public void myLocationMarker() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location));// 设置小蓝点的图标
		// myLocationStyle.strokeColor(Color.BLUE);// 设置圆形的边框颜色
		// myLocationStyle.strokeWidth(5);// 设置画的宽度
		aMap.setMyLocationStyle(myLocationStyle);// 把设置好的定位样式添加到地图中
	}

	/**
	 * 在地图上添加marker
	 * 
	 * @param mMList
	 *            要添加图标的商户集合
	 */
	private void addMarkersToMap(ArrayList<Merchant> merchants) {
		ArrayList<LatLng> ls = new ArrayList<LatLng>();
		// 设置图标基点，图标经纬坐标，标题，附加文本（内容），帧动画
		// 图标有近大远小效果，用户可移动标记，刷新一次图片资源的周期
		// marker.setRotateAngle(90);// 设置marker旋转90度
		for (int i = 0; i < merchants.size(); i++) {
			double mLng=0,mLat=0;
			mLat=merchants.get(i).MerchantLat;
			mLng=merchants.get(i).MerchantLng;
			if(Math.abs(mLat)<0.5&&Math.abs(mLng)<0.5){
				continue;
			}
			Marker mMarker = aMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 0.5f)
					.position(new LatLng(mLat, mLng))
					.title(merchants.get(i).MerchantName)
					.snippet(merchants.get(i).MerchantAddress)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.nearby_03))
					.perspective(true).draggable(true));
			mMarker.setObject(merchants.get(i));
			mList.add(mMarker);
			ls.add(new LatLng(merchants.get(i).MerchantLat,
					merchants.get(i).MerchantLng));
		}
		float lv=aMap.getCameraPosition().zoom;
		//System.out.println(lv+"==lv");
		// 改变地图可视位置
		if (ls != null && ls.size() > 1) {
//			CameraPosition cp = new CameraPosition(Util.getCenterPoint(ls), lv,
//					0, 0);
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(Util.getCenterPoint(ls), lv);// 仅改变地图中心的方法
			aMap.animateCamera(cu);
		} else if(ls != null && ls.size() ==1) {
			LatLng l= new LatLng(merchants.get(0).MerchantLat,
					merchants.get(0).MerchantLng);			
			//CameraPosition cp = new CameraPosition(l, lv, 0, 0);
			CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(l, lv);// 仅改变地图中心的方法
			aMap.animateCamera(cu);			
		}

	}

	private PopupWindow mPopupWindow;
	private View mPopupView;

	private Merchant merchant = new Merchant();// 当前点击的商户

	/**
	 * 显示底边的PopupWindow
	 * 
	 * @param marker
	 */
	private void showPopup(Marker marker) {
		merchant = (Merchant) marker.getObject();// 得到点击的商户
		phone = merchant.MerchantTele;
		if (merchant != null) {

			mPopupView = getLayoutInflater().inflate(R.layout.map_popup_main,
					null);
			TextView txtTitel = (TextView) mPopupView
					.findViewById(R.id.map_store_titel);
			txtTitel.setText(merchant.MerchantName);// 设置显示标题

			TextView txtContent = (TextView) mPopupView
					.findViewById(R.id.map_store_content);
			txtContent.setText(merchant.BusinessArea);// 设置显示类容

			mPopupWindow = new PopupWindow(mPopupView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
			mPopupWindow.setTouchable(true);
			mPopupWindow.setOutsideTouchable(true);

			mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);

			mPopupWindow.showAtLocation(mapView, Gravity.BOTTOM, 0, 0);
		}
	}

	private PopupWindow mAPopupWindow;

	private void showAPopup(Marker marker) {
		merchant = (Merchant) marker.getObject();// 得到点击的商户
		phone = merchant.MerchantTele;
		if (merchant != null) {
			View view = getLayoutInflater().inflate(R.layout.map_a_popup, null);
			view.findViewById(R.id.walkBtnA).setOnClickListener(aListener);
			view.findViewById(R.id.routeBtnA).setOnClickListener(aListener);

			mAPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
			mAPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
			mAPopupWindow.setTouchable(true);
			mAPopupWindow.setOutsideTouchable(true);
			mAPopupWindow.showAtLocation(mapView, Gravity.BOTTOM, 0, 0);
		}
	}

	private OnClickListener aListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mGoIntent = new Intent();
			mGoIntent.setClass(MapPatternActivity.this, RouteActivity.class);
			switch (v.getId()) {
			case R.id.walkBtnA:
				mGoIntent.putExtra("WalkOrDriveIndex", 1);
				break;
			case R.id.routeBtnA:
				mGoIntent.putExtra("WalkOrDriveIndex", 0);
				break;
			}
			// 存放商户信息
			Bundle mGoBundle = new Bundle();
			mGoBundle.putSerializable("Merchant", merchant);
			// 存放我的位置
			Double la = aMap.getMyLocation().getLatitude();
			Double lo = aMap.getMyLocation().getLongitude();
			mGoBundle.putDoubleArray("MyLocation", new double[] { la, lo });
			mGoIntent.putExtra("GoBundle", mGoBundle);
			if (lo != null && la != null) {
				startActivity(mGoIntent);
			} else {
				Toast.makeText(MapPatternActivity.this, getString(R.string.no_location),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private PopupWindow mCallPopupWindow;
	private View mCallPopupView;

	/**
	 * 显示拨打电话的PopupWindow
	 * 
	 * @param marker
	 */
	private void showCallPopup(Merchant merchant) {
		mCallPopupView = getLayoutInflater().inflate(R.layout.map_popup_call,
				null);

		TextView pNTxt = (TextView) mCallPopupView
				.findViewById(R.id.phoneNumberTxt);
		pNTxt.setTag(merchant.MerchantTele);
		//设置电话号码 
		String num = getString(R.string.call);
		pNTxt.setText(String.format(num, merchant.MerchantTele));

		mCallPopupWindow = new PopupWindow(mCallPopupView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mCallPopupWindow.setBackgroundDrawable(new ColorDrawable(
				Color.TRANSPARENT));
		mCallPopupWindow.setTouchable(true);
		mCallPopupWindow.setOutsideTouchable(true);

		mCallPopupWindow.showAtLocation(mapView, Gravity.CENTER, 0, 0);
	}

	/******************************************** 点击事件 ********************************************/

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.callBtn:
			if (merchant != null) {
				MapCallTel();
				showCallPopup(merchant);
			}
			break;
		case R.id.goBtn:
			if (merchant != null) {
				MapGoToMerchant();
				Intent mGoIntent = new Intent();
				mGoIntent.setClass(this, RouteActivity.class);
				// 存放商户信息
				Bundle mGoBundle = new Bundle();
				mGoBundle.putSerializable("Merchant", merchant);
				// 存放我的位置
				Double la = aMap.getMyLocation().getLatitude();
				Double lo = aMap.getMyLocation().getLongitude();
				mGoBundle.putDoubleArray("MyLocation", new double[] { la, lo });
				mGoIntent.putExtra("GoBundle", mGoBundle);
				if (lo != null && la != null) {
					startActivity(mGoIntent);
				} else {
					Toast.makeText(this, getString(R.string.no_location),
							Toast.LENGTH_SHORT).show();
				}

			}
			break;
		case R.id.detailsBtn:
			MapMerchantDetail();
			int id = merchant.MerchantId;
			Intent mDIntent = new Intent();
			mDIntent.putExtra("MERCHANTID", id);
			mDIntent.setClass(this, MerchantDetailActivity.class);
			startActivity(mDIntent);
			break;
		case R.id.callOkBtn:
			Uri uri = Uri.parse("tel:" + phone);
			Intent call = new Intent(Intent.ACTION_CALL, uri); // 直接播出电话
			startActivity(call);
			break;
		case R.id.callCancelBtn:
			MapList();
			mCallPopupWindow.dismiss();
			break;
		}
	}

	// -----------------重写avtivity的生命周期方法 ------------------
	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	// -------------------------定位资源重写方法----------------------

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);

			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */

			// 第一个参数是定位provider（定位模式），第二个参数时间最短是2000毫秒，
			// 第三个参数距离间隔单位是米，第四个参数是定位监听者
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 8, this);				
		}

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);// 移除位置管理服务
			mAMapLocationManager.destory();// 销毁位置管理服务
		}
		mAMapLocationManager = null;
		//System.out.println("stop==deactivate。。。。");
	}

	// --------------------------位置监听重写的方法------------------------

	/**
	 * 此方法已废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {		
		if (mListener != null) {
			// 将定位信息显示在地图上
			mListener.onLocationChanged(aLocation);
			mAMapLocationManager.destory();
		}	
		if (mMListAll != null && mMListAll.size() > 0) {		
			addMarkersToMap(mMListAll);// 往地图上添加marker
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
				}
			}, 500);
		}
	}

	public String phone = "110086";

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		cleanABigBitmap(R.drawable.nearby_03);
		if (aMap != null) {
			Merchant m= (Merchant) marker.getObject();
			aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(m.MerchantLat, m.MerchantLng), aMap.getCameraPosition().zoom));
			MapFocus();
			//jumpPoint(marker);
			marker.setIcon(getAmplification(R.drawable.nearby_03_02, 1.25f));
			if (mList.size() == 1) {
				showAPopup(marker);
			} else {
				showPopup(marker);
			}
		}
		
	
		return true;
	}

	/**
	 * 得到一个放大的不失真的BitmapDescriptor
	 * 
	 * @param resourcesId
	 * @param size
	 * @return
	 */
	public BitmapDescriptor getAmplification(int resourcesId, float size) {
		Bitmap b = BitmapFactory.decodeResource(this.getResources(),
				resourcesId);
		// 产生调整大小后的图片
		Bitmap bmp = Util.setBitmapSize(b, (int) (b.getWidth() * size),
				(int) (b.getHeight() * size));
		return BitmapDescriptorFactory.fromBitmap(bmp);
	}

	private void cleanABigBitmap(int imgId) {
		if (mList != null && mList.size() > 0) {
			for (Marker m : mList) {
				m.setIcon(BitmapDescriptorFactory.fromResource(imgId));
			}
		}
	}

	/**
	 * 对正在移动地图事件回调
	 */
	@Override
	public void onCameraChange(CameraPosition arg0) {
		if (mSearchBtn != null) {
			mSearchBtn.setVisibility(View.GONE);
		}
	}

	CameraPosition cameraPosition;

	/**
	 * 对正在移动地图事件回调
	 */
	@Override
	public void onCameraChangeFinish(CameraPosition position) {
		cameraPosition = position;
		mSearchBtn.setVisibility(View.VISIBLE);
		// System.out.println("move。。。。。。");
	}

	// 搜索当前视野商户按钮的监听事件
	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (cameraPosition != null) {			
				float scale =  aMap.getScalePerPixel() / 1000;//得到当前比例
				scale=scale*width/2;//转化成km
				//System.out.println(cameraPosition.target + "==经纬度坐标");
				if(scale<=10){
					//System.out.println(scale+"=当前范围");
					initDataSerche("", 0, 0, 2, cameraPosition.target.latitude,
							cameraPosition.target.longitude, 0, 10,scale);
				}else{
					ShowToast(getString(R.string.near_sarch));
				}
				
			}
		}
	};

	
	/**
	 * 加载收索的数据
	 * 
	 * @return
	 */
	private void initDataSerche(final String KeyWord, final int AreaId,
			final int SortTypeId, final int OrderTypeId, final double Lat,
			final double Lng, final int StartIndex, final int Number,final float Scope) {// 测试数据
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String result = "";
				try {
					NetworkService service = NetworkService
							.getInstance(MapPatternActivity.this);// 获取网路操作实例
					result = service.SearchMerchantList(KeyWord, AreaId,1,
							SortTypeId, OrderTypeId, Lat, Lng, StartIndex,
							Number,Scope);
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
							JSONArray array = obj.optJSONArray("ResponseData");
							if(mMListAll!=null){
								mMListAll.clear();// 清空
							}							
							if (array != null && array.length() > 0) {
								for (int i = 0; i < array.length(); i++) {
									mMListAll.add(Merchant.JsonToSelf(array
											.getJSONObject(i)));
								}
								aMap.clear();// 清除之前所有的标记					
								myLocationMarker();// 把定位标记重新加载上去
								addMarkersToMap(mMListAll);// 往地图上添加收索到的数据
								mSearchBtn.setVisibility(View.INVISIBLE);
							} else {
								ShowToast(getString(R.string.no_more_data));
							}
						}
					} catch (JSONException e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backMap:
			finish();
			break;
		case R.id.maptoListBtn:
			finish();
			break;
		}
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		// 获得影藏的地理位置坐标
		Merchant merchant = (Merchant) marker.getObject();
		final LatLng latLng = new LatLng(merchant.MerchantLat,
				merchant.MerchantLng);
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		// 将屏幕位置和地理坐标（经纬度）进行转换的类
		Projection proj = aMap.getProjection();
		// 根据传入的图位置（经纬度）得到一个屏幕位置
		Point startPoint = proj.toScreenLocation(latLng);
		// 设置偏移点的坐标想x,y
		startPoint.offset(0, -100);
		// 根据转入的屏幕位置返回一个地图位置（经纬度）
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;
		// 定义设置动画效果的实例
		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * latLng.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * latLng.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	@Override
	public void onMapLoaded() {			
	}
}
