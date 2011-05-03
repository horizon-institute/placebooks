package org.placebooks.www;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;

public class JSONfunctions {

	public static JSONObject getJSONfromURL(String url){
		InputStream is = null;
		//String result = "";
		//JSONObject jArray = null;
		
		JSONObject json = new JSONObject();

		//http post
	    try{
	            HttpClient httpclient = new DefaultHttpClient();
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
	    /*
	  //convert response to string
	    try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                    sb.append(line + "\n");
	            }
	            is.close();
	            result=sb.toString();
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    
	    try{
	    	
            jArray = new JSONObject(result);            
	    }catch(JSONException e){
	            Log.e("log_tag", "Error parsing data "+e.toString());
	    }
    
	    return jArray;
	    */
	    	
	}
	
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		 
		String line = null;
		try {
		while ((line = reader.readLine()) != null) {
		sb.append(line + "\n");
		}
		} catch (IOException e) {
		e.printStackTrace();
		} finally {
		try {
		is.close();
		} catch (IOException e) {
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
