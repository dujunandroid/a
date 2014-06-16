package com.boqii.petlifehouse.widgets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import com.boqii.petlifehouse.utilities.Config;
import com.boqii.petlifehouse.utilities.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;

/**
 * 自定义ImageView,url自动加载和缓存
 * @author DuJun
 * 
 */

public class NetImageView extends android.widget.ImageView{
	private String imageUrl;
	private String SDPath;
	private static Boolean isAutoLoadImges = true;
	private static LruCache<String, Bitmap> mLruCache;
	private Bitmap defaultImg;
	private Context mContext;
	private Boolean isDrawComplete = false;

	public NetImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	public NetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}
	
	/**
	 * 初始化LruCache,内存
	 * @author DuJun
	 * 
	 */
	
	public void initCache(){
		if(mLruCache == null){
			int mMemory = ((ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			mLruCache = new LruCache<String, Bitmap>(mMemory/8 * 1024 * 1024){
				@Override
				protected int sizeOf(String key, Bitmap value) {
					// TODO Auto-generated method stub
					return value.getRowBytes() * value.getHeight();
				}

				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					// TODO Auto-generated method stub
					super.entryRemoved(evicted, key, oldValue, newValue);
					
					
					if(newValue != null && !newValue.isRecycled()){
						//newValue.recycle();
					}
				}
			};
		}
	}
	
	
	
	public static void clearCache(){
		mLruCache = null;
	}
	
	/**
	 * 根据url加载图片,先从LurCache 到 SD 到 NetWork
	 * @author DuJun
	 * 
	 */
	
	public void setImageUrl(String imageUrl,String SDPath,Bitmap bm){
		initCache();
		this.defaultImg = bm;
		isDrawComplete = false;
		this.imageUrl = imageUrl;
		this.SDPath = SDPath;
		Bitmap mBitmap = null;
		if(mLruCache != null){
			mBitmap = mLruCache.get(imageUrl);
			if(mBitmap != null){
				setImageBitmap(mBitmap);
			}
		}
		if(mBitmap == null && defaultImg != null){
			super.setImageBitmap(defaultImg);
		}
		if(mBitmap == null && isAutoLoadImges){
//			try {
				new SdCardAsyncTask().execute();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
		}
	}
	
	/**
	 * 设置是否自动从SDcard,NetWork加载图片
	 * @author DuJun
	 * 
	 */
	
	public static void setIsAutoLoadImage(Boolean isAuto){
		isAutoLoadImges = isAuto;
	}
	
	/**
	 * 手动加载图片
	 * @author DuJun
	 * 
	 */
	
	public void updateImage(){
		if(!isDrawComplete){
//			try {
				new SdCardAsyncTask().execute();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
		}
	}
	
	public void setImageBitmap(Bitmap bm){
		super.setImageBitmap(bm);
		isDrawComplete = true;
	}
	
	/**
	 * 从SDcard加载图片,并加入LurCache
	 * @author DuJun
	 * 
	 */

	class SdCardAsyncTask extends AsyncTask<String, Object, Bitmap>{
		private String flag;
		private String path;
		public SdCardAsyncTask(){
			if(imageUrl != null && SDPath != null){
				this.flag = imageUrl;
				int index = imageUrl.lastIndexOf("/");
				if(index != -1){
					this.path = SDPath+imageUrl.substring(index, imageUrl.length());
				}
			}
			
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeFile(this.path);
			} catch (Exception e) {
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			if(result != null){
				if(mLruCache != null){
					mLruCache.put(this.flag, result);
				}
				if(this.flag == imageUrl){
					setImageBitmap(result);
				}
			}else{
				if(!Util.isWiFiActive(mContext) && !Config.IsCanDownloadPicWhenNoWifi)
					return;
				try {
					new NetWorkAsyncTask().execute(flag,path);
				} catch (Exception e) {
//					
				}
			}
			super.onPostExecute(result);
		}
		
	}
	
	/**
	 * 从NetWork加载图片,并保存SDCard,LurCache
	 * @author DuJun
	 * 
	 */
	
	class NetWorkAsyncTask extends AsyncTask<String, Object, Bitmap>{
		private String flag;
		@SuppressWarnings("finally")
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(params[0] != null){
				this.flag = params[0];
				Bitmap mbitmap = loadImageFromUrl(params[0]);
				if(mbitmap != null){
					try {
						saveFile(mbitmap, params[1]);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						return mbitmap;
					}
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null){
				if(mLruCache != null){
					mLruCache.put(this.flag, result);
				}
				if(this.flag == imageUrl){
					setImageBitmap(result);
				}
			}
		}
	}
	
	
	/**
	 * 从NetWork加载图片
	 * @author DuJun
	 * 
	 */
	
	public static Bitmap loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		Bitmap d = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			d = BitmapFactory.decodeStream(i);
		} catch (Exception e1) {
			e1.printStackTrace();
		} catch (OutOfMemoryError e) {
			System.gc();
		}
		return d;
	}
	
	/**
	 * 保存图片到SDcard
	 * @author DuJun
	 * 
	 */
	
	public void saveFile(Bitmap bm, String filePath) throws Exception{  
        File mFile = new File(filePath);
        File pFile = mFile.getParentFile();
        if(pFile != null && !pFile.exists()){  
        	pFile.mkdirs();  
        }  
        filePath = filePath.substring(0, filePath.length() - 1);
        File myCaptureFile = new File(filePath);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
//		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
	    bos.flush();  
	    bos.close(); 
         
    }  
	

}
