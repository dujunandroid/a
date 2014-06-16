package com.boqii.petlifehouse.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.fragments.CarRouteFragment;
import com.boqii.petlifehouse.fragments.WalkRouteFragment;

public class RouteActivity extends FragmentActivity {

	private CarRouteFragment mCarFragment;
	private WalkRouteFragment mWalkFragment;
	private FragmentManager fragmentManager;
	private RadioButton mCarRBtn;
	private RadioButton mWalkRBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_routelists);
		fragmentManager = getSupportFragmentManager();
		mWalkRBtn=(RadioButton) findViewById(R.id.walkRouteBtn);
		mCarRBtn=(RadioButton) findViewById(R.id.carRouteBtn);
		int index = getIntent().getIntExtra("WalkOrDriveIndex", 0);
		setTabSelection(index);
	}

	public void onAction(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.walkRouteBtn:
			setTabSelection(1);
			break;
		case R.id.carRouteBtn:
			setTabSelection(0);
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标
	 */
	private void setTabSelection(int index) {
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 1:
			if (mWalkFragment == null) {
				mWalkFragment = new WalkRouteFragment();
				transaction.add(R.id.route_frame, mWalkFragment);
			} else {
				transaction.show(mWalkFragment);
			}
			mWalkRBtn.setBackgroundResource(R.drawable.walk_route);
			mWalkRBtn.setChecked(true);
			break;
		default:
			if (mCarFragment == null) {
				mCarFragment = new CarRouteFragment();
				transaction.add(R.id.route_frame, mCarFragment);
			} else {
				transaction.show(mCarFragment);
			}
			mCarRBtn.setBackgroundResource(R.drawable.drive_route);
			mCarRBtn.setChecked(true);
			break;
		}
		// 提交事务
		transaction.commit();
		// System.out.println("commit tansaction.....");
	}

	/**
	 * 隐藏所有的fragment
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (mWalkFragment != null) {
			transaction.hide(mWalkFragment);
		}
		if (mCarFragment != null) {
			transaction.hide(mCarFragment);
		}
	}
}
