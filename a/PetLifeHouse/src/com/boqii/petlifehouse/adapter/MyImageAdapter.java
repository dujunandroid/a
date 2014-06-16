package com.boqii.petlifehouse.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.NetImageView;

public class MyImageAdapter extends BaseAdapter{

	private ArrayList<String> imageItemsList;
	private Context context;
	private Bitmap defaultImg;
	private int windowWidth;
	public MyImageAdapter(Context c,ArrayList<String> list){
		this.context = c;
		this.imageItemsList = list;
		windowWidth = ((Activity)c).getWindowManager().getDefaultDisplay().getWidth();
		defaultImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.nopic);
	}
	
	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		if(imageItemsList.size() > 0)
			return imageItemsList.get(position % imageItemsList.size());
		else
			return "";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public String getItemUrl(int position) {
		if(imageItemsList.size() > 0){
			String url = String.valueOf(getItem(position));
			return Util.GetImageUrl(url, windowWidth, Util.dip2px(context, 180));
		}
		return "";
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NetImageView i = new NetImageView(context);
		i.setImageUrl(getItemUrl(position), Constants.PATH, defaultImg);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

}
