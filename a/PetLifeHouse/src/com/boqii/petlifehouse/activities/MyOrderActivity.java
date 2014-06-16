package com.boqii.petlifehouse.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseFragmentActivity;
import com.boqii.petlifehouse.fragments.MyOrderFragment;

public class MyOrderActivity extends BaseFragmentActivity implements OnClickListener {

	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	private MyOrderFragment allFragment, unpayFragment, paiedFragment, refundFragment, refundedFragment;
	private TextView line1,line2,line3,line4;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.myorder);
		initView();
		int index = this.getIntent().getIntExtra("INDEX", 1);
		setMyTabHost(index);
	}

	private void initView() {
		fragmentManager = getSupportFragmentManager();
		line1 = (TextView)findViewById(R.id.line1);
		line2 = (TextView)findViewById(R.id.line2);
		line3 = (TextView)findViewById(R.id.line3);
		line4 = (TextView)findViewById(R.id.line4);
		((ImageView)findViewById(R.id.back)).setOnClickListener(this);
		((TextView)findViewById(R.id.all)).setOnClickListener(this);
		((TextView)findViewById(R.id.unpay)).setOnClickListener(this);
		((TextView)findViewById(R.id.paied)).setOnClickListener(this);
		((TextView)findViewById(R.id.refund)).setOnClickListener(this);
		((TextView)findViewById(R.id.refunded)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			startActivity(new Intent(this, MainActivity.class).putExtra("INDEX", 2).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case R.id.all:
			OrderListAll();
			setMyTabHost(1);
			break;
		case R.id.unpay:
			OrderListUnpay();
			setMyTabHost(2);
			break;
		case R.id.paied:
			OrderListPayed();
			setMyTabHost(3);
			break;
		case R.id.refund:
			OrderListRefunding();
			setMyTabHost(4);
			break;
		case R.id.refunded:
			OrderListRefunded();
			setMyTabHost(5);
			break;

		default:
			break;
		}
	}
	
	private void setMyTabHost(int index){
		transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		clearText();
		Bundle b = new Bundle();
		b.putInt("INDEX", index);
		Drawable drawable;
		
		switch (index) {
		case 1:
			line1.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.all)).setTextColor(Color.argb(255, 252, 74, 0));
			drawable = getResources().getDrawable(R.drawable.ic_order_all_r);  
			drawable.setBounds(0, 0, 24, 24);
			((TextView)findViewById(R.id.all)).setBackgroundColor(Color.WHITE);
			//((TextView)findViewById(R.id.all)).setCompoundDrawables(null, drawable, null, null);
			if(allFragment == null){
				allFragment = new MyOrderFragment();
				allFragment.setArguments(b);
				transaction.add(R.id.content, allFragment);
			}else{
				transaction.show(allFragment);
			}
			break;
		case 2:
			line1.setVisibility(View.INVISIBLE);
			line2.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.unpay)).setTextColor(Color.argb(255, 252, 74, 0));
			drawable = getResources().getDrawable(R.drawable.ic_order_unpay_r);  
			drawable.setBounds(0, 0, 24, 24);
			((TextView)findViewById(R.id.unpay)).setBackgroundColor(Color.WHITE);
			//((TextView)findViewById(R.id.unpay)).setCompoundDrawables(null, drawable, null, null);
			if(unpayFragment == null){
				unpayFragment = new MyOrderFragment();
				unpayFragment.setArguments(b);
				transaction.add(R.id.content, unpayFragment);
			}else{
				transaction.show(unpayFragment);
			}
			break;
		case 3:
			line2.setVisibility(View.INVISIBLE);
			line3.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.paied)).setTextColor(Color.argb(255, 252, 74, 0));
			drawable = getResources().getDrawable(R.drawable.ic_order_paied_r);  
			drawable.setBounds(0, 0, 24, 24);
			((TextView)findViewById(R.id.paied)).setBackgroundColor(Color.WHITE);
			//((TextView)findViewById(R.id.paied)).setCompoundDrawables(null, drawable, null, null);
			if(paiedFragment == null){
				paiedFragment = new MyOrderFragment();
				paiedFragment.setArguments(b);
				transaction.add(R.id.content, paiedFragment);
			}else{
				transaction.show(paiedFragment);
			}
			break;
		case 4:
			line3.setVisibility(View.INVISIBLE);
			line4.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.refund)).setTextColor(Color.argb(255, 252, 74, 0));
			drawable = getResources().getDrawable(R.drawable.ic_order_refund_r);  
			drawable.setBounds(0, 0, 24, 24);
			((TextView)findViewById(R.id.refund)).setBackgroundColor(Color.WHITE);
			//((TextView)findViewById(R.id.refund)).setCompoundDrawables(null, drawable, null, null);
			if(refundFragment == null){
				refundFragment = new MyOrderFragment();
				refundFragment.setArguments(b);
				transaction.add(R.id.content, refundFragment);
			}else{
				transaction.show(refundFragment);
			}
			break;
		case 5:
			line4.setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.refunded)).setTextColor(Color.argb(255, 252, 74, 0));
			drawable = getResources().getDrawable(R.drawable.ic_order_refund_r);  
			drawable.setBounds(0, 0, 24, 24);
			((TextView)findViewById(R.id.refunded)).setBackgroundColor(Color.WHITE);
			//((TextView)findViewById(R.id.refunded)).setCompoundDrawables(null, drawable, null, null);
			if(refundedFragment == null){
				refundedFragment = new MyOrderFragment();
				refundedFragment.setArguments(b);
				transaction.add(R.id.content, refundedFragment);
			}else{
				transaction.show(refundedFragment);
			}
			break;

		default:
			break;
		}
		transaction.commit();
	}

	private void clearText() {
		((TextView)findViewById(R.id.all)).setTextColor(Color.parseColor("#333333"));
		((TextView)findViewById(R.id.unpay)).setTextColor(Color.parseColor("#333333"));
		((TextView)findViewById(R.id.paied)).setTextColor(Color.parseColor("#333333"));
		((TextView)findViewById(R.id.refund)).setTextColor(Color.parseColor("#333333"));
		((TextView)findViewById(R.id.refunded)).setTextColor(Color.parseColor("#333333"));
		
		((TextView)findViewById(R.id.all)).setBackgroundColor(Color.argb(255, 209, 209, 209));
		((TextView)findViewById(R.id.unpay)).setBackgroundColor(Color.argb(255, 209, 209, 209));
		((TextView)findViewById(R.id.paied)).setBackgroundColor(Color.argb(255, 209, 209, 209));
		((TextView)findViewById(R.id.refund)).setBackgroundColor(Color.argb(255, 209, 209, 209));
		((TextView)findViewById(R.id.refunded)).setBackgroundColor(Color.argb(255, 209, 209, 209));

		line1.setVisibility(View.VISIBLE);
		line2.setVisibility(View.VISIBLE);
		line3.setVisibility(View.VISIBLE);
		line4.setVisibility(View.VISIBLE);
		
//		Drawable drawable;
//		drawable = getResources().getDrawable(R.drawable.ic_order_all_b);  
//		drawable.setBounds(0, 0, 24, 24);
//		((TextView)findViewById(R.id.all)).setCompoundDrawables(null, drawable, null, null);
//		drawable = getResources().getDrawable(R.drawable.ic_order_unpay_b);  
//		drawable.setBounds(0, 0, 24, 24);
//		((TextView)findViewById(R.id.unpay)).setCompoundDrawables(null, drawable, null, null);
//		drawable = getResources().getDrawable(R.drawable.ic_order_paied_b);  
//		drawable.setBounds(0, 0, 24, 24);
//		((TextView)findViewById(R.id.paied)).setCompoundDrawables(null, drawable, null, null);
//		drawable = getResources().getDrawable(R.drawable.ic_order_refund_b);  
//		drawable.setBounds(0, 0, 24, 24);
//		((TextView)findViewById(R.id.refund)).setCompoundDrawables(null, drawable, null, null);
//		drawable = getResources().getDrawable(R.drawable.ic_order_refunded_b);  
//		drawable.setBounds(0, 0, 24, 24);
//		((TextView)findViewById(R.id.refunded)).setCompoundDrawables(null, drawable, null, null);
		
	}

	/**
	 * 隐藏所有的fragment
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (allFragment != null) {
			transaction.hide(allFragment);
		}
		if (unpayFragment != null) {
			transaction.hide(unpayFragment);
		}
		if (paiedFragment != null) {
			transaction.hide(paiedFragment);
		}
		if (refundFragment != null) {
			transaction.hide(refundFragment);
		}
		if (refundedFragment != null) {
			transaction.hide(refundedFragment);
		}
	}
}
