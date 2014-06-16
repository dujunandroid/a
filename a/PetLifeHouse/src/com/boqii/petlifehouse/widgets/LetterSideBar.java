package com.boqii.petlifehouse.widgets;

import com.boqii.petlifehouse.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 字母索引控件 (字母导航菜单栏)
 * 
 * @author Administrator
 * 
 */
public class LetterSideBar extends View {

	// 26个字母
	public static String[] arrays = { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z", "#" };
	private int width;// 字母索引控件宽度
	private int height;// 字母索引控件高度
	private boolean isDown = false;// 标识是否按下，用作改变控件背景色
	private int oldSelect = -1;// 上次选中项索引

	private int textsize;// 画笔字体大小

	private ListView listView;// 绑定ListView
	private TextView overlay;// 当前索引显示框

	public LetterSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public LetterSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public LetterSideBar(Context context) {
		super(context);
		init(context, null, 0);
	}

	/**
	 * 该方法的作用：初始化索引控件
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	private void init(Context context, AttributeSet attrs, int defStyle) {
		initOverlay(context);
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.LetterIndexPaint, defStyle, 0);
		if (typedArray != null) {
			textsize = typedArray.getInt(R.styleable.LetterIndexPaint_textsize,
					12);
			typedArray.recycle();
		}
	}

	/**
	 * 该方法的作用：初始化当前索引显示框
	 * 
	 * @param context
	 */
	private void initOverlay(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		overlay = (TextView) inflater.inflate(R.layout.letter_overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	/**
	 * 该方法的作用：设置ListView绑定
	 * 
	 * @param listView
	 */
	public void setListView(ListView listView) {
		this.listView = listView;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float y = event.getY();// 得到点击的Y坐标
		// 点击y坐标所占总高度的比例*b数组的长度就等于点击中的个数（坐标）.
		int c = (int) (y / getHeight() * arrays.length);
		if (c < 0 || c > arrays.length) {
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isDown = true;
			setSelection(c);
			break;
		case MotionEvent.ACTION_UP:
			isDown = false;
			overlay.setVisibility(View.INVISIBLE);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			setSelection(c);
			break;
		}
		return true;
	}

	/**
	 * 该方法的作用：控件核心代码 ，通过索引设置选中项 日
	 * 
	 * @param index
	 */
	private void setSelection(int index) {
		if (index < 0 || index >=arrays.length) {
			return;
		}
		oldSelect = index;
		overlay.setText(arrays[index]);
		overlay.setVisibility(View.VISIBLE);
		if (listView != null && listView.getAdapter() != null) {
			if (listView.getAdapter() instanceof ILetterIndexer) {
				ILetterIndexer letterIndexer = (ILetterIndexer) listView
						.getAdapter();
				// 获取选中索引在listView中位置（position）
				int position = letterIndexer
						.getPositionForSection(arrays[index]);
				listView.setSelection(position);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isDown) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		width = getWidth();
		height = getHeight();
		Paint paint = new Paint();

		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(textsize);
		paint.setAntiAlias(true);// 抗锯齿
		for (int i = 0; i < arrays.length; i++) {
			if (i == oldSelect) {
				paint.setColor(getResources().getColor(R.color.title_color));
			} else {
				paint.setColor(getResources().getColor(R.color.blue52bdef));
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(arrays[i]) / 2;
			//height / arrays.length（单个字母的高度）
			float yPos = height / arrays.length * (i + 1);//y坐标
			canvas.drawText(arrays[i], xPos, yPos, paint);			
		}
		paint.reset();// 重置画笔
	}

	/**
	 * 自定义借口，类似于SectionIndexe，SectionIndexe限制太大
	 * 
	 * @author Administrator
	 * 
	 */
	public interface ILetterIndexer {
		int getPositionForSection(String section);
	}
}
