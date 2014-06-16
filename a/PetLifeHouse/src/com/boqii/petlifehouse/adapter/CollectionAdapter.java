package com.boqii.petlifehouse.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.entities.Collection;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.NetImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectionAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Collection> list;
	private LayoutInflater mInflater;
	private DeleteListener listener;
	private Bitmap defaultImg;
	public CollectionAdapter(Context context, ArrayList<Collection> list, DeleteListener l){
		this.listener = l;
		this.context = context;
		defaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.nopic);
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
		NetImageView icon;
		TextView title, price, oriPrice, delete,status;
		Button sure, cancel;
		LinearLayout delete_layout;
		public ViewHolder(){
			itemView = mInflater.inflate(R.layout.mycollection_item, null);
			itemView.setTag(this);
			icon = (NetImageView)itemView.findViewById(R.id.image);
			title = (TextView)itemView.findViewById(R.id.title);
			price = (TextView)itemView.findViewById(R.id.price);
			oriPrice = (TextView)itemView.findViewById(R.id.oriprice);
			status = (TextView)itemView.findViewById(R.id.status);
			delete = (TextView)itemView.findViewById(R.id.delete);
			sure = (Button)itemView.findViewById(R.id.sure);
			cancel = (Button)itemView.findViewById(R.id.cancel);
			delete_layout = (LinearLayout)itemView.findViewById(R.id.delete_layout);
		}
		
		public View getView(final int position){
			DecimalFormat df=new DecimalFormat("#0.00");
			Collection c = list.get(position);
			icon.setImageUrl(Util.GetImageUrl(c.ticket.TicketImg, Util.dip2px(context, 100), Util.dip2px(context, 72)), Constants.PATH, defaultImg);
			title.setText(c.ticket.TicketTitle);
			price.setText(context.getString(R.string.price, df.format(c.ticket.TicketPrice)));
			oriPrice.setText(context.getString(R.string.price, df.format(c.ticket.TicketOriPrice)));
			status.setText(c.TicketStatus);
			delete_layout.setVisibility(View.GONE);
			delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					delete_layout.setVisibility(View.VISIBLE);
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					delete_layout.setVisibility(View.GONE);
				}
			});
			sure.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(listener != null)
						listener.Delete(position);
				}
			});
			return itemView;
		}
	}
	
	public void HideLayout(int position){
		View itemView = getView(position, null, null);
		((LinearLayout)itemView.findViewById(R.id.delete_layout)).setVisibility(View.GONE);
	}

	public interface DeleteListener{
		void Delete(int position);
	}
}
