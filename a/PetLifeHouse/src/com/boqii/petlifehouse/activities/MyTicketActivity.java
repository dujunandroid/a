package com.boqii.petlifehouse.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseFragmentActivity;
import com.boqii.petlifehouse.fragments.MyTicketFragment;

public class MyTicketActivity extends BaseFragmentActivity implements OnClickListener {

	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	private MyTicketFragment unusedFragment, usedFragment, outoftimeFragment;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.myticket);
		initView();
		int index = this.getIntent().getIntExtra("INDEX", 1);
		setMyTabHost(index);
	}

	private void initView() {
		fragmentManager = getSupportFragmentManager();
		((ImageView)findViewById(R.id.back)).setOnClickListener(this);
		((TextView)findViewById(R.id.unused)).setOnClickListener(this);
		((TextView)findViewById(R.id.used)).setOnClickListener(this);
		((TextView)findViewById(R.id.outoftime)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.unused:
			MyTicketListUnused();
			setMyTabHost(1);
			break;
		case R.id.used:
			MyTicketListUsed();
			setMyTabHost(2);
			break;
		case R.id.outoftime:
			MyTicketListOutoftime();
			setMyTabHost(3);
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
		switch (index) {
		case 1:
			((TextView)findViewById(R.id.unused)).setTextColor(Color.argb(255, 252, 74, 0));
			((TextView)findViewById(R.id.unused)).setBackgroundResource(R.drawable.bg_tab_able);
			if(unusedFragment == null){
				unusedFragment = new MyTicketFragment();
				unusedFragment.setArguments(b);
				transaction.add(R.id.content, unusedFragment);
			}else{
				transaction.show(unusedFragment);
			}
			break;
		case 2:
			((TextView)findViewById(R.id.used)).setTextColor(Color.argb(255, 252, 74, 0));
			((TextView)findViewById(R.id.used)).setBackgroundResource(R.drawable.bg_tab_able);
			if(usedFragment == null){
				usedFragment = new MyTicketFragment();
				usedFragment.setArguments(b);
				transaction.add(R.id.content, usedFragment);
			}else{
				transaction.show(usedFragment);
			}
			break;
		case 3:
			((TextView)findViewById(R.id.outoftime)).setTextColor(Color.argb(255, 252, 74, 0));
			((TextView)findViewById(R.id.outoftime)).setBackgroundResource(R.drawable.bg_tab_able);
			if(outoftimeFragment == null){
				outoftimeFragment = new MyTicketFragment();
				outoftimeFragment.setArguments(b);
				transaction.add(R.id.content, outoftimeFragment);
			}else{
				transaction.show(outoftimeFragment);
			}
			break;

		default:
			break;
		}
		transaction.commit();
	}

	private void clearText() {
		((TextView)findViewById(R.id.unused)).setTextColor(R.color.text_gray);
		((TextView)findViewById(R.id.used)).setTextColor(R.color.text_gray);
		((TextView)findViewById(R.id.outoftime)).setTextColor(R.color.text_gray);
		((TextView)findViewById(R.id.unused)).setBackgroundResource(R.drawable.bg_tab_unable);
		((TextView)findViewById(R.id.used)).setBackgroundResource(R.drawable.bg_tab_unable);
		((TextView)findViewById(R.id.outoftime)).setBackgroundResource(R.drawable.bg_tab_unable);
	}

	/**
	 * 隐藏所有的fragment
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (unusedFragment != null) {
			transaction.hide(unusedFragment);
		}
		if (usedFragment != null) {
			transaction.hide(usedFragment);
		}
		if (outoftimeFragment != null) {
			transaction.hide(outoftimeFragment);
		}
	}
}
