package com.boqii.petlifehouse.adapter;

import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * 控制授权页面的显示内容的适配器
 * @author Administrator
 *
 */
public class MyAuthorizeAdaper extends AuthorizeAdapter{
	
	@Override
	public void onCreate() {		
		//super.onCreate();
		
		hideShareSDKLogo();
		TitleLayout tl=getTitleLayout();
		tl.getBtnRight().setText("波奇");
				
	}

}
