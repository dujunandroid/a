package com.boqii.petlifehouse.baseservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * ClientHelper
 * 请求数据(GET/POST方式)
 * @author DuJun
 */

public class ClientHelper {
	
	 private String httpUrl;
	 private String method;
	 public static String POST = "POST";
	 public static String GET = "GET";
	 private RequestParameters mRequestParameters;
	 private static ClientHelper instance;
	 private TimeOutListener listener;
//	 private static String cookie;
	 
	public static ClientHelper getInstance() {
		instance = new ClientHelper();
		return instance;
	}
	 
	 public void setTimeoutListener(TimeOutListener listener){
		 this.listener = listener;
	 }
	
	 public String set(String httpUrl,RequestParameters mRequestParameters,String method){
			this.httpUrl = httpUrl;
			this.method = method;
			this.mRequestParameters = mRequestParameters;
			return mRequestParameters.getRquestParam();
	 }
	 
	 /**
	  * 
	  * @param httpUrl
	  * @param mRequestParameters 参数
	  * @param method post/get
	  * @return String
	  */
	 public String execute(String httpUrl,RequestParameters mRequestParameters,String method){
			String params = set(httpUrl, mRequestParameters, method);
			String responseData = null;
			HttpURLConnection urlConnection = null;
			try{
				urlConnection = getURLConnection(params);
				Log.v("dujun", urlConnection + "");
//				String newCookie = urlConnection.getHeaderField("Set-Cookie");
//				if(newCookie != null && newCookie.length() > 0)
//					cookie = newCookie;

				InputStream stream = urlConnection.getInputStream(); 
				if(urlConnection.getResponseCode() == HttpStatus.SC_OK){
					byte[] b = new byte[1024];
					int readedLength = -1;
					ByteArrayOutputStream outputS = new ByteArrayOutputStream();
					while ((readedLength = stream.read(b)) != -1) {
						outputS.write(b, 0, readedLength);
					}
					responseData = outputS.toString();
					String data = new String(responseData.getBytes(), "utf-8");
					Log.v("dujun", data);
					urlConnection.disconnect();
				}else if(urlConnection.getResponseCode() == HttpStatus.SC_REQUEST_TIMEOUT){
					listener.timeoutListener();
					urlConnection.disconnect();
				}else{
					urlConnection.disconnect();
				}
				
			}catch(Exception e){
				if(urlConnection != null){
					urlConnection.disconnect();
				}
				e.printStackTrace();
				responseData = null;
			}
			return responseData;	
		} 
	 
	 /**
	  * 
	  * 返回HttpURLConnection对象
	  * @return HttpURLConnection
	  * @throws IOException
	  */
	private HttpURLConnection getURLConnection(String params) throws IOException{
		HttpURLConnection conn = null;
		if(method.equals(POST)){
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
//			if(cookie != null && cookie.length()>0)
//				conn.addRequestProperty("Cookie", cookie);
			conn.setConnectTimeout(20000);
			conn.setDoOutput(true);
//			if(params.contains("CommitOrder"))
//				params = URLEncoder.encode(params, "UTF-8");
			conn.getOutputStream().write(params.getBytes());
		}else if(method.equals(GET)){
			if(mRequestParameters != null){
				String param = params;
				httpUrl = (httpUrl.indexOf("?") != -1) ? httpUrl + "&"  + param  : httpUrl +"?" + param;
			}
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
//			if(cookie != null && cookie.length()>0)
//				conn.addRequestProperty("Cookie", cookie);
//			conn.addRequestProperty("User-Agent", MassVigUtil.getHeader());
			conn.setConnectTimeout(20000);
			conn.connect();
		}
		return conn;
	}
	
	public interface TimeOutListener{
		void timeoutListener();
	}
	
}

