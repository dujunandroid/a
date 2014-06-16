package com.boqii.petlifehouse.fragments;

import java.util.ArrayList;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.MapPatternActivity;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.entities.Merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class NearFragment extends BaseFragment implements
		android.widget.CompoundButton.OnCheckedChangeListener, OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 用于两个fragment间传值
	 * 
	 * @param bundle
	 *            传递的值封装成的bundle
	 * @return
	 */
	public static NearFragment newInstance(Bundle bundle) {
		NearFragment nearFragment = new NearFragment();
		bundleNearFragment = bundle;
		return nearFragment;
	}

	private static Bundle bundleNearFragment = new Bundle();
	public static String RESULT_CODE = "MerchantList";
	private View view;
	private NearMerchantFragment mMerchantFragment;// 商户Fragment列表
	private NearTicketFragment mTicketFragment;// 服务券Fragment列表
	private FragmentManager fragmentManager;// fragment管理对象
	private View vImage;// 图片

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.near, container, false);
		init();
		return view;
	}

	void init() {
		fragmentManager = getFragmentManager();
		vImage = view.findViewById(R.id.mapPatternBtn);
		view.findViewById(R.id.mapPatternBtn).setOnClickListener(this);
		((RadioButton) view.findViewById(R.id.nearMerchant))
				.setOnCheckedChangeListener(this);
		((RadioButton) view.findViewById(R.id.nearTicket))
				.setOnCheckedChangeListener(this);
		setTabSelection(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nearMerchant:
			NearMerchant();
			setTabSelection(1);
			break;
		case R.id.nearTicket:
			NearTicket();
			setTabSelection(0);			
			break;
		case R.id.mapPatternBtn:
			MapIcon();
			ArrayList<Merchant> lst = new ArrayList<Merchant>();
			lst = (ArrayList<Merchant>) bundleNearFragment
					.getSerializable(RESULT_CODE);
			if (null != lst && lst.size() > 0) {
				Intent mIntent = new Intent();
				mIntent.setClass(getActivity(), MapPatternActivity.class);
				mIntent.putExtras(bundleNearFragment);
				startActivity(mIntent);
			} else {
				Toast.makeText(getActivity(), R.string.no_merchant,
						Toast.LENGTH_SHORT).show();
			}
			break;
		}

	}

	FragmentTransaction transaction;

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
			vImage.setVisibility(View.VISIBLE);
			if (mMerchantFragment == null) {
				mMerchantFragment = new NearMerchantFragment();
				transaction.add(R.id.nearContent, mMerchantFragment);
			} else {
				transaction.show(mMerchantFragment);
			}
			break;
		default:
			vImage.setVisibility(View.INVISIBLE);
			if (mTicketFragment == null) {
				mTicketFragment = new NearTicketFragment();
				transaction.add(R.id.nearContent, mTicketFragment);
			} else {
				transaction.show(mTicketFragment);
			}
			break;
		}
		// 提交事务
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
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		if (isChecked) {
			switch (view.getId()) {
			case R.id.nearTicket:
				setTabSelection(0);
				vImage.setVisibility(View.INVISIBLE);
				break;
			case R.id.nearMerchant:
				setTabSelection(1);
				vImage.setVisibility(View.VISIBLE);
				break;
			}
		}

	}

}
