package org.placebooks.www;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.impl.cookie.BasicClientCookie;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.util.List;
import 	android.content.Context;


public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url, String cookieName, String cookieValue, int cookieVersion, String cookieDomain, String cookiePath){
		InputStream is = null;
		JSONObject json = new JSONObject();
		

	    try{
	            //HttpClient httpclient = new DefaultHttpClient();
            	DefaultHttpClient httpclient = new DefaultHttpClient();
            	          	
                BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
                cookie.setVersion(cookieVersion);
                cookie.setDomain(cookieDomain);
                cookie.setPath(cookiePath);
                cookie.setExpiryDate(null);
                
                
                httpclient.setCookieStore(new BasicCookieStore());
                httpclient.getCookieStore().addCookie(cookie);
            	
            	 
	            HttpGet httpget = new HttpGet(url);
	            HttpResponse response = httpclient.execute(httpget);
	            HttpEntity entity = response.getEntity();
	            
	            if (entity != null) {
		            is = entity.getContent();
		            String result= convertStreamToString(is);	             
		            json=new JSONObject(result); 
		            is.close();         
	            }
	            

	    }catch (ClientProtocolException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	} catch (IOException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	} catch (JSONException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	}
	    	 
	    	return json;
	    	
	}
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		 
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				is.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
		}
	
	public static JSONObject getJSONfromSDCard(String address){

		JSONObject json = new JSONObject();

	    try{
	    		FileInputStream in = new FileInputStream(address);

	            if (in != null) {
		            String result= convertStreamToString(in);	             
		            json=new JSONObject(result); 
		            in.close();         
	            }
	            

	    }catch (IOException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	} catch (JSONException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    	}
	    	 
	    	return json;		
		
	}
}
