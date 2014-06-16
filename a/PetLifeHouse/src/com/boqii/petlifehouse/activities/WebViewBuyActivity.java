package com.boqii.petlifehouse.activities;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Util;
import com.boqii.petlifehouse.widgets.LineTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebViewBuyActivity extends BaseActivity implements OnClickListener {

	private WebView mWebView;
	private String url = "";
	private Ticket ticket;
	private final int REQUEST_BUY_CODE = 1;
	private ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_buy);
		ticket = (Ticket)this.getIntent().getSerializableExtra("TICKET");
		url = ticket.TicketShowUrl;
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case REQUEST_BUY_CODE:
				startActivity(new Intent(this, CommitOrderActivity.class).putExtra("TICKET", ticket));
				break;
			default:
				break;
			}
		}
	}

	private void initView() {
		progress = (ProgressBar)findViewById(R.id.progress);
		RelativeLayout ticket_info = (RelativeLayout) findViewById(R.id.ticket_info);
		((TextView) ticket_info.findViewById(R.id.price)).setText(getString(
				R.string.price, ticket.TicketPrice));
		((LineTextView) ticket_info.findViewById(R.id.oriPrice))
				.setText(getString(R.string.price, ticket.TicketOriPrice));
		((TextView) ticket_info.findViewById(R.id.sale)).setText(getString(
				R.string.sale, ticket.TicketSale));
		((TextView) ticket_info.findViewById(R.id.limit)).setText(ticket.TicketLimit);
		ImageView buy = (ImageView) ticket_info.findViewById(R.id.buy_btn);
		buy.setOnClickListener(this);
		if (ticket.TicketRemain > 0) {
			buy.setBackgroundResource(R.drawable.ic_buy);
		} else {
			buy.setBackgroundResource(R.drawable.ic_cannot_buy);
		}

		mWebView = (WebView) findViewById(R.id.webview);
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
				WebViewBuyActivity.this.finish();
			}
		});
		
	}

	@Override
	public void onClick(View arg0) {
		if (ticket.TicketRemain > 0) {
			if(!Util.isEmpty(getApp().user.UserID)){
				startActivity(new Intent(this, CommitOrderActivity.class).putExtra("TICKET", ticket));
			}else
				UserLoginForResult(REQUEST_BUY_CODE);
		}else{
		}
	}

}
