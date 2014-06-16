package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ProtocolsActivity extends BaseActivity implements OnClickListener {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.protocols);		
		initView();
	}
	
	private void initView(){
		findViewById(R.id.backPBtn).setOnClickListener(this);
		mWebView = (WebView) findViewById(R.id.mpWebView);
//		mWebView.getSettings().setJavaScriptEnabled(true);  
//		mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");  
//		mWebView.setWebViewClient(new MyWebViewClient());  
		try {
			mWebView.loadUrl("http://vettest.boqii.com/MobileApi/GetRegAgreement");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	 final class MyWebViewClient extends WebViewClient{    
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {     
	            view.loadUrl(url);     
	            return true;     
	        }    
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {  
	            Log.d("WebView","onPageStarted");  
	            super.onPageStarted(view, url, favicon);  
	        }      
	        public void onPageFinished(WebView view, String url) {  
	            Log.d("WebView","onPageFinished ");  
	            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +  
	                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");  
	            super.onPageFinished(view, url);  
	        }  
	    }  
	      
	    final class InJavaScriptLocalObj {  
	        public void showSource(String html) {  
	            Log.d("HTML", html);  
	        }  
	    }  

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backPBtn:
			finish();
			break;
		default:
			
			break;
		}
	}
}
