package com.boqii.petlifehouse.fragments;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.PayDemoActivity;
import com.boqii.petlifehouse.baseactivities.BaseFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FindFragment extends BaseFragment implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view= inflater.inflate(R.layout.find, container, false);
		view.findViewById(R.id.find).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		
		Intent mIntent=new Intent();
		mIntent.setClass(getActivity(), PayDemoActivity.class);
		startActivity(mIntent);
	}
}
