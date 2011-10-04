package org.placebooks.www;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
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
import org.apache.http.util.ByteArrayBuffer;
import java.io.IOException;
import android.view.View.OnClickListener; 
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.os.Handler;
import android.os.Message;



public class Shelf extends Activity/*extends ListActivity*/{
	
	private ProgressDialog myDialog = null;
	
	private Button onlineButton;
	private Button offlineButton;
	private OnlineCheck oc;
	
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
		        unzippedDir = appState.getRoot();
		        
		        //Get the extras (username) out of the new intent
		        //Retrieve the username.
		        Intent intent = getIntent();
		        if(intent != null) username = intent.getStringExtra("username");
		        //if(intent != null) password = intent.getStringExtra("password");
		        System.out.println("Username = " + username);
		        		        
		        //Load cached shelf on startup and then if there is an Internet connection, load the latest shelf
	        	getCachedShelf();


		        offlineButton = (Button)findViewById(R.id.offlineButton);
		        onlineButton = (Button)findViewById(R.id.onlineButton);

		        oc = new OnlineCheck();	
		        

		        onlineButton.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {

		        		//Vibration to alert users
			            //Get instance of Vibrator from current Context
			            Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
			            //Vibrate for 300 milliseconds
			            vib.vibrate(300);
		             try{	
		            	if (oc.isOnline(Shelf.this)){
		            		 
							//try accessing the live shelf if available
							getLiveShelf();
				        	offlineButton.setVisibility(View.VISIBLE);
							onlineButton.setVisibility(View.GONE);	
							Toast msg = Toast.makeText(Shelf.this, "Online Shelf", Toast.LENGTH_LONG);
							msg.show();
						 }
		            	
		            	else if(!oc.isOnline(Shelf.this)){
							 //get cached shelf
							 displayShelf();
							 
							 AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
			                 builder.setTitle("No Internet Connection");
			                 builder.setMessage("Unable to get online book shelf");
			                 builder.setPositiveButton("OK", null);
			                 builder.show();
						 }
		             }
		             catch(Exception e){
		            	 e.printStackTrace();
		             }

		            }
		        });
		        
		        offlineButton.setOnClickListener(new OnClickListener() {

		            @Override
		            public void onClick(View v) {
		            	
		        		//Vibration to alert users
			            //Get instance of Vibrator from current Context
			            Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
			            //Vibrate for 300 milliseconds
			            vib.vibrate(300);
			            			            
							 getCachedShelf();
							 offlineButton.setVisibility(View.GONE);
							 onlineButton.setVisibility(View.VISIBLE);
							 Toast msg2 = Toast.makeText(Shelf.this, "Offline Shelf", Toast.LENGTH_LONG);
							 msg2.show();
		            }
		        });
		        
				 
		        //Now set the shelf title to the user's name
		        TextView shelfTitle = (TextView)findViewById(R.id.shelfTitle);
		        if (shelfOwner!=null){
		        	shelfTitle.setText(("  " + shelfOwner + "'s " + "Book Shelf").toUpperCase());
		        }
		        else{
		        	//leave empty
		        	shelfTitle.setText("");
		        }
		        
			
	 } //end of onCreate
	 
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
				json = JSONfunctions.getJSONfromURL(userShelfUrl);
			    
				//And update the view
				displayShelf();
				
				//Also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
				DownloadFromUrl(userShelfUrl, username + "_shelf.json"); 	
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
		        	
		        	String state = e.getString("state");
		        	if(state.equalsIgnoreCase("UNPUBLISHED")){
		        	
		        		String bookTitle = e.getString("title");
		        		if (bookTitle.equalsIgnoreCase("null")){
				        	item.setTitle("Untitled Book");
		        		}
		        		else{
				        	item.setTitle(e.getString("title"));	//Book title
		        		}
		           		item.setID(i);	//Owner ID
			        	item.setKey(e.getString("key"));   //Book key
			        	item.setDescription(e.getString("description"));	//Book description
			        	item.setPackagePath(e.getString("packagePath"));
			        	//item.setOwner(jObject.getString("name"));  //owner's name
		       		
		        	 item.dl_listener = new OnClickListener(){
			        	public void  onClick  (View  v){
			        		
			        		//Vibration to alert users
				            //Get instance of Vibrator from current Context
				            Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
				            // Vibrate for 300 milliseconds
				            vib.vibrate(300);
			        	
				            
			        		SDCardCheck sdcardcheck = new SDCardCheck();
			        		//If the sdcard is mounted then download
			        		if (sdcardcheck.isSdPresent()){
			        			 //Placebook does not exist on sdcard so download it.
			        			 //Call the download method and pass it the book key and package path
			        			downloadPlaceBook(item.getKey(), item.getPackagePath() );
			        		}
			        		else{
			        			//No sdcard
			        			Log.d("Shelf", "No SDCARD");
			        		       
			                	AlertDialog.Builder builder = new AlertDialog.Builder(Shelf.this);
			                	builder.setTitle("No SD Card!");
			                	builder.setMessage("There is no sd card mounted to this device! You need an sd card to download a placebook!");
			                	builder.setPositiveButton("OK", null);
			                	builder.show();
			        			
			        		}

					     } 
					   };
				
		 
					 item.view_listener = new OnClickListener(){
				       public void  onClick  (View  v){
				          //Placebook exists on sdcard so view it
				          //Call to viewPlacebook();
				    	   
				    	   myDialog = ProgressDialog.show(Shelf.this, " " , " Opening.. ", true);				   	 
        		        	
        		        	new Thread() {
        		        	public void run() {
        		        		try{
        		        			//Vibration to alert users
        				            //Get instance of Vibrator from current Context
        				            Vibrator vib = (Vibrator) getSystemService(Shelf.this.VIBRATOR_SERVICE);
        				            //Vibrate for 300 milliseconds
        				            vib.vibrate(300);
        		        			
					        		Intent intent = new Intent();
					        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
					        		intent.putExtra("packagePath", item.getPackagePath());
					        		startActivity(intent);	
        				            
        		        		}
        		        		catch (Exception e) { 
        		        			
        		        		}
 	                           //Dismiss the Dialog
 	                           myDialog.dismiss();     
				        		
        		        	}
        		        	}.start();     	
				        		
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
	 public void DownloadFromUrl(String DownloadUrl, String fileName) {

		 
		 
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
	 public void downloadPlaceBook(String packageKey, String downloadPath) {

		 String url = packageUrl + packageKey;
		 new DownloadFileAsync(downloadPath).execute(url);	
		
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
			                mProgressDialog.setMessage("Downloading PlaceBook..");
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
				  
				   public DownloadFileAsync(String dlPath){
					   packagePath = dlPath;
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
			                urlConnection.setDoOutput(true); 
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
			                reload();
			                
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
			  /* @Override
			   public boolean onKeyDown(int keyCode, KeyEvent event) {
			       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			           Log.d(this.getClass().getName(), "back button pressed");
			          this.finish();
			       }
			       return super.onKeyDown(keyCode, event);
			   }*/
			   
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
			   
			   	  
			   
			   

}	//End of public shelf
