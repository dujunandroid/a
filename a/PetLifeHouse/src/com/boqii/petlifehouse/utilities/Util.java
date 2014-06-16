package com.boqii.petlifehouse.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.amap.api.maps.model.LatLng;
import com.boqii.petlifehouse.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Toast;

public class Util {

	private final static String NAME = "BOQII";

	public static void CopyAssets(final Context c, String assetDir, String dir) {
		String[] files;
		try {
			files = c.getResources().getAssets().list(assetDir);
		} catch (Exception e) {
			return;
		}
		File SdPath = new File(dir);
		if (!SdPath.exists() || !SdPath.isDirectory()) {
			SdPath.mkdirs();
		}
		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// make sure file name not contains '.' to be a folder
				if (fileName.contains("jpg") || fileName.contains("png")) {
					File outFile = new File(SdPath, fileName);
					if (outFile.exists())
						outFile.delete();
					InputStream in = null;
					if (assetDir.length() == 0) {
						in = c.getAssets().open(fileName);
					} else {
						in = c.getAssets().open(assetDir + "/" + fileName);
					}
					OutputStream o = new FileOutputStream(outFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						o.write(buf, 0, len);
					}
					in.close();
					o.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static String TimeLeft(Context context, long time) {
		String result = "";
		// long year = time / (365 * 24 * 3600);
		time = time % (365 * 24 * 3600);
		// long month = time / (30 * 24 * 3600);
		time = time % (30 * 24 * 3600);
		long day = time / (24 * 3600);
		time = time % (24 * 3600);
		long hour = time / 3600;
		time = time % 3600;
		long min = time / 60;
		time = time % 60;
		long sec = time;
		if (day >= 3) {
			result = context.getString(R.string.threedays);
		} else {
			result = ((day > 0) ? (day + context.getString(R.string.day)) : "") + ((hour > 0) ? (hour + context.getString(R.string.hour)) : "")
					+ ((min > 0) ? (min + context.getString(R.string.min)) : (sec + context.getString(R.string.sec)));
		}
		return result;
	}

	public static String GetDistanceToKM(float distance) {
		String format="";
		if(distance>1000 && distance < 10000 * 1000){
			format = (distance / 1000) + "km";
		}else if(distance > 0 && distance < 10000 * 1000){
			format = distance + "m";
		}
		return format;
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param telephone
	 */
	public static void CallUp(Context context, String telephone) {
		context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone)));
	}

	/**
	 * round bitmap
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		if (bitmap == null)
			return null;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取指定宽高的图片
	 * 
	 * @param imageurl
	 * @param width
	 * @param height
	 * @return
	 */
	public static String GetImageUrl(String imageurl, int width, int height) {

		// FIXME
//		return "http://imgtest.boqiicdn.com/Data/Vet/B/1308/27/img81781377598026_100x100.jpg?v=b18e33fe9b92b51d0f538c59ac2000f9";
		String url = imageurl;
		String image = "";
		if(!Util.isEmpty(url) && url.lastIndexOf(".") != -1){
			image = url.substring(0, url.lastIndexOf(".")) + "_" + width + "x" + height + url.substring(url.lastIndexOf("."), url.length());
		}
		try {
			String sign = getMD5(image + "comboqiiwwwvetappimg");
			image = image + "?v=" + sign;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
		
		
		// String url = imageurl;
		// String image = "";
		// if (!TextUtils.isEmpty(url) && !url.equals("null")) {
		// String[] elements = url.split("/");
		// String front = elements[0] + "/" + elements[1] + "/" + elements[2];
		// String middle = "";
		// for (int i = 3; i < elements.length - 1; i++) {
		// middle += elements[i] + "_";
		// }
		// if(!TextUtils.isEmpty(middle)){
		// middle = middle.substring(0, middle.length() - 1);
		// String back = elements[elements.length - 1];
		// back = width + "x" + height + "_1_0_80_" + back;
		// image = front + "/" + middle + "/" + back;
		// }
		// }
		// return image;
	}

	/**
	 * md5
	 * 
	 * @param val
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();
		String str = getString(m);
		// str = str.toUpperCase();
		return str;
		/*
		 * String result = ""; try { result = GetRSAString(val); result =
		 * URLEncoder.encode(result, "UTF-8"); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } return result;
		 */
		// return val;
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {

			int val = ((int) b[i]) & 0xff;
			if (val < 16) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
	}

	// private static String GetRSAString(String str_m) throws Exception {
	// String str_exponent = MassVigContants.EXPONENT;
	// String str_modulus = MassVigContants.MODULUS;
	// //创建公钥
	// byte[] ary_exponent=(new BASE64Decoder()).decodeBuffer(str_exponent);
	// byte[] ary_modulus=(new BASE64Decoder()).decodeBuffer(str_modulus);
	// //注意构造函数，调用时指明正负值，1代表正值，否则报错
	// BigInteger big_exponent = new BigInteger(1,ary_exponent);
	// BigInteger big_modulus = new BigInteger(1,ary_modulus);
	// RSAPublicKeySpec keyspec=new RSAPublicKeySpec(big_modulus,big_exponent);
	// KeyFactory keyfac=KeyFactory.getInstance("RSA");
	// PublicKey publicKey=keyfac.generatePublic(keyspec);
	// //进行加密
	// Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	// cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	// byte[] enBytes = cipher.doFinal(str_m.getBytes("UTF-8"));
	// String s = (new BASE64Encoder()).encodeBuffer(enBytes);
	// return s;
	//
	// }

	public static boolean isEmpty(String content) {
		if (!TextUtils.isEmpty(content) && !content.equals("null"))
			return false;
		return true;
	}

	/**
	 * 判断邮箱是否合法
	 * 
	 * @param email_string
	 * @return
	 */
	public static boolean isEmail(String email_string) {

		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]?@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(check);
		Matcher matcher = pattern.matcher(email_string);

		return matcher.matches();
	}

	/**
	 * 判断电话号码是否合法
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	/**
	 * 通过Service的类名来判断是否启动某个服务
	 * 
	 * @param mServiceList
	 * @param className
	 * @return
	 */
	public static boolean ServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList, String className) {
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取所有启动的服务的类名
	 * 
	 * @param mServiceList
	 * @return
	 */
	public static String getServiceClassName(List<ActivityManager.RunningServiceInfo> mServiceList) {
		String res = "";
		for (int i = 0; i < mServiceList.size(); i++) {
			res += mServiceList.get(i).service.getClassName() + " \n";
		}
		return res;
	}

	/**
	 * see if the file is exist
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFileExist(String path) {
		if (path != null) {
			return new File(path).exists();
		} else {
			return false;
		}
	}

	private static void writeFile(String fileName, String data) {
		BufferedWriter buf = null;
		try {
			buf = new BufferedWriter(new FileWriter(fileName, true));
			buf.write(data, 0, data.length());
			buf.newLine();
		} catch (OutOfMemoryError oom) {

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buf != null)
					buf.close();
			} catch (IOException e) {
			}
		}

	}
	
	/**
	 * 存储数据到文件
	 * @param content
	 */
	public static void saveFile(String content, String filePath){
		File mFile = new File(filePath);
        File pFile = mFile.getParentFile();
        if(pFile != null && !pFile.exists()){  
        	pFile.mkdirs();  
        }
		if(mFile.isFile() && mFile.exists())
			mFile.delete();
		writeFile(filePath, content);
		
	}

	/**
	 * 获取文件内容
	 * 
	 * @param file
	 * @return
	 */
	public static String loadFile(String filePath) {
		File file = new File(filePath);
		if(!file.exists())
			return "";
		
		String content = "";
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br;
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			content = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 判断wifi是否可用
	 * 
	 * @param inContext
	 * @return
	 */
	public static boolean isWiFiActive(Context inContext) {
		WifiManager mWifiManager = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断3G是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				return false;
			} else {
				if (info.isAvailable()) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * 设置自定义字体颜色大小
	 * 
	 * @param source
	 *            要改变的字体源
	 * @param color
	 *            目标颜色
	 * @param start
	 *            开始index
	 * @param end
	 *            结束index
	 * @param textsize
	 *            字体大小
	 * @return
	 */
	public static SpannableString setCustomText(CharSequence source, int color, int start, int end, int textsize) {
		SpannableString s = new SpannableString(source);
		s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// s.setSpan(new AbsoluteSizeSpan(textsize), start, end,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return s;
	}

	/**
	 * toast显示，多次点击不重复生成
	 * 
	 * @param toast
	 * @param str
	 *            显示的信息
	 * @param len
	 *            显示时长
	 */
	public static void doToastShow(Context context, Toast toast, String str, int len) {
		if (toast != null) {
			toast.cancel();
			toast.setText(str);
			toast.setDuration(len);
		} else {
			toast = Toast.makeText(context, str, len);
		}
		toast.show();
	}

	/**
	 * 判断gps是否开启
	 * 
	 * @param context
	 * @return
	 */

	public static boolean isGpsOpen(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean GPS_status = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return GPS_status;
	}

	/**
	 * 获取唯一设备号
	 */
	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";

	public synchronized static String id(Context context) {
		sID = null;
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation, context);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return sID;
		}
		return null;
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation, Context context) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = getMyUUID(context);
		out.write(id.getBytes());
		out.close();
	}

	public static String[] Sort(String[] s) {
		String t;
		for (int i = 0; i < s.length; i++) {
			for (int j = i; j < s.length; j++) {
				if (s[i].compareTo(s[j]) > 0) {
					t = s[i];
					s[i] = s[j];
					s[j] = t;
				}
			}
		}
		return s;
	}

	/**
	 * 获取当前安装版本号
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context c) throws Exception {
		PackageManager packageManager = c.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}

	private static String getMyUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * change the bitmap size
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap setBitmapSize(Bitmap bitmap, int newWidth, int newHeight) {

		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		return newbitmap;
	}

	/**
	 * save boolean data into sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setPreferenceBooleanData(Context context, String key, Boolean value) {
		SharedPreferences settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * get boolean data from sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getPreferenceData(Context context, String key, boolean defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaultValue);

	}

	/**
	 * save string data into sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setPreferenceStringData(Context context, String key, String value) {
		SharedPreferences settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * get String data from sharedPreference
	 * 
	 * @param context
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getPreferenceData(Context context, String key, String defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);

	}

	public static void deleteFiles(String path) {
		File dirFile = new File(path);
		try {
			if (dirFile.exists()) {
				File[] fileList = dirFile.listFiles();
				if (fileList != null) {
					for (int i = 0; i < fileList.length; i++) {
						fileList[i].delete();
					}
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 得N个点的中心点
	 * 
	 * @param ls
	 * @return
	 */
	public static LatLng getCenterPoint(ArrayList<LatLng> ls) {
		double maxX = ls.get(0).latitude, minX = ls.get(0).latitude, minY = ls.get(0).longitude, maxY = ls.get(0).longitude;
		for (int i = 1; i < ls.size(); i++) {
			if (ls.get(i).latitude >= maxX) {
				maxX = ls.get(i).latitude;
			}

			if (ls.get(i).latitude <= minX) {
				minX = ls.get(i).latitude;
			}

			if (ls.get(i).longitude >= maxY) {
				maxY = ls.get(i).longitude;
			}

			if (ls.get(i).longitude <= minY) {
				minY = ls.get(i).longitude;
			}
		}
		// System.out.println(maxX + "-" + maxY + "=" + minY + "-" + minX);
		double x = Math.abs(maxX - minX) / 2 + minX;
		double y = Math.abs(maxY - minY) / 2 + minY;
		// System.out.println(x + "-" + y);
		return new LatLng(x, y);
	}

	/**
	 * 得到一个指定长度的省略字符串
	 * 
	 * @param string
	 * @param size指定长度
	 *            ,包含该长度
	 */
	public static String getStrFormatSize(String str, int size) {
		int length = str.length();
		if (length > size) {
			str = str.substring(0, size) +"...";
		}
		return str;
	}


	/**
	 * 快捷分享的方法
	 */
	public static void share(final Context context, PlatformActionListener OneKeyShareCallback) {
		ShareSDK.initSDK(context);
		Resources r= context.getResources();
		OnekeyShare oks = new OnekeyShare();
		String downloadAddress="http://m.boqii.com/appdown.html";
		// 分享时 Notification 的图标和文字
		oks.setNotification(R.drawable.ic_launcher, r.getString(R.string.app_name));
		oks.setTitle(r.getString(R.string.app_name));// 标题
		oks.setTitleUrl(downloadAddress);	
		// 图片指向的链接
		oks.setComment(r.getString(R.string.share_all)+ downloadAddress);
		oks.setSite(r.getString(R.string.app_name));
		oks.setSiteUrl(downloadAddress);
		if(com.boqii.petlifehouse.utilities.Config.DEBUG){
			oks.setImageUrl("http://atest.boqiicdn.com/Images/app/pic1.png");
		}else{
			oks.setImageUrl("http://a.boqiicdn.com/Images/app/pic1.png");
		}
		//oks.setImagePath(SHARE_IMG);//待分享的本地图片。如果目标平台使用客户端分享，此路径不可以在/data/data下面
		//oks.setImageUrl("http://a.boqiicdn.com/Images/site/logo.jpg");
		oks.setUrl(downloadAddress);	
		oks.setText(r.getString(R.string.share_all) + downloadAddress);
		// 自定义图标
//		Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.u53_normal);
//		// 图标点击后会通过 Toast 提示消息
//		final OnekeyShare fOKS = oks;
//		OnClickListener listener = new OnClickListener() {
//			public void onClick(View v) {
//				context.startActivity(new Intent().setClass(context, ShareWeiboActivity.class));
//				fOKS.finish();
//			}
//		};
//		oks.setCustomerLogo(logo, context.getResources().getString(R.string.share_weibo), listener);
		// 监听快捷分享的处理结果
		oks.setCallback(OneKeyShareCallback);
		oks.show(context);
	}
	private static String imgName="share_img.png";
	public static String SHARE_IMG;
	public static void initImagePath(Context context) {
		try {
			String cachePath = Environment.getExternalStorageDirectory().getPath();				
			SHARE_IMG =cachePath+imgName;
			File file = new File(SHARE_IMG);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_img);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			SHARE_IMG = null;
		}
	}
}
