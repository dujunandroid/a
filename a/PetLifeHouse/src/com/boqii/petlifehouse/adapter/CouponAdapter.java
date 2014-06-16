package com.boqii.petlifehouse.adapter;

import java.util.ArrayList;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.entities.Coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CouponAdapter extends BaseAdapter {

	private int index;
	private Context context;
	private ArrayList<Coupon> list;
	private LayoutInflater mInflater;
	public CouponAdapter(Context context, ArrayList<Coupon> list, int index){
		this.index = index;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
		ViewHolder holder;
		if(v == null){
			holder = new ViewHolder();
		}else{
			holder = (ViewHolder)v.getTag();
		}
		v = holder.getView(arg0);
		return v;
	}
	
	class ViewHolder{
		View itemView;
		ImageView icon;
		TextView title, price, status;
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.mycoupon_item, null);
			itemView.setTag(this);
			icon = (ImageView)itemView.findViewById(R.id.image);
			title = (TextView)itemView.findViewById(R.id.title);
			price = (TextView)itemView.findViewById(R.id.price);
			status = (TextView)itemView.findViewById(R.id.status);
		}
		public View getView(int position){
			Coupon coupon = list.get(position);
			title.setText(coupon.CouponTitle);
			price.setText(context.getString(R.string.yuan, coupon.CouponPrice));
			switch (index) {
			case 1://未使用
				icon.setBackgroundResource(R.drawable.ic_coupon);
				status.setText(context.getString(R.string.unused));
				break;
			case 2://已使用
				icon.setBackgroundResource(R.drawable.ic_coupon_unable);
				status.setText(context.getString(R.string.used));
				break;
			case 3://已过期
				icon.setBackgroundResource(R.drawable.ic_coupon_unable);
				status.setText(context.getString(R.string.outoftime));
				break;

			default:
				break;
			}
			return itemView;
		}
	}

}
