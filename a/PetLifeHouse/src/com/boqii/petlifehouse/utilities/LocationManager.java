package com.boqii.petlifehouse.utilities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public class LocationManager implements AMapLocationListener{

	
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private MyLocationListener listener;
	
	
	public LocationManager(Context context, MyLocationListener l){
		this.listener = l;
		aMapLocManager = LocationManagerProxy.getInstance(context);
	}
	
	public void StartLocation(){
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
//		aMapLocManager.setGpsEnable(false);
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (aMapLocation == null) {
					listener.Fail();
					stopLocation();// 销毁掉定位
				}
			}
		}, 6000);// 设置超过12秒还没有定位到就停止定位
	}
	
	public void StopLocation(){
		stopLocation();
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			listener.Success(geoLat, geoLng);
//			String cityCode = "";
//			String desc = "";
//			Bundle locBundle = location.getExtras();
//			if (locBundle != null) {
//				cityCode = locBundle.getString("citycode");
//				desc = locBundle.getString("desc");
//			}
//			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//					+ "\n精    度    :" + location.getAccuracy() + "米"
//					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
//					+ AMapUtil.convertToTime(location.getTime()) + "\n城市编码:"
//					+ cityCode + "\n位置描述:" + desc + "\n省:"
//					+ location.getProvince() + "\n市:" + location.getCity()
//					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
//					.getAdCode());
//			myLocation.setText(str);
		}else{
			listener.Fail();
			stopLocation();// 销毁掉定位
		}
	}

	
	public interface MyLocationListener{
		void Success(double lat, double lng);
		void Fail();
	}
	
}
