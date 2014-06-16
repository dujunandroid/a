package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseFragmentActivity;
import com.boqii.petlifehouse.fragments.ListMerchantFragment;
import com.boqii.petlifehouse.fragments.ListTicketFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class ListActivity extends BaseFragmentActivity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		init();
	}	
	private FragmentManager fragmentManager;// fragment管理对象
	private FragmentTransaction transaction;
	private ListTicketFragment mTicketFragment;// 服务列表
	private ListMerchantFragment mMerchantFragment;// 商户列表
	void init() {		
		fragmentManager = getSupportFragmentManager();
		findViewById(R.id.backListBtn).setOnClickListener(this);
		findViewById(R.id.serchMerchantList).setOnClickListener(this);
		findViewById(R.id.serchTicketList).setOnClickListener(this);
		setTabSelection(0);
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标
	 */
	private void setTabSelection(int index) {
		// 开启一个Fragment事务
		transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 1:
			ListMerchant();
			if (mMerchantFragment == null) {
				mMerchantFragment = new ListMerchantFragment();
				transaction.add(R.id.listContent, mMerchantFragment);
			} else {
				transaction.show(mMerchantFragment);
			}
			break;
		default:
			ListTicket();
			if (mTicketFragment == null) {
				mTicketFragment = new ListTicketFragment();
				transaction.add(R.id.listContent, mTicketFragment);
			} else {
				transaction.show(mTicketFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 隐藏所有的fragment
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (mMerchantFragment != null) {
			transaction.hide(mMerchantFragment);
		}
		if (mTicketFragment != null) {
			transaction.hide(mTicketFragment);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (transaction != null) {
			hideFragments(transaction);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backListBtn:
			finish();
			break;
		case R.id.serchTicketList:
			setTabSelection(0);
			break;
		case R.id.serchMerchantList:
			setTabSelection(1);
			break;
		}

	}

}
