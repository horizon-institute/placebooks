package org.placebooks.www;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.placebooks.www.Shelf.DownloadFileAsync;

import android.app.ListActivity;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener; 
import android.util.Log;
import android.app.AlertDialog;
import android.content.Intent;
import java.util.zip.ZipException;

import android.view.KeyEvent;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;

import org.apache.http.cookie.Cookie;
import org.apache.http.util.ByteArrayBuffer;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.content.SharedPreferences.Editor;
import android.widget.ProgressBar;
import android.view.LayoutInflater;


public class Search extends ListActivity  {
		
	private JSONObject json;
    private ListView lv;
	
	//-- Download variables --
	private File file;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String filename= "downloadFile.zip";   // you can download to any type of file ex:.jpeg (image) ,.txt(text file),.mp3 (audio file)
    //-- Download Variables --
    
    private String root;
    private String unzippedDir;
    private String packageUrl;
    private String unzippedRoot;
    
    private String cookieName;
    private String cookieVal;

	//The phones current longitude and latitude
	private double longitude;
	private double latitude;
	
	ProgressDialog searchDialog;
	private OnlineCheck oc;
	
	private String username;
	
	private int distanceEntered = 10;
	//private int bookCount = 0;
	
	private LocationManager locationManager;
	private String provider;
	
	private String distanceItem;
	private String activityItem;
		
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen	
        
        
        
        //Get the current lon/lat from the phone
        //and append it to the POINT parameters in the url
        //http://www.placebooks.org/placebooks/placebooks/a/admin/location_search/placebook/POINT(41 -4.20)
        //http://www.placebooks.org/placebooks/placebooks/a/admin/location_search/placebookbinder/POINT%20(51%20-4.2)
        
        //Parse the JSON response into a list view
    
        
        CustomApp appState = ((CustomApp)getApplicationContext());
        //shelfUrl = appState.getShelfUrl();
        unzippedRoot = appState.getUnzippedRoot();
        packageUrl = appState.getPackageUrl();
        root = appState.getRoot();
        unzippedDir = appState.getUnzippedDir();
        
        SharedPreferences prefs = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
        cookieName = prefs.getString("cookieName", "");
        cookieVal = prefs.getString("cookieValue", "");
        
        Intent intent = getIntent();
        if(intent != null) username = intent.getStringExtra("username");
        System.out.println("Username = " + username);
        if(intent != null) distanceItem = intent.getStringExtra("distanceItem");
        if(intent != null) activityItem = intent.getStringExtra("activityItem");
                

        if (distanceItem == "Up to 10 Miles")
			//search(10);
			distanceEntered = 10;
		else if (distanceItem == "Up to 25 Miles")
			distanceEntered = 25;
			//search(25);
		else if (distanceItem == "Up to 50 Miles")
			distanceEntered = 50;
		else if (distanceItem == "Everywhere")
			distanceEntered = 100000000;

    
        
        oc = new OnlineCheck();	
        
        MyListAdapter adapter = new MyListAdapter(this);
        int a = adapter.getCount();
        
        
      //Get the mobile's current longitude and latitude
	    //Find best location provider that features high accuracy and draws as little power as possible
	    String context = Context.LOCATION_SERVICE;
	    locationManager = (LocationManager)getSystemService(context);	//Finds your current location
	    
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(true);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
   	
        provider = locationManager.getBestProvider(criteria, true);
        
        
        if (provider != null){

             //search();
        	new AttemptSearch().execute();

    	}

        
        ImageButton logoutButton = (ImageButton) findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
        	
	            //Vibrator vib = (Vibrator) getSystemService(TabGroupActivity.VIBRATOR_SERVICE);
	            //vib.vibrate(300);
	            
	            //ask user are they sure they want to log out
	            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(getParent());
	            helpBuilder.setTitle("Logout");
	            helpBuilder.setMessage("Are you sure you want to logout?");
	            helpBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {

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
          	 
	            helpBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

		             @Override
		             public void onClick(DialogInterface dialog, int which) {
		              //Do nothing
		             }
		            });

		            AlertDialog helpDialog = helpBuilder.create();
		            helpDialog.show();
	               }
	            
	            
			});

    }
	
	public void clearPreferences(){
		
  	    SharedPreferences prefs = getParent().getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
	    editor.clear();
	    editor.commit();
	     
	}

	private class AttemptSearch extends AsyncTask<Void, Void, Void> {
		  
		   AlertDialog.Builder builder;
		   //String uname;
		   //String disItem;
		   //String actItem;
		   String message;

		   
		   public AttemptSearch(){
			  /*uname = username;
			   disItem = distanceItem;
			   actItem = activityItem;*/
			   message = "Searching..";
		   }

		   @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	        	//progress dialog for logging in (gives user feedback)
	            searchDialog = new ProgressDialog(getParent());
	            searchDialog.setMessage(message);
	            searchDialog.setIndeterminate(true);
	            searchDialog.setCancelable(false);
	            searchDialog.show();

	        }
			 
			@Override 
			protected Void doInBackground(Void... strings) {
				
				try{
					 if (provider != null){
							
						    Location location = locationManager.getLastKnownLocation(provider);
						    updateWithNewLocation(location);
						    //locationManager.requestLocationUpdates(provider, 0, 0, locationListener);	
		           }
		           else{
		           	   //Alert user that GPS is turned off and they will not be able to see tracking
		        	   Toast msg = Toast.makeText(getParent(), "Please turn on GPS to search for Placebooks around you", Toast.LENGTH_LONG);
		   			   msg.show();
		           }
		        }
		        catch(Exception e){	 
		        	System.out.println("Exception " + e.toString());
					TextView txtView = new TextView(Search.this);
					txtView.setText("Exception " +e.toString());
					setContentView(txtView);
		        }
				
				return null;

			}
	
			@Override
	        protected void onPostExecute(Void unused) {
                searchDialog.dismiss();
				displayList(longitude,latitude);
			}
	
	}

	
	
	public void displayList(double lon, double lat){
		
		
		//bookCount = 0;
		//String searchUrl =  "http://www.placebooks.org/placebooks/placebooks/a/admin/location_search/placebook/POINT("+lat +"%20"+lon+")";
		//String searchUrl =  "http://www.placebooks.org/placebooks/placebooks/a/admin/location_search/placebookbinder/POINT(51%20-4.2)";
		String searchUrl =  "http://www.placebooks.org/placebooks/placebooks/a/admin/location_search/placebookbinder/POINT("+lat+ "%20"+lon+")";
		System.out.println("latitude ===="+lat+" ; longitude===="+lon);
		System.out.println("Is Online URL = " + searchUrl);
        SharedPreferences prefs = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
        String cookieName = prefs.getString("cookieName", "");
        String cookieValue = prefs.getString("cookieValue", "");
        int cookieVersion = prefs.getInt("cookieVersion", 0);
        String cookieDomain = prefs.getString("cookieDomain", "");
        String cookiePath = prefs.getString("cookiePath", "");
        
		json = JSONfunctions.getJSONfromURL(searchUrl, cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath);
		
		//Might need to add some extra info into the list model class such as Distance once it has been calculated
        List<MyListItemModel> myListModel = new ArrayList<MyListItemModel>();
        OnlineCheck oc = new OnlineCheck();		       

		//Check if we have an online connection first
        if (oc.isOnline(this)){
        	
        	try{
	        	
	        	JSONArray entries = json.getJSONArray("entries");
	        	//JSONObject u = json.getJSONObject("user");
	        	
		        for(int i=0;i<entries.length();i++){						
				
		        	final MyListItemModel item = new MyListItemModel(this);
		        	JSONObject e = entries.getJSONObject(i);
		        	
		        	Double bookDistance = e.getDouble("distance")*100;
		        	String activity = e.getString("activity");
		        	
		        	System.out.println("activity item === " +activityItem + "    activity ==== " +activity);
		        	System.out.println("book distance === " +bookDistance + "      distanceEntered === " +distanceEntered);
			        if (bookDistance < distanceEntered && activityItem.equals(activity)){
			        		
				        	item.setDistance(e.getDouble("distance"));
				        	//item.setID(i);		//Owner ID
				        	item.setKey(e.optString("key"));	//book key key
				        	item.setTitle(e.optString("title"));	//book title
				        	item.setDescription(e.optString("description"));	//book description
				        	item.setPackagePath(e.optString("packagePath"));
			        	

		        	//taken out for now
		        	//item.setOwner(u.getString("name"));  //book owner name e.g stuart
		        		
		        	 item.dl_listener = new OnClickListener(){
			        	public void  onClick  (View  v){
			        		
			        		SDCardCheck sdcardcheck = new SDCardCheck();
			        		//if the sdcard is mounted then download
			        		if (sdcardcheck.isSdPresent()){
    				            //Vibrator vib = (Vibrator) getSystemService(Search.this.VIBRATOR_SERVICE);
    				            //vib.vibrate(300);
			        			
			        			 //placebook does not exist on sdcard so download it.
			        			 //call the download method and pass it the book key and package path
			        			downloadPlaceBook(item.getKey(), item.getPackagePath(), item.getBookTitle(), item.getDescription());
			        		}
			        		else{
			        			//no sdcard
			        			Log.d("MyApp", "No SDCARD");
			        		       
			                	AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
			                	builder.setTitle("No SD Card!");
			                	builder.setMessage("There is no sd card mounted to this device! You need an sd card to download a Placebook!");
			                	builder.setPositiveButton("OK", null);
			                	builder.show();
			        			
			        		}

					     } 
					   };
					   
					 item.view_listener = new OnClickListener(){
				       public void  onClick  (View  v){
				        						        		
						         //Vibrator vib = (Vibrator) getSystemService(Search.this.VIBRATOR_SERVICE);
						         //vib.vibrate(300);
				            
				        		 //placebook exists on sdcard so view it
				        		 //call to viewPlacebook();
				        		 
				        		Intent intent = new Intent();

				        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
				        		
				        		intent.putExtra("packagePath", item.getPackagePath());
				        		startActivity(intent);	
				        		
				    	  

						     } 
						   };
					   
		        	
		        	myListModel.add(item);	//add the item to the arraylist of ListItems
		        	//bookCount++;
			        }	//end of if bookDistance
			        
			        else if (bookDistance < distanceEntered && activityItem == "<No Activity>"){
			        	
							      		
							        	item.setDistance(e.getDouble("distance"));
							        	//item.setID(i);		//Owner ID
							        	item.setKey(e.optString("key"));	//book key key
							        	item.setTitle(e.optString("title"));	//book title
							        	item.setDescription(e.optString("description"));	//book description
							        	item.setPackagePath(e.optString("packagePath"));
						        	
				
					        	//taken out for now
					        	//item.setOwner(u.getString("name"));  //book owner name e.g stuart
					        		
					        	 item.dl_listener = new OnClickListener(){
						        	public void  onClick  (View  v){
						        		
						        		SDCardCheck sdcardcheck = new SDCardCheck();
						        		//if the sdcard is mounted then download
						        		if (sdcardcheck.isSdPresent()){
								            //Vibrator vib = (Vibrator) getSystemService(Search.this.VIBRATOR_SERVICE);
								            //vib.vibrate(300);
						        			
						        			 //placebook does not exist on sdcard so download it.
						        			 //call the download method and pass it the book key and package path
						        			downloadPlaceBook(item.getKey(), item.getPackagePath(), item.getBookTitle(), item.getDescription());
						        		}
						        		else{
						        			//no sdcard
						        			Log.d("MyApp", "No SDCARD");
						        		       
						                	AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
						                	builder.setTitle("No SD Card!");
						                	builder.setMessage("There is no sd card mounted to this device! You need an sd card to download a Placebook!");
						                	builder.setPositiveButton("OK", null);
						                	builder.show();
						        			
						        		}
				
								     } 
								   };
								   
								 item.view_listener = new OnClickListener(){
							       public void  onClick  (View  v){
							        						        		
									         //Vibrator vib = (Vibrator) getSystemService(Search.this.VIBRATOR_SERVICE);
									         //vib.vibrate(300);
							            
							        		 //placebook exists on sdcard so view it
							        		 //call to viewPlacebook();
							        		 
							        		Intent intent = new Intent();
				
							        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
							        		
							        		intent.putExtra("packagePath", item.getPackagePath());
							        		startActivity(intent);	
							        		
							    	  
				
									     } 
									   };
								   
					        	
					        	myListModel.add(item);	//add the item to the arraylist of ListItems
	        		
			        	
			        	
			        }
		        
		        	}  //end of for int i=0

		        	}catch(JSONException e) {
		        		Log.e("log_tag", "Error parsing data "+e.toString());
		        	}
		        	
		        	
		        	MyListAdapter adapter = new MyListAdapter(this);	//create the Adapter
		        	adapter.setModel(myListModel);		//pass the ArrayList into the Adapter
		        	adapter.setUnzippedDir(unzippedDir);
		        	setListAdapter(adapter);			//assign Adapter
		        	int size = adapter.getCount();
		        	System.out.println("list view size ==== "+ size);
		        	
		        	if (size >0 ){
			        	lv = getListView();				//call the ListView
			        	lv.setTextFilterEnabled(true);  //enables filtering for the contents of the ListView.
		        	}
		        	else{
		        		TextView tv = (TextView) findViewById(R.id.empty);
		        		tv.setText("0 search results found");
		        	}
		        	
		        	//Toast message telling the user how many books were found
		        	//Toast bookResultsMsg = Toast.makeText(Search.this, Integer.toString(bookCount) + " books found", Toast.LENGTH_SHORT);
		        	//bookResultsMsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		        	//bookResultsMsg.show();
		        	//bookCount = 0;	//reset the bookCount var
        	
        } //end of isOnline
        
		
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
			            case DIALOG_DOWNLOAD_PROGRESS:
			                mProgressDialog = new ProgressDialog(getParent());
			            	//mProgressDialog = ProgressDialog.show(getParent(), "Downloading Placebook..");
			                mProgressDialog.setMessage("Downloading Placebook..");
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
				   
				  
				   public DownloadFileAsync(String dlPath, String key, String ti, String desc){
					   packagePath = dlPath;
					   packageKey = key;
					   title = ti;
					   description = desc;
					   
				   }
				   
			        @Override
			        protected void onPreExecute() {
			            super.onPreExecute();
			            showDialog(DIALOG_DOWNLOAD_PROGRESS);
			            
			        }

			        @Override
			        protected String doInBackground(String... aurl) {
			           
			        	String filepath=null;

			            try {
			                URL url = new URL(aurl[0]);
			                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			                urlConnection.setRequestMethod("GET");
			                urlConnection.setDoOutput(true); 
			                System.out.println("Cookie:"+cookieName+"="+ cookieVal);
			                urlConnection.setRequestProperty("Cookie:" , cookieName + "="+ cookieVal);
			                urlConnection.connect();

			                int lenghtOfFile = urlConnection .getContentLength();
			                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);


	
			                Log.i("Local filename:",""+filename);
			                file = new File(root,filename);

			                
			                if(file.createNewFile())
			                {
			                 file.createNewFile();
			                }

			                //this will be used to write the downloaded data into the file we created
			                FileOutputStream fileOutput = new FileOutputStream(file);

			                //this will be used in reading the data from the internet
			                InputStream inputStream = urlConnection.getInputStream();         
			                               
			                //this is the total size of the file
			                int totalSize = urlConnection.getContentLength();
			                //variable to store total downloaded bytes
			                int downloadedSize = 0;                

			                //create a buffer...
			                byte[] buffer = new byte[1024];
			                int bufferLength = 0; //used to store a temporary size of the buffer

			                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			              	  
			                    //add the data in the buffer to the file in the file output stream (the file on the sd card
			                    fileOutput.write(buffer, 0, bufferLength);
			                    //add up the size so we know how much is downloaded
			                    downloadedSize += bufferLength;
			                    publishProgress(""+(int)((downloadedSize*100)/lenghtOfFile));
			                    
			                    //this is where you would do something to report the progress, like this maybe
			                    Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;

			                   }
			                     
			                fileOutput.flush();
			                fileOutput.close();
			                
			                // location of the downloaded .zip file on the sd card AND unzip file path (where to unzip)
			                String zipFileLocation = (root + "/" +filename);
			                String unzipPath = (unzippedRoot);
			                File fIn = new File(zipFileLocation);
			        	    File fOut = new File(unzipPath);
			                
				                try {
				        		    Decompress.unzip(fIn, fOut);
				        	    }
				        		catch (ZipException e) 
				        		{
				        		      // TODO Auto-generated catch block
				        		        e.printStackTrace();
				        		} catch (IOException e) {
				        		       // TODO Auto-generated catch block
				        		     e.printStackTrace();
				        		}
				      			                
			                
			                if(downloadedSize==totalSize){   
			                filepath=file.getPath();
			                }
			                //reload();
			                //onRestart();
			                
			            //catch some possible errors...  
			            } catch (MalformedURLException e) {
			            	
			            	e.printStackTrace();
			            }
			            catch (IOException e) {
			            	
			            	e.printStackTrace();
			            }
			            
			            catch (Exception e) {}
			            
			            Log.i("filepath:", " " +filepath);
			            
			            //return null;
			            return filepath;
			            
			        }
			        protected void onProgressUpdate(String... progress) {
			             Log.d("ANDRO_ASYNC",progress[0]);
			             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
			        }

			        @Override
			        protected void onPostExecute(String unused) {
			          dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			            //check if the file was downloaded
			            File fiOut = new File(unzippedDir + packagePath);
		                if(fiOut.exists()){
				            //if file downloaded then write the book to the user's downloads.json file
				            appendToDownloadsFile(packageKey, packagePath, title, description);
		                }
			            //write the book to the user's downloads.json file
			            //appendToDownloadsFile(packageKey, packagePath, title, description);
	//TAKEN OUT				    switchTabInActivity(0);	//switch to the first tab (downloads tab)

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
				          //this.finish();
				           //do nothing
				       }
				       return super.onKeyDown(keyCode, event);
				   }
				   
				   

				   
				   private void updateWithNewLocation(Location location){
						
						String latLongString;
						
						if(location!=null){
							//update the latitude and longitude variables
						    longitude = location.getLongitude();
						    latitude = location.getLatitude();
						    latLongString = "Lat: " +latitude + "\nLong: " + longitude;
				
						}
						else{
							latLongString = "No location found";
						}
						

					}
					
					//listens out for changes in gps coordinates		
					private final LocationListener locationListener = new LocationListener(){
						public void onLocationChanged(Location location){
							//update application based on new location
							updateWithNewLocation(location);

							longitude = location.getLongitude();
						    latitude = location.getLatitude();
						    									
							//displayList(longitude, latitude);
	//TAKEN OUT			    displayList(longitude,latitude);
						    //searchDialog.dismiss();

						}
						
						public void onProviderDisabled(String provider){
							updateWithNewLocation(null);
						}
						
						public void onProviderEnabled(String provider){
							//Update application if provider enabled
						}
						public void onStatusChanged(String provider, int status, Bundle extras){
							//Update application if provider hardware status changed
						}
					};
					
					public void switchTabInActivity(int indexTabToSwitchTo){
			            TabLayoutActivity ParentActivity;
			            ParentActivity = (TabLayoutActivity) this.getParent();
			            ParentActivity.getTabHost().setCurrentTab(indexTabToSwitchTo);//switchTab(indexTabToSwitchTo);
				   }

					
					
}
