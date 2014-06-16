package com.boqii.petlifehouse.widgets;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.utilities.CategoryMerchantData;
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

public class CategoryMerchantDialog extends RelativeLayout {
	private LinearLayout category_layout, sub_category_layout, order_layout;
	private JSONArray categoryData;
	private Context context;
	private LayoutInflater mLayoutInflater;
	private CateGoryDialogMerchantCallBack callBack = null;
	private TextView category, order;// 标题类型、排序
	private ArrayList<TextView> lst;

	public CategoryMerchantDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		this.context = context;
		bringToFront();
		closeDiaLog();
	}

	public void execute(CateGoryDialogMerchantCallBack callBack) {
		this.callBack = callBack;
		mLayoutInflater = LayoutInflater.from(context);
		category_layout = (LinearLayout) findViewById(R.id.category_layout);
		sub_category_layout = (LinearLayout) findViewById(R.id.sub_category_merchant_layout);
		order_layout = (LinearLayout) findViewById(R.id.order_layout);
		lst = new ArrayList<TextView>();
		category = (TextView) findViewById(R.id.category);// 分类标题
		order = (TextView) findViewById(R.id.order);// 排序标题
		lst.add(category);
		lst.add(order);
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		category_layout.getLayoutParams().width = width ;
		sub_category_layout.getLayoutParams().width = width / 2;
		order_layout.getLayoutParams().width = width;

		category_layout.bringToFront();
		categoryData = getMerchantCategory();
		createCategoryBaseView();
		createOrderView();
	}

	private void createOrderView() {
		TextView text;
		order_layout.getChildAt(0).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDiaLog();
			}
		});
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
						order.setText(Util.getStrFormatSize(name, 8));
						setTitelColor(lst, 1);
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
			final int typeId = json.optInt("TypeId");
			final String typeName = json.optString("TypeName");
			((TextView) view.findViewById(R.id.name)).setText(typeName);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sub_category_layout.setVisibility(View.VISIBLE);
					// createCategoryChildView(i);
					if (callBack != null) {
						category.setText(Util.getStrFormatSize(typeName, 8));
						setTitelColor(lst, 0);
						callBack.categoryCallback(typeName, typeId);
						setState(view, true);
					}
					closeDiaLog();
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

	// protected void createCategoryChildView(int index) {
	// JSONObject json;
	// View v;
	// TextView view;
	// try {
	// JSONArray array =
	// categoryData.optJSONObject(index).optJSONArray("TypeList");
	// sub_category_layout.removeAllViews();
	// for (int i = 0; i < array.length(); i++) {
	// json = array.optJSONObject(i);
	// mLayoutInflater.inflate(R.layout.category_dialog_view_item,
	// sub_category_layout);
	// v = sub_category_layout.getChildAt(sub_category_layout.getChildCount() -
	// 1);
	// view = (TextView)v.findViewById(R.id.name);
	// final String typeName = json.optString("SubTypeName");
	// view.setText(typeName);
	// final int typeId = json.optInt("SubTypeId");
	// v.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if(callBack != null){
	// callBack.categoryCallback(typeName, typeId);
	// }
	// setState(v, false);
	// closeDiaLog();
	// }
	// });
	// }
	// sub_category_layout.setVisibility(VISIBLE);
	// } catch (Exception e) {
	// }
	// }

	private JSONArray getMerchantCategory() {
		return CategoryMerchantData.getinstance(context).getMerchantCategory();
	}

	public void closeDiaLog() {
		setVisibility(GONE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		closeDiaLog();
		return true;
	}

	public interface CateGoryDialogMerchantCallBack {
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
