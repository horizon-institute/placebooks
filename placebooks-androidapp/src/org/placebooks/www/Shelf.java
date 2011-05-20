package org.placebooks.www;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

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
import android.widget.TextView;
import android.widget.LinearLayout;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import org.apache.http.util.ByteArrayBuffer;
import java.io.IOException;
import android.widget.Button;
import android.view.View.OnClickListener; 




public class Shelf extends ListActivity {
	
	private String name;
	private JSONObject json;
//    protected ListView mListView;
    private String username;
    private ArrayList<Button> download;
    private ArrayList<Button> view;
    ListView lv;
    private ArrayList<Integer> pbkey = new ArrayList<Integer>();
    
	
	 @Override
		public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);	//icicle
		        setContentView(R.layout.shelflist);	//push shelf list layout into the content view
		
		        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		        
		        /*
		         * get the extras (username) out of the new intent
		         * retrieve the username.
		         */
		        Intent intent = getIntent();
		        if(intent != null) username = intent.getStringExtra("username");
		        	        
		        		       
		        /*
		         * If the user name and password are correct then it will get the json file from online and display the placebooks. The user can then download their shelf or a single placebook at a time. If the user has no Internet
		         * then the code will attempt to read the json file from the sdcard. If the user has no placebooks on the sdcard then a message will be displayed saying that there have been no placebooks downloaded.
		        */       
		        if (isOnline()){
		        	String url = "http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/shelf/"+ username + "/";
		          	json = JSONfunctions.getJSONfromURL(url);		//email address that the user enters (stuart@tropic.org.uk) (ktg@cs.nott.ac.uk/)
		          										  
		          	//also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
		          	DownloadFromUrl(url, username+ "_shelf" + ".json"); 	
		          	
		          	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
			        TextView tv = new TextView(this);
			        tv.setText("reading the shelf from the Internet. Also updating the cached shelf.");	
			        ll.addView(tv);
		          	
		        }
		        else if (!isOnline()) {		//do a check if there is a shelf file on the sdcard
		        	//if the json file is empty or does not exist then the listview will display an error message otherwise it will display the contents in the json shelf file
			        json = JSONfunctions.getJSONfromSDCard("sdcard/placebooks/unzipped/packages/" + username+ "_shelf" + ".json");			///sdcard/placebooks/unzipped/" + "packages/shelfstuart.json
		        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
			        TextView tv = new TextView(this);
			        tv.setText("reading the cached shelf because cannot connect to Internet at this time.");
			        ll.addView(tv);
			        
			        
		        }
		        else {
		        	// either no internet, no placebook shelf stored on the sd card, no books can be accessed
		        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
		        	TextView tv = new TextView(this);
		        	tv.setText("No data: You have not downloaded any placebooks and do not appear to be online. You need to enable the Internet to access your placebook shelf and then you can download your placebook/s. Another reason why you are not seeing your shelf might be due to your Internet security settings");
		        	ll.addView(tv);
		        }
		       
		        
		        try{
		        	
		        	JSONArray entries = json.getJSONArray("entries");
		        	
			        for(int i=0;i<entries.length();i++){						
						HashMap<String, String> map = new HashMap<String, String>();	
						JSONObject e = entries.getJSONObject(i);
						
						pbkey.add(e.getInt("key"));	// store the placebook keys in the arraylist	
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
		        
		        
		      /*  final ListView lv = getListView();
		        lv.setTextFilterEnabled(true);	
		        
		        //action listener for each row
		        lv.setOnItemClickListener(new OnItemClickListener() {
		        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        		
		        		@SuppressWarnings("unchecked")
						HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);	        		
		        		Toast.makeText(Shelf.this, o.get("title") + "' was clicked.", Toast.LENGTH_SHORT).show(); 

					}
				});    
		       */
		       
		        
		   
		        
		        
	 } //end of onCreate
	 
	 /*
	  * Check for an Internet connection and return true if there is Internet
	  */
	 public boolean isOnline() {
		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}
	 
	 /*
	  * Method for downloading the shelf JSON file to the SDCard for caching.
	  * Used for the initial download and also caching.
	  */
	 public void DownloadFromUrl(String DownloadUrl, String fileName) {

		   try {
		           //File root = android.os.Environment.getExternalStorageDirectory();               

		           //File dir = new File (root.getAbsolutePath() + "/xmls");
		           File SDCardRoot = Environment.getExternalStorageDirectory();
		           File dir = new File(SDCardRoot + "/placebooks/unzipped/packages/"); //SDCardRoot/PlaceBooks/unzipped/packages
		           if(dir.exists()==false) {
		                dir.mkdirs();
		           }

		           URL url = new URL(DownloadUrl); //you can write here any link
		           File file = new File(dir, fileName);

		           long startTime = System.currentTimeMillis();
		           Log.d("DownloadManager", "download begining");
		           Log.d("DownloadManager", "download url:" + url);
		           Log.d("DownloadManager", "downloaded file name:" + fileName);

		           /* Open a connection to that URL. */
		           URLConnection ucon = url.openConnection();

		           /*
		            * Define InputStreams to read from the URLConnection.
		            */
		           InputStream is = ucon.getInputStream();
		           BufferedInputStream bis = new BufferedInputStream(is);

		           /*
		            * Read bytes to the Buffer until there is nothing more to read(-1).
		            */
		           ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		           int current = 0;
		           while ((current = bis.read()) != -1) {
		              baf.append((byte) current);
		           }


		           /* Convert the Bytes read to a String. */
		           FileOutputStream fos = new FileOutputStream(file);
		           fos.write(baf.toByteArray());
		           fos.flush();
		           fos.close();
		           Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		   } catch (IOException e) {
		       Log.d("DownloadManager", "Error: " + e);
		   }

		}
	 

	 public void myClickHandler(View v) 
	    {
	          
	        //reset all the listView items background colours 
	        //before we set the clicked one..

	        lv = getListView();
		    lv.setTextFilterEnabled(true);
    
	        for (int i=0; i < lv.getChildCount(); i++) 
	       {
	        	
	            //lvItems.getChildAt(i).setBackgroundColor(Color.BLUE); 
	        	
	        	/*LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
		        TextView tv = new TextView(this);
		        tv.setText("keys are: " + pbkey.get(i));	
		        ll.addView(tv);
	        	*/
	        
	       }
	        
	        
	        //get the row the clicked button is in
//	        LinearLayout vwParentRow = (LinearLayout)v.getParent();
	         
//	        TextView child = (TextView)vwParentRow.getChildAt(0);
//	        Button btnChild = (Button)vwParentRow.getChildAt(1);
	      //  btnChild.setText(child.getText());
//	        btnChild.setText("I've been clicked!");
	        
	        //int c = Color.CYAN;
	        
	        //vwParentRow.setBackgroundColor(c); 
//	        vwParentRow.refreshDrawableState();      
		// Toast.makeText(getApplicationContext(), "OK button clicked", Toast.LENGTH_LONG).show();

		}
	 
	
	 
	
	

}
