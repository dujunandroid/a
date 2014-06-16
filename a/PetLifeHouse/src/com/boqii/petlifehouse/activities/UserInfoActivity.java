package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.entities.User;
import com.boqii.petlifehouse.utilities.Util;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		initView();
	}

	private void initView() {
		((ImageView) findViewById(R.id.back)).setOnClickListener(this);
		((Button) findViewById(R.id.logout)).setOnClickListener(this);
		((TextView)findViewById(R.id.account)).setText(getApp().user.UserName);
		((TextView)findViewById(R.id.nickname)).setText(getApp().user.NickName);
		((TextView)findViewById(R.id.sex)).setText(getApp().user.Sex);
		String telephone = getApp().user.Telephone;
		if(!Util.isEmpty(telephone)){
			telephone = telephone.substring(0, 3) + "****" + telephone.substring(7, 11);
			((TextView)findViewById(R.id.telephone)).setText(telephone);
		}else{
			((TextView)findViewById(R.id.telephone)).setText(getString(R.string.unbind));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.logout:
			//TODO
			UserCenterLogout();
			User user = new User();
			getApp().user = user;
			finish();
			break;
		default:
			break;
		}
	}
}
