package com.boqii.petlifehouse.activities;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.boqii.petlifehouse.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchActivity extends Activity implements OnClickListener, OnFocusChangeListener, TextWatcher {

	private ListView listview;
	private EditText keyEdit;
	private LayoutInflater mInflater;
	private SearchAdapter adapter;
	private ArrayList<String> history = new ArrayList<String>();
	private boolean click = false;
	private ImageView mViewClean;
	private boolean isAddFoot = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		history = getHistory();
		initView();
	}

	@Override
	protected void onResume() {
		click = false;
		super.onResume();
	}

	private void saveHistory() {
		String text = keyEdit.getText().toString();
		if (history != null && history.size() > 0) {
			if (history.contains(text)) {
				return;
			} else {
				history.add(0, text);
				adapter.notifyDataSetChanged();
				editCommit(text);
			}
		} else {
			history.add(0, text);
			adapter.notifyDataSetChanged();
			editCommit(text);
		}
	}

	private void editCommit(String text) {
		SharedPreferences sp = getSharedPreferences("history_strs", 0);
		String save_Str = sp.getString("history", "");
		StringBuilder sb = new StringBuilder(save_Str);
		sb.append(text + ",");
		sp.edit().putString("history", sb.toString()).commit();
	}

	private ArrayList<String> getHistory() {
		ArrayList<String> list = new ArrayList<String>();
		SharedPreferences sp = getSharedPreferences("history_strs", MODE_PRIVATE);
		String save_history = sp.getString("history", null);
		if (!TextUtils.isEmpty(save_history)) {
			String[] hisArrays = save_history.split(",");
			if (hisArrays != null && hisArrays.length > 0) {
				for (int i = hisArrays.length; i > 0; i--) {
					list.add(hisArrays[i - 1]);
				}
			}
		}
		return list;
	}

	private void cleanHistory() {
		SharedPreferences sp = getSharedPreferences("history_strs", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		history.clear();
		adapter.notifyDataSetChanged();
	}

	public void collapseSoftInputMethod(EditText inputText) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
		if (isAddFoot == false) {
			listview.addFooterView(ll);
			listview.setAdapter(adapter);
		}
		saveHistory();
		startActivity(new Intent(SearchActivity.this, ListActivity.class).putExtra("SearchKey", keyEdit.getText().toString()));
	}

	private LinearLayout ll;

	private void initView() {
		// 底部视图
		listview = (ListView) findViewById(R.id.listview);
		TextView cleanHistory = new TextView(this);
		Drawable d = getResources().getDrawable(R.drawable.btn_clean_record);
		cleanHistory.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
		cleanHistory.setText(R.string.clean_all);
		cleanHistory.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 90));
		cleanHistory.setGravity(Gravity.CENTER);
		cleanHistory.setCompoundDrawablePadding(16);
		cleanHistory.setTextSize(16);
		cleanHistory.setTextColor(Color.parseColor("#333333"));
		ll = new LinearLayout(this);
		ll.setGravity(Gravity.CENTER);
		ll.addView(cleanHistory);

		((Button) findViewById(R.id.search_btn)).setOnClickListener(this);
		(mViewClean = (ImageView) findViewById(R.id.cleanSearch)).setOnClickListener(this);
		mInflater = LayoutInflater.from(this);
		(keyEdit = (EditText) findViewById(R.id.search_edit)).setOnFocusChangeListener(this);
		keyEdit.addTextChangedListener(this);
		keyEdit.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(keyEdit, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300);
		keyEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					if (!TextUtils.isEmpty(keyEdit.getText().toString())) {
						if (!click) {
							click = true;
							collapseSoftInputMethod(keyEdit);
							// startActivity(new
							// Intent(SearchActivity.this,GoodsListActivity.class).putExtra("KEYWORD",
							// keyEdit.getText().toString()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
						}
						return false;
					} else {
						// Toast.makeText(SearchActivity.this,
						// getString(R.string.no_key), Toast.LENGTH_SHORT)
						// .show();
					}
				}
				return false;
			}
		});

		cleanHistory.setOnClickListener(new OnClickListener() {// 点击事件
					@Override
					public void onClick(View v) {
						cleanHistory();// 清空历史记录
						isAddFoot = false;
						listview.removeFooterView(ll);
					}
				});

		if (history.size() > 0 && isAddFoot == false) {// 如果有值就添加到listView中
			isAddFoot = true;
			listview.addFooterView(ll);
			listview.setAdapter(adapter);
		}
		adapter = new SearchAdapter();
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				view.setSelected(true);
				String s = history.get(position);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(keyEdit.getWindowToken(), 0);
				keyEdit.setText(s);
				startActivity(new Intent(SearchActivity.this, ListActivity.class).putExtra("SearchKey", s));
				// startActivity(new Intent(SearchActivity.this,
				// GoodsListActivity.class).putExtra("KEYWORD",
				// s).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});
		((ImageView) findViewById(R.id.cancel)).setOnClickListener(this);
		keyEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String ss = keyEdit.getText().toString();
				if (ss.length() > 0) {
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm1.hideSoftInputFromWindow(keyEdit.getWindowToken(), 0);
			finish();
			break;
		case R.id.search_btn:
			if (!TextUtils.isEmpty(keyEdit.getText().toString())) {
				// startActivity(new Intent(SearchActivity.this,
				// GoodsListActivity.class).putExtra("KEYWORD",
				// keyEdit.getText().toString()).addFlags(
				// Intent.FLAG_ACTIVITY_CLEAR_TOP));
				// 将底部视图添加的listView
				if (isAddFoot == false) {
					isAddFoot = true;
					listview.addFooterView(ll);
					listview.setAdapter(adapter);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(keyEdit.getWindowToken(), 0);
				saveHistory();
				startActivity(new Intent(SearchActivity.this, ListActivity.class).putExtra("SearchKey", keyEdit.getText().toString()));
			} else {
				// Toast.makeText(SearchActivity.this,
				// getString(R.string.no_key),
				// Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.cleanSearch:
			keyEdit.getText().clear();
			mViewClean.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}

	}

	class SearchAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return history.size();
		}

		@Override
		public Object getItem(int position) {
			return history.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView = holder.getView(position);
			return convertView;
		}

		class ViewHolder {
			private TextView key;
			private View itemView;
			private TextView line;

			public ViewHolder() {
				itemView = mInflater.inflate(R.layout.search_item, null);
				itemView.setTag(this);
				key = (TextView) itemView.findViewById(R.id.key);
				line = (TextView) itemView.findViewById(R.id.keyLine);
			}

			public View getView(int position) {
				if (position == history.size()) {
					line.setVisibility(View.INVISIBLE);
				}
				key.setText((CharSequence) getItem(position));
				return itemView;
			}
		}
	}

	private boolean hasFoucs;// 是否有焦点

	// 输入文本框焦点监听
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		int length = keyEdit.getText().length();
		if (length > 0) {
			mViewClean.setVisibility(View.VISIBLE);
		} else {
			mViewClean.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 当输入框里面内容发生变化的时候回调的方法
		if (hasFoucs) {
			if (s.length() == 0) {
				mViewClean.setVisibility(View.INVISIBLE);
			} else {
				mViewClean.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	protected void onDestroy() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(keyEdit.getWindowToken(), 0);
		super.onDestroy();
	}
}
