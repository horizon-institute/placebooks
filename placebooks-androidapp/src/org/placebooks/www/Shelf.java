package org.placebooks.www;

import android.app.Activity;
import android.app.ActivityGroup;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ListView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
//import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.io.File;
//import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.ByteArrayBuffer;
import java.io.IOException;
import android.view.View.OnClickListener; 
import android.view.animation.Animation;
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.ToggleButton;
import android.app.LocalActivityManager;
import java.io.*;
import org.json.JSONObject;
//import org.json.simple.JSONValue;
//import java.util.*; 
import java.util.zip.ZipException;


/**
 * Online Shelf Class
 */
public class Shelf extends ActivityGroup{//Activity/*extends ListActivity*/
	
    private ProgressDialog refreshDialog = null;

	private ProgressDialog myDialog = null;
	//private ToggleButton toggleButton;
	//private Button onlineButton;
	//private Button offlineButton;
	private OnlineCheck oc;
	
	private JSONObject jsonDownloads;
	private JSONObject json;
    private String username;
    //private String password;
    //private ListView lv;
    private String shelfOwner;	//owner's name
	
	//Download variables
	private File file;
    public static final int dialogDownloadProgress = 0;
    private ProgressDialog mProgressDialog;
    private String filename= "downloadFile.zip"; 
    //Application variables
    private String shelfUrl;
    private String unzippedRoot;
    private String packageUrl;
    private String root;
    private String unzippedDir;
    
    private String cookieName;
    private String cookieVal;
    private String languageSelected;
    
	 @Override
		public void onCreate(Bundle savedInstanceState) {

		 		super.onCreate(savedInstanceState);
		        setContentView(R.layout.bookshelf);
		        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		
		        
		        CustomApp appState = ((CustomApp)getApplicationContext());
		        shelfUrl = appState.getShelfUrl();
		        unzippedRoot = appState.getUnzippedRoot();
		        packageUrl = appState.getPackageUrl();
		        root = appState.getRoot();
		        unzippedDir = appState.getUnzippedDir();
		        
		        languageSelected  = appState.getLanguage();  
		        Locale locale = new Locale(languageSelected);   
		        Locale.setDefault(locale);  
		        Configuration config = new Configuration();  
		        config.locale = locale;  
		        getBaseContext().getResources().updateConfiguration(config,   
		        getBaseContext().getResources().getDisplayMetrics());  
		        
		        
		        //Get the extras (username) out of the new intent
		        //Retrieve the username.
		        Intent intent = getIntent();
		        if(intent != null) username = intent.getStringExtra("username");
		        //if(intent != null) password = intent.getStringExtra("password");
		        System.out.println("Username = " + username);
		        
		        oc = new OnlineCheck();
					
	            if (oc.isOnline(Shelf.this)){
	            	new Refresh(getResources().getString(R.string.accessing_online_shelf)).execute();	
	            }
	            else{
	            	AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
	                builder.setTitle(getResources().getString(R.string.no_connection));
	                builder.setMessage(getResources().getString(R.string.unable_to_reach));
	                builder.setPositiveButton(getResources().getString(R.string.ok), null);
	                builder.show();
	            }
		        
		        ImageButton refreshButton = (ImageButton)findViewById(R.id.refreshButton);
//		        ImageButton logoutButton = (ImageButton) findViewById(R.id.logoutButton);

		        
		        refreshButton.setOnClickListener(new OnClickListener() {
		        	 @Override
			            public void onClick(View v) {			   	 

				            //Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
				            //vib.vibrate(300);
				            
				            if (oc.isOnline(Shelf.this)){
				        		//v.invalidate();
								new Refresh(getResources().getString(R.string.refreshing)).execute();	
				            }
				            else{
				            	AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
				                builder.setTitle(getResources().getString(R.string.no_connection));
				                builder.setMessage(getResources().getString(R.string.unable_to_reach));
				                builder.setPositiveButton(getResources().getString(R.string.ok), null);
				                builder.show();
				            }
		        	 	}
		        	 });
		        		        
		        		        
/*		        logoutButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
		        	
			            //Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
			            //vib.vibrate(300);
			            
			            //ask user are they sure they want to log out
			            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(Shelf.this);
			            helpBuilder.setTitle(getResources().getString(R.string.sign_out));
			            helpBuilder.setMessage(getResources().getString(R.string.sign_out_q));
			            helpBuilder.setPositiveButton(getResources().getString(R.string.ok),
			              new DialogInterface.OnClickListener() {

			               public void onClick(DialogInterface dialog, int which) {
			            	   
			            	   //clear the preferences
			            	   clearPreferences();
			            	   //switch to login interface
			            	   Intent intent = new Intent();
			   	        	   intent.setClassName("org.placebooks.www", "org.placebooks.www.PlaceBooks");
			   	        	   startActivity(intent);
			   	        	   finish();
			            	   
			               }
			               });
		          	 
			            helpBuilder.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

				             @Override
				             public void onClick(DialogInterface dialog, int which) {
				              //Do nothing
				             }
				            });

				            AlertDialog helpDialog = helpBuilder.create();
				            helpDialog.show();
			               }
			            
			          
			            
					});
*/
			
	 } //end of onCreate
	 
	 public void clearPreferences(){
			
	  	    SharedPreferences prefs = Shelf.this.getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
		    editor.clear();
		    editor.commit();
		     
		}
	 
	 
		//Get the cached shelf from the sdcard
	 public void getCachedShelf(){

		     	json = JSONfunctions.getJSONfromSDCard(unzippedRoot + username + "_shelf.json");
		        System.out.println("Reading the cached shelf on startup");
		        //display the cached shelf
				displayShelf();
	 }
	 
	 
	//If the user name and password are correct then it will get the json file from online and display the placebooks. The user can then download their shelf or a single placebook at a time.
	 public void getLiveShelf(){
				 
	        	String userShelfUrl =  shelfUrl + username;
			    System.out.println("Is Online URL = " + userShelfUrl);
		        SharedPreferences prefs = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
    	        String cookieName = prefs.getString("cookieName", "");
    	        String cookieValue = prefs.getString("cookieValue", "");
    	        int cookieVersion = prefs.getInt("cookieVersion", 0);
    	        String cookieDomain = prefs.getString("cookieDomain", "");
    	        String cookiePath = prefs.getString("cookiePath", "");
    	        this.cookieName = cookieName;//"JSESSIONID"; 
    	        this.cookieVal = cookieValue;//"B34907FAD6CB3E45E8EE45170F08AE0B";
    	        System.out.println("cookieName === " + cookieName + " " + "cookieValue === " +cookieValue + " " + "cookieVersion === " + cookieVersion + " " + "cookieDomain === " + cookieDomain + " " + "cookiePath === " + cookiePath);
    	        
				json = JSONfunctions.getJSONfromURL(userShelfUrl, cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath);
			    
				//And update the view
				//displayShelf();  
				
				//Also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
				downloadFromUrl(userShelfUrl, username + "_shelf.json"); 	
				System.out.println("Reading the shelf from the Internet. Also updating the cached shelf.");	

	 }
	 
	 public void displayShelf(){
		 
		    GridView gridview = (GridView) findViewById(R.id.gridview);		       
	        List<MyListItemModel> myListModel = new ArrayList<MyListItemModel>();

	        try{
	        	
	        	JSONArray entries = json.getJSONArray("entries");
	        	JSONObject jObject = json.getJSONObject("user");
	        	
		        for(int i=0;i<entries.length();i++){						
				
		        	final MyListItemModel item = new MyListItemModel(this);
		        	JSONObject e = entries.getJSONObject(i);
		        	
		        	String state = e.optString("state");
		        	if(state.equalsIgnoreCase("0")){	//UNPUBLISHED
		        	
		        		String bookTitle = e.optString("title");
		        		if (bookTitle.equalsIgnoreCase("null") || bookTitle.equals("")){
				        	item.setTitle("Untitled Book");
		        		}
		        		else{
				        	item.setTitle(e.optString("title"));	//Book title
		        		}
		           		item.setID(i);	//Owner 
			        	item.setKey(e.getString("key"));   //Book key
			        	item.setDescription(e.optString("description"));	//Book description
			        	item.setPackagePath(e.getString("packagePath"));
			        	
			        	//item.setOwner(Integer.parseInt(jObject.getString("owner"))); 
			        	//item.setTimestamp(Integer.parseInt(jObject.getString("timestamp")));
			        	//item.setPreviewImage(e.getString("previewImage"));
			        	//item.setNumItems(Integer.parseInt(jObject.getString("numItems")));
		       		
		        	 item.dl_listener = new OnClickListener(){
			        	public void  onClick  (View  v){
			        		
				            //Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
				            //vib.vibrate(300);
			        	
			            	if (oc.isOnline(Shelf.this)){

				        		SDCardCheck sdcardcheck = new SDCardCheck();
				        		//If the sdcard is mounted then download
				        		if (sdcardcheck.isSdPresent()){
				        			 //Placebook does not exist on sdcard so download it.
				        			 //Call the download method and pass it the book key and package path
				        			downloadPlaceBook(item.getKey(), item.getPackagePath(), item.getBookTitle(), item.getDescription() );
				        		}
				        		else{
				        			//No sdcard
				        			Log.d("Shelf", "No SDCARD");
				        		       
				                	AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
				                	builder.setTitle(getResources().getString(R.string.no_sdcard));
				                	builder.setMessage(getResources().getString(R.string.no_sdcard_download));
				                	builder.setPositiveButton(getResources().getString(R.string.ok), null);
				                	builder.show();
				        			
				        		}
			            	}
			            	else{
			            		AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
			                	builder.setTitle(getResources().getString(R.string.no_connection));
			                	builder.setMessage(getResources().getString(R.string.unable_connect_internet));
			                	builder.setPositiveButton(getResources().getString(R.string.ok), null);
			                	builder.show();
			            	}

					     } 
					   };
				

		        	myListModel.add(item);	//Add the item to the arraylist of ListItems

		        		}
		        	
			        	if(jObject.getString("name") != null){
			        		shelfOwner = jObject.getString("name");  //owner's name
			        	}
			        	
		        	
	        		}

		        	}catch(JSONException e) {
		        		Log.e("log_tag", "Error parsing data "+e.toString());
		        	}
		 
		 
		    //Code for the new bookshelf using a gridview   	
		    ImageAdapter adapter = new ImageAdapter(this);
		    adapter.setModel(myListModel);		//pass the ArrayList into the Adapter
		    adapter.setUnzippedDir(unzippedDir);
	        gridview.setAdapter(adapter);
	        
	        
	 }
	 
	 
	 /*
	  * Method for downloading the shelf JSON file to the SDCard for caching.
	  * Used for the initial download and also caching.
	  */
	 public void downloadFromUrl(String DownloadUrl, String fileName) {

		 
		 
		   try {

		           File dir = new File(unzippedRoot);
		           if(dir.exists()==false) {
		                dir.mkdirs();
		           }

		           URL url = new URL(DownloadUrl);
		           File file = new File(dir, fileName);

		           long startTime = System.currentTimeMillis();
		           Log.d("Shelf", "download begining");
		           Log.d("Shelf", "download url:" + url);
		           Log.d("Shelf", "downloaded file name:" + fileName);
		           
		           //Open a connection to that Url
		           URLConnection ucon = url.openConnection();

		           //Define InputStreams to read from the UrlConnection.
		           InputStream is = ucon.getInputStream();
		           BufferedInputStream bis = new BufferedInputStream(is);

		           //Read bytes to the buffer until there is nothing more to read(-1).
		           ByteArrayBuffer baf = new ByteArrayBuffer(5000);
		           int current = 0;
		           while ((current = bis.read()) != -1) {
		              baf.append((byte) current);
		           }

		           //Convert the bytes read to a String
		           FileOutputStream fos = new FileOutputStream(file);
		           fos.write(baf.toByteArray());
		           fos.flush();
		           fos.close();
		           Log.d("Shelf", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		   } catch (IOException e) {
		       Log.d("Shelf", "Error: " + e);
		   }

		}	 

	 /*
	  * Download the zip file package
	  */
	 public void downloadPlaceBook(String packageKey, String downloadPath, String title, String description) {

		 
		 String url = packageUrl + packageKey;
		 new DownloadFileAsync(downloadPath, packageKey, title, description).execute(url);	
		
	    }
	 		
			 @Override
			    public void onConfigurationChanged (Configuration newConfig){
			    	super.onConfigurationChanged(newConfig);
			    }

			    
			    @Override
			    protected Dialog onCreateDialog(int id) {
			        switch (id) {
			            case dialogDownloadProgress:
			                mProgressDialog = new ProgressDialog(this);
			                mProgressDialog.setMessage(getResources().getString(R.string.downloading_placebook));
			                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			                mProgressDialog.setCancelable(true);
			                mProgressDialog.show();
			                return mProgressDialog;
			            default:
			                return null;
			        }
			    }
			    
		   public class DownloadFileAsync extends AsyncTask<String, String, String> {
			   String packagePath;
			   String packageKey;
			   String title;
			   String description;

			  
			   public DownloadFileAsync(String dlPath, String key, String ti, String des){
				   packagePath = dlPath;
				   packageKey = key;
				   title = ti;
				   description = des;
			   }
			   
		        @Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            showDialog(dialogDownloadProgress);
		        }

		        @Override
		        protected String doInBackground(String... aurl) {
		            
		        	String filepath=null;

		            try {
		                URL url = new URL(aurl[0]);
		               
		                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();    
		                urlConnection.setRequestMethod("GET");
		                //urlConnection.setDoOutput(true); 
		                urlConnection.setDoInput(true); 
		                System.out.println("Cookie:"+cookieName+"="+ cookieVal);
		                urlConnection.setRequestProperty("Cookie:" , cookieName + "="+ cookieVal);
		                urlConnection.connect();
		                
		               		                

		                int lenghtOfFile = urlConnection .getContentLength();
		                Log.d("Shelf", "Lenght of file: " + lenghtOfFile);
		       
		                //Create a new file, specifying the path, and the filename
		                //which we want to save the file as.
		                Log.i("Local filename:",""+filename);
		                file = new File(root,filename);

		                if(file.createNewFile())
		                {
		                  file.createNewFile();
		                }

		                //This will be used to write the downloaded data into the file we created
		                FileOutputStream fileOutput = new FileOutputStream(file);
		                //This will be used in reading the data from the internet
		                InputStream inputStream = urlConnection.getInputStream();         
		                               
		                //This is the total size of the file
		                int totalSize = urlConnection.getContentLength();
		                //Variable to store total downloaded bytes
		                int downloadedSize = 0;                

		                //Create a buffer...
		                byte[] buffer = new byte[1024];
		                int bufferLength = 0; //used to store a temporary size of the buffer
		                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
		              	  
		                    //Add the data in the buffer to the file in the file output stream (the file on the sd card)
		                    fileOutput.write(buffer, 0, bufferLength);
		                    //Add up the size so we know how much is downloaded
		                    downloadedSize += bufferLength;
		                    publishProgress(""+(int)((downloadedSize*100)/lenghtOfFile));
		                    
		                    Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;

		                   }
		                     
		                fileOutput.flush();
		                fileOutput.close();
		                
		                //Location of the downloaded .zip file on the sd card AND unzip file path (where to unzip)
		                String zipFileLocation = (root + file.separator +filename);
		                String unzipPath = (unzippedRoot);
		                //Pass these values to the unzipper method in the decompress class
		                File fIn = new File(zipFileLocation);
		        	    File fOut = new File(unzipPath);
		                
			                try {
			        		    Decompress.unzip(fIn, fOut);
			        	    }
			        		catch (ZipException e) 
			        		{
			        		        e.printStackTrace();
			        		} catch (IOException e) {
			        		     e.printStackTrace();
			        		}
			      			                
		                
		                if(downloadedSize==totalSize)   
		                 filepath=file.getPath();
		                //reload();
		               
		                
		                
		            } catch (MalformedURLException e) {
		            	
		            	e.printStackTrace();
		            }
		            catch (IOException e) {
		            	
		            	e.printStackTrace();
		            }
		            
		            catch (Exception e) {}
		            
		            Log.i("filepath:", " " +filepath);
		            
		            return filepath;
		            
		        }
		        protected void onProgressUpdate(String... progress) {
		             Log.d("ANDRO_ASYNC",progress[0]);
		             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		        }

		        @Override
		        protected void onPostExecute(String unused) {
		            dismissDialog(dialogDownloadProgress);
		            File fiOut = new File(unzippedDir + packagePath);
	                if(fiOut.exists()){
			            //write the book to the user's downloads.json file
			            appendToDownloadsFile(packageKey, packagePath, title, description);
	                }
				    switchTabInActivity(0);	//switch to the first tab (downloads tab)

		            
		        }
		    }
		   
		   public void appendToDownloadsFile (String key, String path, String title, String description) {
			   

			      BufferedWriter bw = null;
			      //jsonDownloads = JSONfunctions.getJSONfromSDCard(unzippedRoot + username + "_downloads.json");
			      JSONObject jsonObject = new JSONObject();


			     try {
				     bw = new BufferedWriter(new FileWriter(unzippedRoot + username + "_downloads.json", true));
				     //JSONArray jsonArray = new JSONArray();//new JSONArray();
				     
				    
					 jsonObject.put("title", title);
				     jsonObject.put("description", description);
				     jsonObject.put("packagePath", path);
				     //jsonObject.put("\"numItems\" : ", "test4");
				     //jsonObject.put("\"previewImage\" : ", "test5");
				     jsonObject.put("key", key);
				     //jsonObject.put("\"owner\" : ", "test7");
				     //jsonObject.put("\"timestamp\" : ", "test8");
				     
				     //jsonArray.put(jsonObject);
				     String output = jsonObject.toString();//jsonArray.toString();
				     bw.write(output);
			         bw.newLine();
			         bw.close();
			      } 
			     catch (IOException ioe) {
			    	  ioe.printStackTrace();
			      } 
			      catch(JSONException jsone) {
			    	  jsone.printStackTrace();
			      } 
			     finally {               
				    	 // always close the file
					 if (bw != null) try {
					    bw.close();
					 }
					 catch (IOException ioe2) {
					    //just ignore it
					 }
			     }

			   }
			   			   
			   
			   /*
			    * Reload method for reloading the activity
			    */
			   //@Override
			   //public void onRestart(){
			   public void reload() {
				   
				    Intent intent = getIntent();
				    overridePendingTransition(0, 0);
				    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				    finish();

				    overridePendingTransition(0, 0);
				    startActivity(intent);
				
	        		//intent.setClassName("org.placebooks.www", "org.placebooks.www.BookDownloads");
	        		//startActivity(intent);	
				   
			   
			   }

			   
			   /*
			   *Quit the app on back press. User will already be logged in and
			   *their credentials saved. Therefore there is no reason to go back
			   *because we do not need to log in screen anymore.
			   */
			   @Override
			   public boolean onKeyDown(int keyCode, KeyEvent event) {
			       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			           Log.d(this.getClass().getName(), "back button pressed");
			          this.finish();
			       }
			       return super.onKeyDown(keyCode, event);
			   }
			   
			   /*
			    * NEW METHOD
			    * Instead of quiting the app on 'back' press, we  just disable it and make the button redundant here.
			    * The reason behind this is it keeps the bookshelf in memory, so the next time the user opens the app
			    * it won't have to load the shelf all over again.
			    */
			   @Override
			   public void onBackPressed() {

			      return;
			   }
			   
			
			   
			   
			   
			   private class Refresh extends AsyncTask<Void, Void, Void> {
					
				   String message;
				   
				   public Refresh(String m){
					   message = m;
				   }

				   @Override
			        protected void onPreExecute() {
			            super.onPreExecute();
			            //refreshDialog.show();
				       	//refreshDialog = ProgressDialog.show( Shelf.this, " " , "Accessing online shelf.. ", true);				   	 
			            refreshDialog = new ProgressDialog(Shelf.this);
			            refreshDialog.setMessage(message);
			            refreshDialog.setIndeterminate(true);
			            refreshDialog.setCancelable(false);
			            refreshDialog.show();
			        }
					 
					@Override 
					protected Void doInBackground(Void... strings) {
												
						        try{	
								    //try accessing the live shelf if available
			                	    getLiveShelf();

					             }
					             catch(Exception e){
					            	 e.printStackTrace();
					            	 refreshDialog.dismiss();  
					             }
					             
					    
						return null;
				 	}
				 	
					 
					   @Override
				        protected void onPostExecute(Void unused) {
						    //now display the shelf
						   	displayShelf();
				            refreshDialog.dismiss();
				            
				        }
				 
				
				     }
			   
			   public void switchTabInActivity(int indexTabToSwitchTo){
		            TabLayoutActivity ParentActivity;
		            ParentActivity = (TabLayoutActivity) this.getParent();
		            ParentActivity.getTabHost().setCurrentTab(indexTabToSwitchTo);//switchTab(indexTabToSwitchTo);
			   }
			   
			   

}	//End of public shelf

