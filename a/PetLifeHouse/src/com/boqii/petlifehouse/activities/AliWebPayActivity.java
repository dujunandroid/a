package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class AliWebPayActivity extends BaseActivity implements OnClickListener{

	private WebView webView;
	private Handler mHandler = new Handler();
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aliweb);
		setTitle("ali web pay");
		webView = (WebView)findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		((Button)findViewById(R.id.left_button)).setOnClickListener(this);
		String url = this.getIntent().getStringExtra("URL");
		if(!TextUtils.isEmpty(url)){
			webView.loadUrl(url);
		}
		webView.addJavascriptInterface(new JavaScriptInterface(), "Alipay");
		webView.setWebViewClient(new MyWebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;

			}
			
		});
	}
	
	final class JavaScriptInterface{
		JavaScriptInterface(){
			
		}
		public void PaySuccess(){
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					setResult(RESULT_OK);
					AliWebPayActivity.this.finish();
				}
			});
		}
	}

	public class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_button:
			AliWebPayActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
