package org.placebooks.www;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;



public class Shelf extends ListActivity {
	
	private String name;
	private JSONObject json;
    protected ListView mListView;

  
	
	 @Override
		public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);	//icicle
		        setContentView(R.layout.shelflist);	//push shelf list layout into the content view
		
		        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

		        // if connected to Internet then get JSON from server else get the cached version from sdcard
		        //JSONObject json = JSONfunctions.getJSONfromURL("http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/shelf/stuart@tropic.org.uk");
		        //name = "stuart";	//this is going to change to the variable "name" in the .xml response file from the server
		        /*
		         * If the user name and password are correct then it will get the json file from online and display the placebooks. The user can then download their shelf or a single placebook at a time. If the user has no internet
		         * then the code will attempt to read the json file from the sdcard. If the user has no placebooks on the sdcard then a message will be displayed saying that there have been no placebooks downloaded.
		        */
		         
		        if (isOnline()){
		          	json = JSONfunctions.getJSONfromURL("http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/shelf/stuart@tropic.org.uk");
		          	
		          	//also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
		        }
		        else {
		        	//if the json file is empty or does not exist then the listview will display an error message otherwise it will display the contents in the json shelf file
			        json = JSONfunctions.getJSONfromSDCard("/sdcard/placebooks/unzipped/" + "/packages/shelf.json");	
		        }
		       
		        
		        try{
		        	
		        	JSONArray entries = json.getJSONArray("entries");
		        	
			        for(int i=0;i<entries.length();i++){						
						HashMap<String, String> map = new HashMap<String, String>();	
						JSONObject e = entries.getJSONObject(i);
						
						map.put("id",  String.valueOf(i));
			        	map.put("title", "Title:" + e.getString("title"));
			        	map.put("description", "Description: " +  e.getString("description"));
			        	mylist.add(map);			
					}		
		        }catch(JSONException e)        {
		        	 Log.e("log_tag", "Error parsing data "+e.toString());
		        }
		        
		        ListAdapter adapter = new SimpleAdapter(this, mylist , R.layout.shelf, 
		                        new String[] { "title", "description" }, 
		                        new int[] { R.id.item_title, R.id.item_subtitle });
		        
		        setListAdapter(adapter);
		        
		        final ListView lv = getListView();
		        lv.setTextFilterEnabled(true);	
		        lv.setOnItemClickListener(new OnItemClickListener() {
		        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
		        		@SuppressWarnings("unchecked")
						HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);	        		
		        		Toast.makeText(Shelf.this, o.get("title") + "' was clicked.", Toast.LENGTH_SHORT).show(); 

					}
				});        
		        
	 } //end of onCreate
	 
	 public boolean isOnline() {
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}
	

}
