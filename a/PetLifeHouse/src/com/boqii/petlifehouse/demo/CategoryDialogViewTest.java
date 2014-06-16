package com.boqii.petlifehouse.demo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.boqii.petlifehouse.R;
import com.boqii.petlifehouse.baseactivities.BaseActivity;
import com.boqii.petlifehouse.widgets.CategoryDialogView;
import com.boqii.petlifehouse.widgets.CategoryDialogView.CateGoryDialogCallBack;

public class CategoryDialogViewTest extends BaseActivity implements OnClickListener, CateGoryDialogCallBack {

	private CategoryDialogView categoryDialogView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorytest);
		((LinearLayout)findViewById(R.id.category_layout)).setOnClickListener(this);
		((LinearLayout)findViewById(R.id.order_layout)).setOnClickListener(this);
		 categoryDialogView = (CategoryDialogView) findViewById(R.id.cate_gory_dialog_view);
		 categoryDialogView.execute(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.category_layout:
			categoryDialogView.ShowDialog(1);
			break;
		case R.id.order_layout:
			categoryDialogView.ShowDialog(2);
			break;

		default:
			break;
		}
	}

	@Override
	public void categoryCallback(String name, int typeId) {
		ShowToast("category : typename = " + name + "----" + "typeId = " + typeId);
	}

	@Override
	public void orderCallBack(String name, int typeId) {
		ShowToast("order : typename = " + name + "----" + "typeId = " + typeId);
	}

}
