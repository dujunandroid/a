package com.boqii.petlifehouse.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * for original price
 * 
 * @author DuJun
 * 
 */
public class LineTextView extends TextView {
	
	private boolean isFirst = false;
	Paint mPaint = new Paint();
	
	public LineTextView(Context context) {
		super(context);

	}

	public LineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public LineTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(isFirst == false){
			isFirst = true;
			mPaint = getPaint();
		}
		
		
		canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);
		super.onDraw(canvas);
	}

}
