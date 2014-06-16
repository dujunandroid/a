package com.boqii.petlifehouse.widgets;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.utilities.CategoryData;
import com.boqii.petlifehouse.utilities.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryDialogView extends RelativeLayout {
	private LinearLayout category_layout, sub_category_layout, order_layout;
	private JSONArray categoryData;
	private Context context;
	private LayoutInflater mLayoutInflater;
	private CateGoryDialogCallBack callBack = null;
	private TextView category, order;
	private ArrayList<TextView> lst;

	public CategoryDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		this.context = context;
		bringToFront();
		closeDiaLog();
	}

	public void execute(CateGoryDialogCallBack callBack) {
		this.callBack = callBack;
		mLayoutInflater = LayoutInflater.from(context);
		lst=new ArrayList<TextView>();
		category = (TextView) findViewById(R.id.category);// 分类标题
		order = (TextView) findViewById(R.id.order);// 排序标题
		lst.add(category);
		lst.add(order);
		category_layout = (LinearLayout) findViewById(R.id.category_layout);
		sub_category_layout = (LinearLayout) findViewById(R.id.sub_category_layout);
		order_layout = (LinearLayout) findViewById(R.id.order_layout);
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		category_layout.getLayoutParams().width = width / 2;
		sub_category_layout.getLayoutParams().width = width / 2;
		order_layout.getLayoutParams().width = width;

		category_layout.bringToFront();
		categoryData = getTicketCategory();
		createCategoryBaseView();
		createOrderView();
	}

	private void createOrderView() {
		TextView text;
		// order_layout.getChildAt(0).setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// closeDiaLog();
		// }
		// });
		for (int i = 0; i < order_layout.getChildCount(); i++) {
			text = (TextView) order_layout.getChildAt(i);
			final int orderId = text.getTag() != null ? Integer.parseInt(text
					.getTag().toString()) : 0;
			final String name = text.getText().toString();
			text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (callBack != null) {
						View view = null;
						callBack.orderCallBack(name, orderId);
						order.setText(Util.getStrFormatSize(name, 5));
						setTitelColor(lst, 1);
						// order.setTextColor(getResources().getColor(R.color.text_yellow));
						for (int j = 0; j < order_layout.getChildCount(); j++) {
							view = order_layout.getChildAt(j);
							view.setBackgroundColor(getResources().getColor(
									R.color.greyd1s));
							((TextView) view).setTextColor(getResources()
									.getColor(R.color.TextColorBlack));
						}
						v.setBackgroundColor(Color.WHITE);
						((TextView) v).setTextColor(getResources().getColor(
								R.color.text_yellow));
					}
					closeDiaLog();
				}
			});
		}
	}

	private void createCategoryBaseView() {
		// category_layout.getChildAt(0).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// closeDiaLog();
		// }
		// });
		if (categoryData != null) {
			// if(category_layout.getChildCount() > 1)
			// category_layout.removeViews(1, category_layout.getChildCount() -
			// 1);
			for (int i = 0; i < categoryData.length(); i++) {
				createBaseItemView(i);
			}
		}
	}

	/**
	 * 显示控件
	 * 
	 * @param dialogType
	 *            1：分类 2：排序
	 */
	public void ShowDialog(int dialogType) {
		setVisibility(View.VISIBLE);
		category_layout.setVisibility(View.GONE);
		sub_category_layout.setVisibility(View.GONE);
		order_layout.setVisibility(View.GONE);
		if (dialogType == 1) {
			category_layout.setVisibility(View.VISIBLE);
			sub_category_layout.setVisibility(View.VISIBLE);
		} else if (dialogType == 2) {
			order_layout.setVisibility(View.VISIBLE);
		}
	}

	private void createBaseItemView(final int i) {
		mLayoutInflater.inflate(R.layout.category_dialog_view_item,
				category_layout);
		final LinearLayout view = (LinearLayout) category_layout
				.getChildAt(category_layout.getChildCount() - 1);
		try {
			JSONObject json = categoryData.optJSONObject(i);
			((TextView) view.findViewById(R.id.name)).setText(json
					.optString("TypeName"));
			if(i==category.length()){//隐藏最后一条线
				view.findViewById(R.id.lineCDVI).setVisibility(View.GONE);
			}
			createCategoryChildView(0);
			setState((LinearLayout) category_layout.getChildAt(0), true);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// sub_category_layout.setVisibility(View.VISIBLE);
					createCategoryChildView(i);
					setState(v, true);
				}
			});
		} catch (Exception e) {
		}
	}

	protected void setState(View v, boolean b) {
		View view = null;
		for (int i = 0; i < category_layout.getChildCount(); i++) {
			view = category_layout.getChildAt(i);
			view.setBackgroundColor(getResources().getColor(R.color.greyd1s));
			((TextView) view.findViewById(R.id.name))
					.setTextColor(getResources().getColor(
							R.color.TextColorBlack));
		}
		if (b) {
			v.setBackgroundColor(Color.WHITE);
			((TextView) v.findViewById(R.id.name)).setTextColor(getResources()
					.getColor(R.color.text_yellow));
		}
	}
	
	private void setSubState(View v){
		View subView = null;
		for (int i = 0; i < sub_category_layout.getChildCount(); i++) {
			subView = sub_category_layout.getChildAt(i);
			((TextView) subView.findViewById(R.id.name))
					.setTextColor(getResources().getColor(R.color.text_gray));
		}
		((TextView) v.findViewById(R.id.name)).setTextColor(getResources()
				.getColor(R.color.text_yellow));		
	}
	private int subIdState=0; 
	protected void createCategoryChildView(int index) {
		JSONObject json;
		View v;
		TextView view;
		try {
			JSONArray array = categoryData.optJSONObject(index).optJSONArray(
					"TypeList");			
			sub_category_layout.removeAllViews();	
			for (int i = 0; i < array.length(); i++) {
				json = array.optJSONObject(i);			
				mLayoutInflater.inflate(R.layout.category_dialog_view_item,
						sub_category_layout);

				v = sub_category_layout.getChildAt(sub_category_layout
						.getChildCount() - 1);
				v.setBackgroundColor(Color.WHITE);
				view = (TextView) v.findViewById(R.id.name);
				final String typeName = json.optString("SubTypeName");
				view.setText(typeName);			
				final int typeId = json.optInt("SubTypeId");
				if(typeId==subIdState){
					((TextView) v.findViewById(R.id.name)).setTextColor(getResources()
							.getColor(R.color.text_yellow));
				}
				if(i==array.length()-1){//隐藏最后一条线
					v.findViewById(R.id.lineCDVI).setVisibility(View.GONE);
				}
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callBack != null) {
							category.setText(Util.getStrFormatSize(typeName, 8));
							setTitelColor(lst, 0);
							subIdState=typeId;
							setSubState(v);								
							callBack.categoryCallback(typeName, typeId);
						}
						closeDiaLog();
					}
				});
			}
			sub_category_layout.setVisibility(VISIBLE);
		} catch (Exception e) {
		}
	}

	private JSONArray getTicketCategory() {
		return CategoryData.getinstance(context).getTicketCategory();
	}

	public void closeDiaLog() {
		setVisibility(GONE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		closeDiaLog();
		return true;
	}

	public interface CateGoryDialogCallBack {
		public void categoryCallback(String name, int typeId);

		public void orderCallBack(String name, int typeId);
	}

	// 改变标题颜色
	private void setTitelColor(ArrayList<TextView> lst, int index) {
		for (int i = 0; i < lst.size(); i++) {
			if (i == index) {
				lst.get(i).setTextColor(
						getResources().getColor(R.color.text_yellow));
			} else {
				lst.get(i).setTextColor(
						getResources().getColor(R.color.TextColorBlack));
			}
		}
	}
}
