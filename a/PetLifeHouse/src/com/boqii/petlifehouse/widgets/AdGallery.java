package com.boqii.petlifehouse.widgets;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class AdGallery extends Gallery {

	public boolean isTouch = false;
	public Context mContext;
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
	    	   isTouch = false;
	    	   this.cancel();
	    }
	};
	public AdGallery(Context context) {
		super(context);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	public AdGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	public AdGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int keyCode;
		if(timer != null){
			if(task != null)
				task.cancel();
			task = new TimerTask() {
				
				@Override
				public void run() {
			    	   isTouch = false;
				    	   this.cancel();
				    }
			};
			timer.schedule(task, 8000);
		}
		if (isScrollingLeft(e1, e2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
//		((MainActivity)mContext).setDotImage(this.getSelectedItemPosition() + 1);
		super.onKeyDown(keyCode, null);
		return true;

		// return super.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		if(!isTouch){
			isTouch = true;
		}
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

}
