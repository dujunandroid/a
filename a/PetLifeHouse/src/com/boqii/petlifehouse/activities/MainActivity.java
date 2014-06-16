package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseFragmentActivity;
import com.boqii.petlifehouse.fragments.FindFragment;
import com.boqii.petlifehouse.fragments.HomeFragment;
import com.boqii.petlifehouse.fragments.MineFragment;
import com.boqii.petlifehouse.fragments.MoreFragment;
import com.boqii.petlifehouse.fragments.NearFragment;
import com.boqii.petlifehouse.utilities.BoqiiExit;
import com.boqii.petlifehouse.utilities.DummyTabContent;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

public class MainActivity extends BaseFragmentActivity {

	TabHost tabHost;
	TabWidget tabWidget;
	LinearLayout bottom_layout;
	HomeFragment homeFragment;
	NearFragment nearFragment;
	FindFragment findFragment;
	MineFragment mineFragment;
	MoreFragment moreFragment;
	android.support.v4.app.FragmentTransaction ft;
	RelativeLayout tabIndicator1, tabIndicator2, tabIndicator3, tabIndicator4, tabIndicator5;
	BoqiiExit exit = new BoqiiExit();
	int CURRENT_TAB = 1;

	private void pressAgainExit() {
		if (exit.isExit()) {
			finish();
		} else {
			Toast.makeText(this, getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
			exit.doExitInOneSecond();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (CURRENT_TAB == 1) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				pressAgainExit();
				return true;
			}
		} else {
			SetCurrentTab(0);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Toast.makeText(this, this.getIntent().getScheme() + "----" +
		// this.getIntent().getDataString(), Toast.LENGTH_LONG).show();
		MobclickAgent.openActivityDurationTrack(false);
		findTabView();
		tabHost.setup();

		TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {

				android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
				homeFragment = (HomeFragment) fm.findFragmentByTag("home");
				nearFragment = (NearFragment) fm.findFragmentByTag("near");
				findFragment = (FindFragment) fm.findFragmentByTag("find");
				mineFragment = (MineFragment) fm.findFragmentByTag("mine");
				moreFragment = (MoreFragment) fm.findFragmentByTag("more");
				ft = fm.beginTransaction();

				if (homeFragment != null)
					ft.hide(homeFragment);

				if (nearFragment != null){
					ft.remove(nearFragment);
					nearFragment = null;
				}
//					ft.hide(nearFragment);

				if (findFragment != null)
					ft.hide(findFragment);
				if (mineFragment != null)
					ft.hide(mineFragment);

				if (moreFragment != null)
					ft.hide(moreFragment);

				if (tabId.equalsIgnoreCase("home")) {
					isTabHome();
					CURRENT_TAB = 1;

					/** ���ǰѡ���near */
				} else if (tabId.equalsIgnoreCase("near")) {
					isTabNear();
					CURRENT_TAB = 2;

					/** ���ǰѡ���find */
				} else if (tabId.equalsIgnoreCase("find")) {
					isTabFind();
					CURRENT_TAB = 3;

					/** ���ǰѡ���me */
				} else if (tabId.equalsIgnoreCase("mine")) {
					isTabMine();
					CURRENT_TAB = 4;

				} else if (tabId.equalsIgnoreCase("more")) {
					isTabMore();
					CURRENT_TAB = 5;
				}
				ft.commit();
			}

		};
		// ���ó�ʼѡ�
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(tabChangeListener);
		initTab();
		/** ���ó�ʼ������ */
		int index = getIntent().getIntExtra("INDEX", 0);
		tabHost.setCurrentTab(index);

	}

	public void SetCurrentTab(int index) {
		tabHost.setCurrentTab(index);
	}

	public void isTabHome() {

		if (homeFragment == null) {
			ft.add(R.id.realtabcontent, new HomeFragment(), "home");
		} else {
			ft.show(homeFragment);
		}
	}

	public void isTabNear() {
		if (nearFragment == null) {
			ft.add(R.id.realtabcontent, new NearFragment(), "near");
		} else {
			ft.show(nearFragment);
		}
	}

	public void isTabFind() {

		if (findFragment == null) {
			ft.add(R.id.realtabcontent, new FindFragment(), "find");
		} else {
			ft.show(findFragment);
		}
	}

	public void isTabMine() {

		if (mineFragment == null) {
			ft.add(R.id.realtabcontent, new MineFragment(), "mine");
		} else {
			ft.show(mineFragment);
		}
	}

	public void isTabMore() {

		if (moreFragment == null) {
			ft.add(R.id.realtabcontent, new MoreFragment(), "more");
		} else {
			ft.show(moreFragment);
		}
	}

	/**
	 * �ҵ�Tabhost����
	 */
	public void findTabView() {

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		LinearLayout layout = (LinearLayout) tabHost.getChildAt(0);
		TabWidget tw = (TabWidget) layout.getChildAt(1);

		tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
		((ImageView) tabIndicator1.getChildAt(0)).setBackgroundResource(R.drawable.ic_tab1);

		tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
		((ImageView) tabIndicator2.getChildAt(0)).setBackgroundResource(R.drawable.ic_tab2);

		tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
		((ImageView) tabIndicator3.getChildAt(0)).setBackgroundResource(R.drawable.ic_tab3);

		tabIndicator4 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
		((ImageView) tabIndicator4.getChildAt(0)).setBackgroundResource(R.drawable.ic_tab3);

		tabIndicator5 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
		((ImageView) tabIndicator5.getChildAt(0)).setBackgroundResource(R.drawable.ic_tab4);
	}

	/**
	 * ��ʼ��ѡ�
	 * 
	 * */
	public void initTab() {

		TabHost.TabSpec tSpecHome = tabHost.newTabSpec("home");
		tSpecHome.setIndicator(tabIndicator1);
		tSpecHome.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecHome);

		TabHost.TabSpec tSpecNear = tabHost.newTabSpec("near");
		tSpecNear.setIndicator(tabIndicator2);
		tSpecNear.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecNear);

		// TabHost.TabSpec tSpecFind = tabHost.newTabSpec("find");
		// tSpecFind.setIndicator(tabIndicator3);
		// tSpecFind.setContent(new DummyTabContent(getBaseContext()));
		// tabHost.addTab(tSpecFind);

		TabHost.TabSpec tSpecMine = tabHost.newTabSpec("mine");
		tSpecMine.setIndicator(tabIndicator4);
		tSpecMine.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMine);

		TabHost.TabSpec tSpecMore = tabHost.newTabSpec("more");
		tSpecMore.setIndicator(tabIndicator5);
		tSpecMore.setContent(new DummyTabContent(getBaseContext()));
		tabHost.addTab(tSpecMore);

	}

}
