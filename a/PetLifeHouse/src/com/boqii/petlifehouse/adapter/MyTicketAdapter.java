package com.boqii.petlifehouse.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.MyTicketDetailActivity;
import com.boqii.petlifehouse.entities.MyTicket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.NetImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyTicketAdapter extends BaseAdapter {

	private int index;
	private Context context;
	private ArrayList<MyTicket> list;
	private LayoutInflater mInflater;
	private Bitmap defaultImg; 
	public MyTicketAdapter(Context context, ArrayList<MyTicket> list, int index){
		this.index = index;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		defaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.nopic);
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
		NetImageView icon;
		TextView title, price, status, look;
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.myticket_item, null);
			itemView.setTag(this);
			icon = (NetImageView)itemView.findViewById(R.id.image);
			title = (TextView)itemView.findViewById(R.id.title);
			price = (TextView)itemView.findViewById(R.id.price);
			status = (TextView)itemView.findViewById(R.id.status);
			look = (TextView)itemView.findViewById(R.id.look);
		}
		public View getView(int position){
			DecimalFormat df=new DecimalFormat("#0.00");
			MyTicket myticket = list.get(position);
			icon.setImageUrl(Util.GetImageUrl(myticket.MyTicketImg, Util.dip2px(context, 100), Util.dip2px(context, 72)), Constants.PATH, defaultImg);
			title.setText(myticket.MyTicketTitle);
			price.setText(context.getString(R.string.price,df.format(myticket.MyTicketPrice)));
			status.setText(myticket.MyTicketStatus);
			look.setVisibility(View.GONE);
			switch (index) {
			case 1:
				look.setVisibility(View.VISIBLE);
				final int myticketid = myticket.MyTicketId;
				look.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MyTicket t = new MyTicket();
						t.MyTicketId = myticketid;
						context.startActivity(new Intent(context, MyTicketDetailActivity.class).putExtra("MYTICKET", t));
					}
				});
				status.setText(context.getString(R.string.unused));
				break;
			case 2:
				status.setText(context.getString(R.string.used));
				break;
			case 3:
				status.setText(context.getString(R.string.outoftime));
				break;

			default:
				break;
			}
			return itemView;
		}
	}

}
