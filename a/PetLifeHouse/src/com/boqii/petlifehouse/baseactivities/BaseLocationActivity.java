package com.boqii.petlifehouse.baseactivities;

import android.os.Bundle;

import com.boqii.petlifehouse.utilities.LocationManager;
import com.boqii.petlifehouse.utilities.LocationManager.MyLocationListener;

public class BaseLocationActivity extends BaseActivity implements MyLocationListener {

	private LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.StopLocation();// 停止定位
	}

	private void initLocation() {
		mLocationManager = new LocationManager(this, this);
		mLocationManager.StartLocation();
	}

	@Override
	public void Success(double lat, double lng) {
		mLocationManager.StopLocation();
	}

	@Override
	public void Fail() {
	}
	
}
