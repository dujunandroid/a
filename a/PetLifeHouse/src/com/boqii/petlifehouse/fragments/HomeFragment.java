package com.boqii.petlifehouse.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.activities.ListActivity;
import com.boqii.petlifehouse.activities.MainActivity;
import com.boqii.petlifehouse.activities.MerchantDetailActivity;
import com.boqii.petlifehouse.activities.RecommendActivity;
import com.boqii.petlifehouse.activities.SearchActivity;
import com.boqii.petlifehouse.activities.TicketDetailActivity;
import com.boqii.petlifehouse.adapter.MyImageAdapter;
import com.boqii.petlifehouse.baseactivities.BaseFragment;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.AdItem;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.AdGallery;
import com.boqii.petlifehouse.widgets.LineTextView;
import com.boqii.petlifehouse.widgets.NetImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends BaseFragment implements OnClickListener{

	private View view;
	private AdGallery gallery;// 轮播广告
	private ArrayList<AdItem> adItemList = new ArrayList<AdItem>();// 轮播广告数据集合
	private ArrayList<String> imageList = new ArrayList<String>();
	private MyImageAdapter mImageAdapter;
	private LinearLayout dotLayout;// 轮播图片上的记录点
	private Bitmap defaultImg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.home, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {		
		((ImageView) view.findViewById(R.id.image1)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image2)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image3)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image4)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image5)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image6)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image7)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image8)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image9)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.image10)).setOnClickListener(this);
		((ImageView) view.findViewById(R.id.search)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.near_ticket)).setOnClickListener(this);
		dotLayout = (LinearLayout) view.findViewById(R.id.dot_images);
		initGallery(view);
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		new HttpManager(getActivity()).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				// FIXME
				return NetworkService.getInstance(getActivity()).GetHomeData();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							if (getActivity() == null)
								return;
							initBannerList(data.optJSONArray("BannerList"));
							initHotList(data.optJSONArray("HotList"));
							initLowPriceList(data.optJSONArray("LowPriceList"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 填充低价团数据
	 * 
	 * @param array
	 */
	protected void initLowPriceList(JSONArray array) {
		LinearLayout lowLayout = (LinearLayout) view.findViewById(R.id.low);
		if (array != null && array.length() > 0) {
			for (int i = 0; i < Math.min(3, array.length()); i++) {
				Ticket t = Ticket.JsonToSelf(array.optJSONObject(i));
				View v = lowLayout.getChildAt(i);
				initLayoutView(v, t, 2);
			}
			if (array.length() < 3) {
				for (int i = 2; i > array.length() - 1; i--) {
					lowLayout.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}

	}

	private void initLayoutView(View v, Ticket t, int type) {
		((NetImageView) v.findViewById(R.id.image)).setImageUrl(Util.GetImageUrl(t.TicketImg, Util.dip2px(getActivity(), 100), Util.dip2px(getActivity(), 72)), Constants.PATH,
				defaultImg);
		((TextView) v.findViewById(R.id.title)).setText(t.TicketTitle);
		DecimalFormat df=new DecimalFormat("#0.00");
		((TextView) v.findViewById(R.id.price)).setText(getString(
				R.string.home_Price, df.format(t.TicketPrice)));
		((LineTextView) v.findViewById(R.id.oriPrice)).setText(getString(
				R.string.home_Price, df.format(t.TicketOriPrice)));
		((TextView) v.findViewById(R.id.buyed)).setText(getString(
				R.string.buyed, t.TicketBuyed));
		v.setTag(v.getId(), t.TicketId);
		v.setOnClickListener(new Listener(type));
	}

	class Listener implements View.OnClickListener {

		private int type;

		public Listener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			startActivity(new Intent(getActivity(), TicketDetailActivity.class).putExtra("TICKETID", Integer.valueOf(String.valueOf(v.getTag(v.getId())))));
			switch (type) {
			case 1:
				Hot();
				break;
			case 2:
				LowPrice();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 填充热门数据
	 * 
	 * @param array
	 */
	protected void initHotList(JSONArray array) {
		LinearLayout hotLayout = (LinearLayout) view.findViewById(R.id.hot);
		if (array != null && array.length() > 0) {
			for (int i = 0; i < Math.min(3, array.length()); i++) {
				Ticket t = Ticket.JsonToSelf(array.optJSONObject(i));
				View v = hotLayout.getChildAt(i);
				initLayoutView(v, t, 1);
			}
			if (array.length() < 3) {
				for (int i = 2; i > array.length() - 1; i--) {
					hotLayout.getChildAt(i).setVisibility(View.GONE);
				}
			}
		}

	}

	/**
	 * 填充广告数据
	 * 
	 * @param array
	 */
	protected void initBannerList(JSONArray array) {
		adItemList.clear();
		if (array != null && array.length() > 0) {
			for (int i = 0; i < array.length(); i++) {
				adItemList.add(AdItem.JsonToSelf(array.optJSONObject(i)));
			}
			imageList.clear();
			for (AdItem item : adItemList) {
				imageList.add(item.ImageUrl);
			}
			mImageAdapter.notifyDataSetChanged();
			initDotLayout(adItemList.size());
		}
	}

	/**
	 * 根据广告长度初始化指示点
	 * 
	 * @param size
	 */
	private void initDotLayout(int size) {
		dotLayout.removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView dot = new ImageView(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(18, 18);
			lp.leftMargin = Util.dip2px(getActivity(), 5);
			dot.setLayoutParams(lp);
			dot.setImageResource(R.drawable.ic_dot_black);
			dotLayout.addView(dot);
		}
		((ImageView) dotLayout.getChildAt(0)).setImageResource(R.drawable.ic_dot_red);
	}

	/**
	 * 初始化gallery
	 * 
	 * @param view
	 */
	private void initGallery(View view) {
		gallery = (AdGallery) view.findViewById(R.id.gallery);
		for (AdItem item : adItemList) {
			imageList.add(item.ImageUrl);
		}
		mImageAdapter = new MyImageAdapter(getActivity(), imageList);
		gallery.setAdapter(mImageAdapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (adItemList.size() > 0) {
					Banner();
					AdItem adItem = adItemList.get(arg2 % adItemList.size());
					switch (adItem.BannerType) {
					case 1:// TODO 券列表
						Intent mIntentT = new Intent(getActivity(), RecommendActivity.class);
						mIntentT.putExtra("URL", adItem.Url);
						mIntentT.putExtra("MOrT", 1);
						startActivity(mIntentT);
						break;
					case 2:// TODO 商户列表
						Intent mIntentM = new Intent(getActivity(), RecommendActivity.class);
						mIntentM.putExtra("URL", adItem.Url);
						mIntentM.putExtra("MOrT", 2);
						startActivity(mIntentM);
						break;
					case 3:// TODO 券详情
						startActivity(new Intent(getActivity(), TicketDetailActivity.class).putExtra("URL", adItem.Url));
						break;
					case 4:// TODO 商户详情
						startActivity(new Intent(getActivity(), MerchantDetailActivity.class).putExtra("URL", adItem.Url));
						break;

					default:
						break;
					}
				}

			}
		});
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (adItemList.size() > 0)
					setDotImage(arg2 % adItemList.size());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	protected void setDotImage(int arg0) {
		for (int i = 0; i < dotLayout.getChildCount(); i++) {
			if (i == arg0 % dotLayout.getChildCount()) {
				((ImageView) (dotLayout.getChildAt(i))).setImageResource(R.drawable.ic_dot_red);

			} else {
				((ImageView) (dotLayout.getChildAt(i))).setImageResource(R.drawable.ic_dot_black);

			}
		}
	}

	public static final String TYPE_CODE = "Type";
	public static final String TYPE_CATORDOG = "CatOrDog";

	@Override
	public void onClick(View v) {
		Intent mIntent = new Intent();
		switch (v.getId()) {// 狗狗服务-洗护计数
		case R.id.image1:// 洗护
			mIntent.putExtra(TYPE_CODE, 9000);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			// startActivity(new Intent(getActivity(),
			// CategoryDialogViewTest.class));
			DogXihu();
			break;
		case R.id.image2:// 寄养
			mIntent.putExtra(TYPE_CODE, 9002);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			// startActivity(new Intent(getActivity(), PayDemoActivity.class));
			DogJiyang();
			break;
		case R.id.image3:// 造型
			mIntent.putExtra(TYPE_CODE, 9001);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			DogZaoxing();
			break;
		case R.id.image4:// 绝育
			mIntent.putExtra(TYPE_CODE, 9004);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			DogJueyu();
			break;
		case R.id.image5:// 狗狗 更多
			mIntent.putExtra(TYPE_CODE, 900);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			DogGengduo();
			break;
		case R.id.image6:// 猫猫洗护
			mIntent.putExtra(TYPE_CODE, 9007);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			CatXihu();
			break;
		case R.id.image7:// 寄养
			mIntent.putExtra(TYPE_CODE, 9008);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			CatJiyang();
			break;
		case R.id.image8:// 医疗
			mIntent.putExtra(TYPE_CODE, 9011);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			CatYiliao();
			break;
		case R.id.image9:// 绝育
			mIntent.putExtra(TYPE_CODE, 9010);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			CatJueyu();
			break;
		case R.id.image10:// 猫猫更多
			mIntent.putExtra(TYPE_CODE, 800);
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			CatGengduo();
			break;
		case R.id.search:
			startActivity(new Intent(getActivity(), SearchActivity.class));
			break;
		case R.id.near_ticket:
			Near();
			((MainActivity) getActivity()).SetCurrentTab(1);
			break;

		default:
			mIntent.putExtra(TYPE_CODE, 0);			
			mIntent.setClass(getActivity(), ListActivity.class);
			startActivity(mIntent);
			break;
		}

	}

	

}
