package com.boqii.petlifehouse.activities;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.baseservice.NetworkService;
import com.boqii.petlifehouse.entities.Order;
import com.boqii.petlifehouse.entities.Ticket;
import com.boqii.petlifehouse.utilities.Constants;
import com.boqii.petlifehouse.utilities.HttpManager;
import com.boqii.petlifehouse.utilities.Util;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommitOrderActivity extends BaseActivity implements OnClickListener, TextWatcher{

	private Ticket ticket;
	private EditText et_number;
	private TextView total;
	
	private final int REQUEST_CODE_COMMIT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commit_order);
		initView();
	}
	
	private void initView() {
		((ImageView)findViewById(R.id.back)).setOnClickListener(this);
		ticket = (Ticket)getIntent().getSerializableExtra("TICKET");
		((TextView)findViewById(R.id.ticket_name)).setText(ticket.TicketTitle);
		((TextView)findViewById(R.id.price)).setText(getString(R.string.price, ticket.TicketPrice));
		et_number = (EditText)findViewById(R.id.number);
		((ImageView)findViewById(R.id.decrease)).setOnClickListener(this);
		((ImageView)findViewById(R.id.decrease)).setEnabled(false);;
		((ImageView)findViewById(R.id.increase)).setOnClickListener(this);
		et_number.addTextChangedListener(this);
		total = (TextView)findViewById(R.id.totalprice);
		total.setText(getString(R.string.price, ticket.TicketPrice));
		((TextView)findViewById(R.id.commit)).setOnClickListener(this);
		((EditText)findViewById(R.id.telephone)).setText(getApp().user.Telephone);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.decrease:
			int number = Integer.parseInt(et_number.getText().toString());
			et_number.setText("" + --number);
			((ImageView)findViewById(R.id.increase)).setEnabled(true);
			if(number > 1){
				((ImageView)findViewById(R.id.decrease)).setEnabled(true);
			}else{
				((ImageView)findViewById(R.id.decrease)).setEnabled(false);
			}
			break;
		case R.id.increase:
			int num = Integer.parseInt(et_number.getText().toString());
			et_number.setText("" + ++num);
			((ImageView)findViewById(R.id.decrease)).setEnabled(true);
			if(num < ticket.TicketLimitNumber){
				((ImageView)findViewById(R.id.increase)).setEnabled(true);
			}
			if(num >= ticket.TicketLimitNumber){
				((ImageView)findViewById(R.id.increase)).setEnabled(false);
			}
			if(ticket.TicketLimitNumber <= 0){
				((ImageView)findViewById(R.id.increase)).setEnabled(true);
			}
			break;
		case R.id.commit:
			final int Number = Integer.parseInt(et_number.getText().toString());
			mNumber = Number;
			final String phone = ((EditText)findViewById(R.id.telephone)).getText().toString();
			mPhone = phone;
			if(Util.isEmpty(phone)){
				ShowToast(getString(R.string.phone_null));
				return;
			}
			if(!Util.isMobileNO(phone)){
				ShowToast(getString(R.string.phone_unable));
				return;
			}
			if(!Util.isEmpty(getApp().user.UserID)){
				Commit(Number, phone);
			}else
				UserLoginForResult(REQUEST_CODE_COMMIT);
			
			break;

		default:
			break;
		}
	}
	
	int mNumber;
	String mPhone;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch (requestCode) {
			case REQUEST_CODE_COMMIT:
				Commit(mNumber, mPhone);
				break;

			default:
				break;
			}
		}
	}

	private void Commit(final int Number, final String phone) {
		new HttpManager(this).Excute(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return NetworkService.getInstance(CommitOrderActivity.this).CommitOrder(getApp().user.UserID, ticket.TicketId, Number, phone);
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (!Util.isEmpty(result)) {
					try {
						JSONObject obj = new JSONObject(result);
						if (obj.optInt("ResponseStatus", -1) == Constants.RESPONSE_OK) {
							JSONObject data = obj.optJSONObject("ResponseData");
							Order order = new Order();
							order.OrderId = data.optInt("OrderId");
							order.OrderTitle = ticket.TicketTitle;
							order.OrderPrice = ticket.TicketPrice * Number;
							order.Mobile = phone;
							order.mTicket = ticket;
							startActivity(new Intent(CommitOrderActivity.this, PayOrderActivity.class).putExtra("ORDER", order));
						}else
							Toast.makeText(CommitOrderActivity.this, (CharSequence) obj.opt("ResponseMsg"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
						
			}
		});
		
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		int number = Integer.parseInt(arg0.toString());
		DecimalFormat df=new DecimalFormat("#0.00");
		total.setText(getString(R.string.price, df.format(ticket.TicketPrice * number)));
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}

}
