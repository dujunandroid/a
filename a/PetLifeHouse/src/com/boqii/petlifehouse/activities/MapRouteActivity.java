package com.boqii.petlifehouse.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.fragments.CarRouteFragment;
import com.boqii.petlifehouse.fragments.WalkRouteFragment;

public class MapRouteActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnRouteSearchListener {

	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;// 地图控件类
	private RouteSearch routeSearch;// 路线查询
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private DriveRouteResult driveRouteResult;
	private ImageView mImgView;
	private ListView mLView;
	private SlidingDrawer mDrawer;
	private TextView mTV;
	private SAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_map);
		mapView = (MapView) findViewById(R.id.mRouteMapView);
		mapView.onCreate(savedInstanceState);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mDrawer = (SlidingDrawer) findViewById(R.id.sliding);
		mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				mImgView.setImageResource(R.drawable.button_route_open);
			}
		});

		mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				mImgView.setImageResource(R.drawable.button_route_close);
			}
		});
		
		findViewById(R.id.backRMBtn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mImgView = (ImageView) findViewById(R.id.imageViewIcon);
		mLView = (ListView) findViewById(R.id.routeLV);
		mTV = (TextView) findViewById(R.id.showTotal);
		adapter=new SAdapter();
		if (aMap == null) {
			aMap = mapView.getMap();
			routeSearch = new RouteSearch(this);
			routeSearch.setRouteSearchListener(this);
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
		// mUiSettings.setCompassEnabled(true);// 显示地图默认的指南针
		mUiSettings.setScaleControlsEnabled(true);// 是否显示比例尺
		aMap.setOnMarkerClickListener(this);// 标记点击监听

		myLocationMarker();// 添加定位标记
		addRoute();
	}

	/**
	 * 添加定位标记
	 */
	public void myLocationMarker() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
		myLocationStyle.strokeWidth(5);// 设置画的宽度
		aMap.setMyLocationStyle(myLocationStyle);// 把设置好的定位样式添加到地图中
	}

	/**
	 * 添加路线
	 */
	public void addRoute() {
		Intent mIntent = getIntent();
		int mode = mIntent.getIntExtra("RouteMode", -1);
		@SuppressWarnings("unchecked")
		ArrayList<LatLonPoint> fromAndToLst = (ArrayList<LatLonPoint>) mIntent
				.getSerializableExtra("FromAndTo");
		RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				fromAndToLst.get(0), fromAndToLst.get(1));
		if (mode == WalkRouteFragment.WALK_MODE) {
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo,
					RouteSearch.WalkDefault);
			routeSearch.calculateWalkRouteAsyn(query);
		} else if (mode == CarRouteFragment.CAR_MODE) {// 将规划添加到地图上
			DriveRouteQuery queryD = new DriveRouteQuery(fromAndTo,
					RouteSearch.WalkDefault, null, null, "");
			routeSearch.calculateDriveRouteAsyn(queryD);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// 此方法以废弃
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

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null) {
			// 将定位信息显示在地图上
			mListener.onLocationChanged(aLocation);
		}
	}

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	// 激活定位
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 第一个参数是定位provider（定位模式），第二个参数时间最短是2000毫秒，
			// 第三个参数距离间隔单位是米，第四个参数是定位监听者
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	// 停止定位
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

	// 标记的点击事件
	@Override
	public boolean onMarkerClick(Marker arg0) {

		return false;
	}

	// 重写生命周期
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int arg1) {
		if (result != null && result.getPaths() != null
				&& result.getPaths().size() > 0) {
			driveRouteResult = result;
			DrivePath drivePath = driveRouteResult.getPaths().get(0);
			aMap.clear();// 清理地图上的所有覆盖物
			DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
					this, aMap, drivePath, driveRouteResult.getStartPos(),
					driveRouteResult.getTargetPos());
			drivingRouteOverlay.removeFromMap();
			drivingRouteOverlay.addToMap();
			drivingRouteOverlay.zoomToSpan();
			if(drivePath!=null){
				List<DriveStep> steps= drivePath.getSteps();				
				ArrayList<String> route=new ArrayList<String>();
				for (int i = 0; i < steps.size(); i++) {
					DriveStep step= steps.get(i);
					route.add(step.getInstruction());
					String allRoute=getResources().getString(R.string.all_route);
					mTV.setText(String.format(allRoute, drivePath.getDistance()));
				}	
				adapter.setData(route, R.drawable.icon_luxian_buxing_highlight);
				mLView.setAdapter(adapter);
				
			}
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int arg1) {
		walkRouteResult = result;
		WalkPath walkPath = walkRouteResult.getPaths().get(0);
		aMap.clear();// 清理地图上的所有覆盖物
		WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap,
				walkPath, walkRouteResult.getStartPos(),
				walkRouteResult.getTargetPos());
		walkRouteOverlay.removeFromMap();
		walkRouteOverlay.addToMap();
		walkRouteOverlay.zoomToSpan();
		if(walkPath!=null){
			List<WalkStep> steps= walkPath.getSteps();				
			ArrayList<String> route=new ArrayList<String>();
			for (int i = 0; i < steps.size(); i++) {
				WalkStep step= steps.get(i);
				route.add(step.getInstruction());
				String allRoute=getResources().getString(R.string.all_route);
				mTV.setText(String.format(allRoute, walkPath.getDistance()));
			}	
			adapter.setData(route, R.drawable.icon_luxian_buxing_highlight);
			mLView.setAdapter(adapter);
			
		}
	}

	class SAdapter extends BaseAdapter {
		ArrayList<String> route;
		int imgId;	

		public void setData(ArrayList<String> route, int imgId) {
			this.route = route;
			this.imgId = imgId;
		}

		@Override
		public int getCount() {
			return route.size();
		}

		@Override
		public Object getItem(int position) {
			return route.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(MapRouteActivity.this)
						.inflate(R.layout.route_sliding_item, null);
				holder.mIV = (ImageView) convertView.findViewById(R.id.imgIV);
				holder.mRTxt = (TextView) convertView.findViewById(R.id.showRouteTxt);
				holder.mLTxt= convertView.findViewById(R.id.line);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.mIV.setImageResource(imgId);
			holder.mRTxt.setText(route.get(position));
//			if(position!=route.size()-1){
//				holder.mLTxt.setVisibility(View.VISIBLE);
//			}									
			return convertView;
		}
		
		public class ViewHolder {
			TextView mRTxt;// 路线
			View mLTxt;// 线
			ImageView mIV;// 图片
		}
	}

}
