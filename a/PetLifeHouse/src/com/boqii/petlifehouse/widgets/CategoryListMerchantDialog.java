package com.boqii.petlifehouse.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.entities.Area;
import com.boqii.petlifehouse.utilities.CategoryAreaData;
import com.boqii.petlifehouse.utilities.CategoryMerchantData;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.LetterSideBar.ILetterIndexer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryListMerchantDialog extends RelativeLayout {
	private LinearLayout category_layout, sub_category_layout, order_layout,
			area_layout;
	private JSONArray categoryData;
	private JSONArray areaData;
	private Context context;
	private LayoutInflater mLayoutInflater;
	private CateGoryDialogMerchantCallBack callBack = null;
	private ListView mAreaLView;
	private ListView mSubAreaLView;
	private LetterSideBar sideBar;// 字母排序控件
	private TextView category, order, area;// 标题类型、排序、区域
	private ArrayList<TextView> lst;

	public CategoryListMerchantDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode())
			return;
		this.context = context;
		bringToFront();
		closeDiaLog();
	}

	public void execute(CateGoryDialogMerchantCallBack callBack) {
		this.callBack = callBack;
		GetLatterStr();
		lst = new ArrayList<TextView>();
		category = (TextView) findViewById(R.id.category);
		lst.add(category);
		order = (TextView) findViewById(R.id.order);
		lst.add(order);
		area = (TextView) findViewById(R.id.area);
		lst.add(area);

		mLayoutInflater = LayoutInflater.from(context);
		category_layout = (LinearLayout) findViewById(R.id.category_layout);
		sub_category_layout = (LinearLayout) findViewById(R.id.sub_category_merchant_layout);

		area_layout = (LinearLayout) findViewById(R.id.area_layout);
		(mAreaLView = (ListView) findViewById(R.id.areaList))
				.setOnItemClickListener(areaOnItemClick);
		(mSubAreaLView = (ListView) findViewById(R.id.subAreaList))
				.setOnItemClickListener(SubAreaOnItemClick);
		areaAdapter = new AreaAdapter();
		subAreaAdapter = new SubAreaAdapter();
		mSubAreaLView.setAdapter(subAreaAdapter);
		(sideBar = (LetterSideBar) findViewById(R.id.areaIndex))
				.setListView(mSubAreaLView);

		order_layout = (LinearLayout) findViewById(R.id.order_layout);
		int width = ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();
		category_layout.getLayoutParams().width = width;
		sub_category_layout.getLayoutParams().width = width / 2;
		area_layout.getLayoutParams().width = width;
		order_layout.getLayoutParams().width = width;
		mAreaLView.getLayoutParams().width = width /3;
		mSubAreaLView.getLayoutParams().width=width/3*2;
		category_layout.bringToFront();
		categoryData = getMerchantCategory();
		areaData = getAreaCateGory();
		createCategoryBaseView();
		createAreaView();// 创建地区视图
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
						order.setText(Util.getStrFormatSize(name, 5));
						setTitelColor(lst, 1);
						callBack.orderCallBack(name, orderId);
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

	private void createAreaView() {
		// area_layout.getChildAt(0).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// closeDiaLog();
		// }
		// });
		if (areaData != null) {
			areaAdapter.setData(areaData);
			mAreaLView.setAdapter(areaAdapter);
			createSubArea(0);
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
			// if (category_layout.getChildCount() > 1)
			// category_layout.removeViews(1,
			// category_layout.getChildCount() - 1);
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
		category_layout.setVisibility(View.INVISIBLE);
		sub_category_layout.setVisibility(View.INVISIBLE);
		area_layout.setVisibility(View.INVISIBLE);
		order_layout.setVisibility(View.INVISIBLE);
		if (dialogType == 1) {
			category_layout.setVisibility(View.VISIBLE);
		} else if (dialogType == 2) {
			order_layout.setVisibility(View.VISIBLE);
		} else if (dialogType == 3) {
			area_layout.setVisibility(View.VISIBLE);
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
					setState(view, true);
					if (callBack != null) {
						category.setText(Util.getStrFormatSize(typeName, 5));
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

	private JSONArray getAreaCateGory() {// 得到地区
		return CategoryAreaData.getinstance(context).getAreaCategory();
	}

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

		public void AreaCallBack(String areaName, int areaCityId,int type);
	}

	public AreaAdapter areaAdapter;

	/**
	 * 主地区适配器
	 */
	class AreaAdapter extends BaseAdapter {
		JSONArray array = new JSONArray();

		public void setData(JSONArray array) {
			this.array = array;
		}

		public JSONArray getData() {
			return array;
		}

		@Override
		public int getCount() {
			return array.length();
		}

		@Override
		public Object getItem(int position) {
			return array.optJSONArray(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.category_dialog_view_item, null);
				holder.mAreaTxt = (TextView) convertView
						.findViewById(R.id.name);
				holder.mLine=(TextView) convertView.findViewById(R.id.lineCDVI);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mLine.setVisibility(View.GONE);
			JSONObject obj = array.optJSONObject(position);
			String name = obj.optString("AreaName");
			if(viewLayout==null){
				holder.mAreaTxt.setTextColor(getResources().getColor(R.color.text_yellow));
				convertView.setBackgroundColor(Color.WHITE);
				viewLayout=convertView;
			}
			holder.mAreaTxt.setText(name);

			return convertView;
		}

		public class ViewHolder {
			TextView mAreaTxt;// 城区名称
			TextView mLine;
		}
	}

	public View viewLayout;// 点击的项
	public SubAreaAdapter subAreaAdapter;
	// 父类大地区的点击事件
	private OnItemClickListener areaOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if (viewLayout != null) {
				viewLayout.setBackgroundColor(getResources().getColor(
						R.color.greyd1s));
				((TextView) viewLayout.findViewById(R.id.name))
						.setTextColor(getResources().getColor(
								R.color.TextColorBlack));
			}
			view.setBackgroundColor(Color.WHITE);
			((TextView) view.findViewById(R.id.name))
					.setTextColor(getResources().getColor(R.color.text_yellow));
			viewLayout = view;// 记录选中的项
			createSubArea(position);
		}
	};

	// 创建子地区项的方法
	private void createSubArea(final int position) {
		JSONObject obj = (JSONObject) areaAdapter.getData().optJSONObject(
				position);// 得到点击项的数据
		JSONArray subArray = obj.optJSONArray("AreaList");// 得到点击项的子项数据
		ArrayList<Area> list = new ArrayList<Area>();
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
		String latter = "";// 排序标识
		if (subArray != null && subArray.length() > 0) {// 解析数据
			for (int i = 0; i < subArray.length(); i++) {
				//System.out.println(subArray);
				Area area = new Area();
				JSONObject subObj = subArray.optJSONObject(i);
				area.AreaId = subObj.optInt("SubAreaId");
				area.AreaName = subObj.optString("SubAreaName");
				area.Type=subObj.optInt("Type",2);				
				if (area.AreaId == 0) {
					latter = area.AreaName;
				}
				if (!hashMap.containsKey(latter)) {
					hashMap.put(latter, i);
				}
				list.add(area);
			}
			if (position != 0) {
				sideBar.setVisibility(View.GONE);
			} else {
				sideBar.setVisibility(View.VISIBLE);
			}
			subAreaAdapter.setData(list, hashMap);
			subAreaAdapter.setState(subAreaId);
			subAreaAdapter.notifyDataSetChanged();
		}
	}

	HashMap<String,String> hashMapL=new HashMap<String, String>();
	private  HashMap<String,String> GetLatterStr(){
		String[] letterStr= LetterSideBar.arrays;
		for (int i = 0; i < letterStr.length; i++) {
			hashMapL.put(letterStr[i], letterStr[i]);
		}
		
		return hashMapL;
	}
	
	// 小地区的适配器
	class SubAreaAdapter extends BaseAdapter implements ILetterIndexer {
		ArrayList<Area> array = new ArrayList<Area>();
		HashMap<String, Integer> hashMap;
		int stateAreaId=0;
		public void setData(ArrayList<Area> array,
				HashMap<String, Integer> hashMap) {
			this.array = array;
			this.hashMap = hashMap;
		}

		public ArrayList<Area> getData() {
			return array;
		}

		@Override
		public int getCount() {

			return array.size();
		}

		@Override
		public Object getItem(int position) {
			return array.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.category_dialog_view_item, null);
				holder.mAreaTxt = (TextView) convertView
						.findViewById(R.id.name);
				holder.mLine=(TextView) convertView.findViewById(R.id.lineCDVI);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mLine.setVisibility(View.GONE);
			Area area = array.get(position);
			if (!hashMap.containsKey(String.valueOf(area.AreaName))) {
				hashMap.put(String.valueOf(area.AreaName), position);
			}
			if(stateAreaId==area.AreaId){
				holder.mAreaTxt.setTextColor(getResources().getColor(R.color.text_yellow));
			}else{
				holder.mAreaTxt.setTextColor(getResources().getColor(R.color.text_gray));
			}			
			convertView.setBackgroundColor(Color.WHITE);
			holder.mAreaTxt.setText(area.AreaName);
			if (area.AreaId == 0) {
				convertView.setEnabled(false);
			}
			return convertView;
		}

		public class ViewHolder {
			TextView mAreaTxt;// 城区名称
			TextView mLine;
		}

		@Override
		public int getPositionForSection(String section) {
			if (!TextUtils.isEmpty(section)) {
				if (hashMap.containsKey(section)) {
					return hashMap.get(section);
				}
			}
			return -1;
		}
		
		public void setState(int AreaId){
			this.stateAreaId=AreaId;
		}
	}

	
	private int subAreaId=-1;
	private OnItemClickListener SubAreaOnItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			Area a = (Area) subAreaAdapter.getItem(position);
			if (a != null) {
				if(hashMapL.containsKey(a.AreaName)){
					return;
				}			
				subAreaId=a.AreaId;
				subAreaAdapter.setState(subAreaId);
				subAreaAdapter.notifyDataSetChanged();
				area.setText(Util.getStrFormatSize(a.AreaName, 5));
				setTitelColor(lst, 2);
				callBack.AreaCallBack(a.AreaName, a.AreaId,a.Type);				
				closeDiaLog();
			}

		}

	};

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
