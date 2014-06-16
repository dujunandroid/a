package com.boqii.petlifehouse.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Merchant;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.LineTextView;
import com.boqii.petlifehouse.widgets.NetImageView;

public class RecommendActivity extends BaseActivity implements
		OnItemClickListener {

	private Intent mIntent;
	private ProgressBar bar;
	private ArrayList<Ticket> mTicketLst = new ArrayList<Ticket>();
	private ArrayList<Merchant> mMerchantLst = new ArrayList<Merchant>();
	private ListView mRexommendList;
	private Bitmap defaultImg;
	private MerchantAdapter mMAdapter;
	private TicketAdapter mTAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_list);
		initView();
	}

	int type = 0;

	void initView() {
		defaultImg = BitmapFactory.decodeResource(getResources(),
				R.drawable.nopic);
		bar = (ProgressBar) findViewById(R.id.progressRL);
		(mRexommendList = (ListView) findViewById(R.id.rexommendList))
				.setOnItemClickListener(this);
		mIntent = getIntent();
		type = mIntent.getIntExtra("MOrT", 0);
		String url = getIntent().getStringExtra("URL");
		if (!Util.isEmpty(url)) {// 来判断RUl是否为空
			bar.setVisibility(View.VISIBLE);
			switch (type) {
			case 1:// 服务券
				mTAdapter = new TicketAdapter();
				mRexommendList.setAdapter(mTAdapter);
				recommendTicket(url);
				break;
			case 2:// 商户
				mMAdapter = new MerchantAdapter();
				mRexommendList.setAdapter(mMAdapter);
				recommendMerchant(url);
				break;
			}
		}

		findViewById(R.id.backRL).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	// 服务券推荐列表
	private void recommendTicket(final String url) {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(RecommendActivity.this)
							.HttpConnect(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONArray array = obj.optJSONArray("ResponseData");
							for (int i = 0; i < array.length(); i++) {
								mTicketLst.add(Ticket.JsonToSelf(array
										.optJSONObject(i)));
							}
							mTAdapter.setData(mTicketLst);
							mTAdapter.notifyDataSetChanged();
						} else
							ShowToast(obj.optString("ResponseMsg"));
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						bar.setVisibility(View.INVISIBLE);
					}
				}
			}

		});
	}

	// 商户推荐列表
	private void recommendMerchant(final String url) {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					return NetworkService.getInstance(RecommendActivity.this)
							.HttpConnect(new URL(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							// System.out.println(obj);
							JSONArray array = obj.optJSONArray("ResponseData");
							for (int i = 0; i < array.length(); i++) {
								mMerchantLst.add(Merchant.JsonToSelf(array
										.optJSONObject(i)));
							}
							mMAdapter.setData(mMerchantLst);
							mMAdapter.notifyDataSetChanged();
						} else
							ShowToast(obj.optString("ResponseMsg"));
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						bar.setVisibility(View.INVISIBLE);
					}
				}
			}

		});
	}

	class TicketAdapter extends BaseAdapter {
		private ArrayList<Ticket> lst = new ArrayList<Ticket>();

		@Override
		public int getCount() {
			return lst.size();
		}

		@Override
		public Object getItem(int position) {
			return lst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(RecommendActivity.this)
						.inflate(R.layout.ticket_item, null);
				holder.mImageView = (NetImageView) convertView
						.findViewById(R.id.image);
				holder.mTitle = (TextView) convertView.findViewById(R.id.title);
				holder.mBUsinessareal = (TextView) convertView
						.findViewById(R.id.businessarea);
				holder.mDistance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.mPrice = (TextView) convertView.findViewById(R.id.price);
				holder.mOriPrice = (LineTextView) convertView
						.findViewById(R.id.oriPrice);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Ticket ticket = lst.get(position);

			if (ticket != null) {
				holder.mImageView.setImageUrl(ticket.TicketImg, Constants.PATH,
						defaultImg);
				holder.mTitle.setText(ticket.TicketTitle);
				holder.mBUsinessareal.setText(ticket.BusinessArea);
//				holder.mDistance.setText(String.format(
//						getString(R.string.distance_km),
//						Util.GetDistanceToKM(ticket.Distance)));
				holder.mDistance.setVisibility(View.INVISIBLE);
				holder.mPrice.setText(getString(R.string.now_price)
						+ ticket.TicketPrice);
				holder.mOriPrice.setText(getString(R.string.ori_Price)
						+ ticket.TicketOriPrice);
			}
			return convertView;
		}

		public void setData(ArrayList<Ticket> ticketList) {
			this.lst = ticketList;
		}

		public ArrayList<Ticket> getData() {
			return lst;
		}

		public class ViewHolder {
			NetImageView mImageView;
			TextView mTitle;
			TextView mBUsinessareal;
			TextView mDistance;
			TextView mPrice;
			LineTextView mOriPrice;
		}
	}

	class MerchantAdapter extends BaseAdapter {
		private ArrayList<Merchant> lst = new ArrayList<Merchant>();

		@Override
		public int getCount() {
			return lst.size();
		}

		@Override
		public Object getItem(int position) {
			return lst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(RecommendActivity.this)
						.inflate(R.layout.merchant_item, null);
				holder.mImage = (NetImageView) convertView
						.findViewById(R.id.image);
				holder.mTitle = (TextView) convertView.findViewById(R.id.title);
				holder.mTicketIcon = (ImageView) convertView
						.findViewById(R.id.ticket_icon);
				holder.mCheckIcon = (ImageView) convertView
						.findViewById(R.id.check_icon);
				holder.mBusinessarea = (TextView) convertView
						.findViewById(R.id.businessarea);
				holder.mDistance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.mPrice = (TextView) convertView.findViewById(R.id.price);
				holder.mScanNumber = (TextView) convertView
						.findViewById(R.id.scan_number);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Merchant merchant = lst.get(position);

			if (merchant != null) {
				holder.mImage.setImageUrl(merchant.MerchantImg, Constants.PATH,
						defaultImg);
				String[] icon = merchant.Characteristic.split(",");
				for (int i = 0; i < icon.length; i++) {
					// 0,无，1,券，2,疫
					if (icon[i].equals("1")) {
						holder.mCheckIcon.setVisibility(View.GONE);
						holder.mTicketIcon.setVisibility(View.VISIBLE);
					} else if (icon[i].equals("2")) {
						holder.mCheckIcon.setVisibility(View.VISIBLE);
						holder.mTicketIcon.setVisibility(View.GONE);
					} else {
						holder.mCheckIcon.setVisibility(View.GONE);
						holder.mTicketIcon.setVisibility(View.GONE);
					}
				}
				holder.mTitle.setText(merchant.MerchantName);
				holder.mTitle.setTag(merchant.MerchantId);
				holder.mBusinessarea.setText(merchant.BusinessArea);
				// 距离多远的值
				// String distance = String.format(
				// getString(R.string.distance_km),
				// Util.GetDistanceToKM(merchant.MerchantDistance));
				// holder.mDistance.setText(distance);
				holder.mDistance.setVisibility(View.INVISIBLE);
				// 人均消费
				String consumePerPerson = String.format(
						getString(R.string.consumption),
						merchant.ConsumePerPerson);
				holder.mPrice.setText(consumePerPerson);
				// 浏览次数
				String scanNumber = String.format(getString(R.string.scan),
						merchant.ScanNumber);
				holder.mScanNumber.setText(scanNumber);

			}
			return convertView;
		}

		public void setData(ArrayList<Merchant> merchantList) {
			this.lst = merchantList;
		}

		public ArrayList<Merchant> getData() {
			return lst;
		}

		class ViewHolder {
			NetImageView mImage;
			TextView mTitle;
			ImageView mTicketIcon;
			ImageView mCheckIcon;
			TextView mBusinessarea;
			TextView mDistance;
			TextView mPrice;
			TextView mScanNumber;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long arg3) {
		switch (type) {
		case 1:// 服务券
			Intent mTIntent = new Intent();
			int idT = mTicketLst.get(position).TicketId;
			mTIntent.putExtra("TICKETID", idT);
			mTIntent.setClass(RecommendActivity.this,
					TicketDetailActivity.class);
			startActivity(mTIntent);
			break;

		case 2:// 商户
			Intent mMIntent = new Intent();
			int idM = mMerchantLst.get(position).MerchantId;
			mMIntent.putExtra("MERCHANTID", idM);
			mMIntent.setClass(RecommendActivity.this,
					MerchantDetailActivity.class);
			startActivity(mMIntent);
			break;
		}
	}
}
