package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.utilities.Util;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewActivity extends BaseActivity {

	private WebView mWebView;
	private String url = "";
	private ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		url = this.getIntent().getStringExtra("URL");
		initView();
	}

	private void initView() {
		progress = (ProgressBar)findViewById(R.id.progress);
		mWebView = (WebView) findViewById(R.id.webview);
		if(!Util.isEmpty(this.getIntent().getStringExtra("TITLE")))
			((TextView)findViewById(R.id.title)).setText(this.getIntent().getStringExtra("TITLE"));
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		mWebView.loadUrl(url);
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				progress.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
		});
		((ImageView)findViewById(R.id.back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebViewActivity.this.finish();
			}
		});
		
	}

}
