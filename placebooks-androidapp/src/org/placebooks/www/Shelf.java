package org.placebooks.www;

//import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
//import java.util.zip.ZipException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListAdapter;
import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
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
//import android.view.LayoutInflater;

//import java.net.URL;
//import android.os.AsyncTask;
//import android.content.res.Configuration;
import android.app.AlertDialog;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.DataOutputStream;
//import android.webkit.CookieManager;

//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.client.CookieStore;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.client.protocol.ClientContext;
//import org.apache.http.client.*;

//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.Header;
//import java.io.ByteArrayOutputStream;
import android.view.KeyEvent;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class Shelf extends ListActivity {
	
	/* This variables need to be global, so we can used them onResume and onPause method to
    stop the listener */
	TelephonyManager Tel;
	MyPhoneStateListener MyListener;
	String cinr;
	
	private ProgressDialog myDialog = null;

	
	private JSONObject json;
    private String username;
    private String password;
    private ListView lv;
	
	//-- Download variables --
	private static String placebooksfolder = new String("/PlaceBooks");
	private File file;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String filename= "downloadFile.zip";   // you can download to any type of file ex:.jpeg (image) ,.txt(text file),.mp3 (audio file)
    //-- Download Variables --

	    
    public void setCinr(String c){
    	this.cinr = c;
    }
    
    
	 @Override
		public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);	//icicle
		        setContentView(R.layout.shelflist);	//push shelf list layout into the content view
		        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen

		        
		        
		        /* 
		         * Update the listener, and start it 
				 * This is for testing the phone signal strength
		         */
		        MyListener = new MyPhoneStateListener();
		        Tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
		        Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		        
		        
		        		        
		        
		        
		        /*
		         * get the extras (username) out of the new intent
		         * retrieve the username.
		         */
		        Intent intent = getIntent();
		        if(intent != null) username = intent.getStringExtra("username");
		        //if(intent != null) password = intent.getStringExtra("password");
		        
		        System.out.println("username =====" + username);
		        


		        

		        OnlineCheck oc = new OnlineCheck();		       
		        /*
		         * If the user name and password are correct then it will get the json file from online and display the placebooks. The user can then download their shelf or a single placebook at a time. If the user has no Internet
		         * then the code will attempt to read the json file from the sdcard. If the user has no placebooks on the sdcard then a message will be displayed saying that there have been no placebooks downloaded.
		        */       
		        if (oc.isOnline(this)){
		        	
		        	//Toast msg = Toast.makeText(this, "cinr= " + cinr, Toast.LENGTH_LONG);
					//msg.show();
		        	
		        	json = JSONfunctions.getJSONfromSDCard("sdcard/placebooks/unzipped/" + username+ "_shelf" + ".json");			///sdcard/placebooks/unzipped/" + "packages/shelfstuart.json
		        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
			        TextView tv = new TextView(this);
			        tv.setText("Reading the cached shelf But I DO have an Internet connection.");
			        ll.addView(tv);
		        	
		/*   TAKEN OUT FOR TIME BEING TO TEST GPS LOCATION
		 *      	
					String url =  "http://www.placebooks.org/placebooks/placebooks/a/admin/shelf/"+ username;
				    System.out.println("URL ===== " + url);
					json = JSONfunctions.getJSONfromURL(url);		//email address that the user enters (stuart@tropic.org.uk) (ktg@cs.nott.ac.uk/)
				          										  
				    //also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
				    DownloadFromUrl(url, username+ "_shelf" + ".json"); 	
				          	
				    LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
					TextView tv = new TextView(Shelf.this);
					tv.setText("Reading the shelf from the Internet. Also updating the cached shelf.");	
					ll.addView(tv);
		*/			
					
		        	/*
		        	 * Still working on
		        	 * new GetShelfTask().execute();
		        	 */
		
		        }
		        else if (!oc.isOnline(this)) {		//do a check if there is a shelf file on the sdcard
		        	//if the json file is empty or does not exist then the listview will display an error message otherwise it will display the contents in the json shelf file
			        json = JSONfunctions.getJSONfromSDCard("sdcard/placebooks/unzipped/" + username+ "_shelf" + ".json");			///sdcard/placebooks/unzipped/" + "packages/shelfstuart.json
		        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
			        TextView tv = new TextView(this);
			        tv.setText("Reading the cached shelf because cannot connect to Internet at this time. If the shelf is blank then your memory card does not have your shelf file. Please try again with Internet access.");
			        ll.addView(tv);
			        
			        
		        }
		        else {
		        	// either no internet, no placebook shelf stored on the sd card, no books can be accessed
		        	LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
		        	TextView tv = new TextView(this);
		        	tv.setText("No data: You have not downloaded any placebooks and do not appear to be online. You need to enable the Internet to access your placebook shelf and then you can download your placebook/s. Another reason why you are not seeing your shelf might be due to your Internet security settings");
		        	ll.addView(tv);
		        }
		       
		        List<MyListItemModel> myListModel = new ArrayList<MyListItemModel>();

		        try{
		        	
		        	JSONArray entries = json.getJSONArray("entries");
		        	//JSONObject jObject = json.getJSONObject("user");
		        	
			        for(int i=0;i<entries.length();i++){						
					
			        	final MyListItemModel item = new MyListItemModel(this);
			        	JSONObject e = entries.getJSONObject(i);
			        	
			        	item.setID(i);		//Owner ID
			        	item.setKey(e.getString("key"));	//book key key
			        	item.setTitle(e.getString("title"));	//book title
			        	item.setDescription(e.getString("description"));	//book description
			        	item.setPackagePath(e.getString("packagePath"));
			        	
			        	//taken out for now
			        	//item.setOwner(u.getString("name"));  //book owner name e.g stuart
			       		
			        	 item.dl_listener = new OnClickListener(){
				        	public void  onClick  (View  v){
				        		
				        		SDCardCheck sdcardcheck = new SDCardCheck();
				        		//if the sdcard is mounted then download
				        		if (sdcardcheck.isSdPresent()){
				        		
				        			
				        			 //placebook does not exist on sdcard so download it.
				        			 // call the download method and pass it the book key and package path
				        			 
				        			downloadPlaceBook(item.getKey(), item.getPackagePath() );
				        		}
				        		else{
				        			//no sdcard
				        			Log.d("MyApp", "No SDCARD");
				        		       
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

			        	}catch(JSONException e) {
			        		Log.e("log_tag", "Error parsing data "+e.toString());
			        	}
			        	
			        	
			        	
			        	
			        	//populates the list view using the adapter
			        	MyListAdapter adapter = new MyListAdapter(this);	//create the Adapter
			        	adapter.setModel(myListModel);		//pass the ArrayList into the Adapter
			        	setListAdapter(adapter);			//assign Adapter
			        	lv = getListView();				//call the ListView
			        	lv.setTextFilterEnabled(true);  //enables filtering for the contents of the ListView.
			        	
			        				        	
		    
	 } //end of onCreate
	 
	 /*
	  * Method for downloading the shelf JSON file to the SDCard for caching.
	  * Used for the initial download and also caching.
	  */
	 public void DownloadFromUrl(String DownloadUrl, String fileName) {

		   try {
		           //File root = android.os.Environment.getExternalStorageDirectory();   

		           //File dir = new File (root.getAbsolutePath() + "/xmls");
		           File SDCardRoot = Environment.getExternalStorageDirectory();
		           File dir = new File(SDCardRoot + "/placebooks/unzipped/"); //SDCardRoot/PlaceBooks/unzipped/packages
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

	 /*
	  * Download the zip file package
	  */
	 public void downloadPlaceBook(String theKey, String downloadPath) {
		 String dlPath = downloadPath;
	     //String url = "http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/package/" + theKey;
		 String url = "http://www.placebooks.org/placebooks/placebooks/a/admin/package/" + theKey;
		 new DownloadFileAsync(dlPath).execute(url);	
		
	    }
	 		
			 @Override
			    public void onConfigurationChanged (Configuration newConfig){
			    	super.onConfigurationChanged(newConfig);
			    }

			    
			    @Override
			    protected Dialog onCreateDialog(int id) {
			        switch (id) {
			            case DIALOG_DOWNLOAD_PROGRESS:
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
			            showDialog(DIALOG_DOWNLOAD_PROGRESS);
			        }

			        @Override
			        protected String doInBackground(String... aurl) {
			      //      int count;
			            String filepath=null;


			            try {
			                URL url = new URL(aurl[0]);
			          //      URLConnection urlConnection  = url.openConnection();
			                //create the new connection
			                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			                //set up some things on the connection
			                urlConnection.setRequestMethod("GET");
			                urlConnection.setDoOutput(true); 
			                 //and connect!
			                urlConnection.connect();

			                int lenghtOfFile = urlConnection .getContentLength();
			                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

			       
			                File SDCardRoot = Environment.getExternalStorageDirectory();
			                //create a new file, specifying the path, and the filename
			                //which we want to save the file as.
			    
			                /**
			                 * This needs to be changed to whatever the filename you are downloading is called. 
			                 */
			                //String filename= "downloadFile.zip";   // you can download to any type of file ex:.jpeg (image) ,.txt(text file),.mp3 (audio file)
			                Log.i("Local filename:",""+filename);
			                file = new File(SDCardRoot + placebooksfolder,filename); //SDCardRoot/PlaceBooks

			                
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
			                String zipFileLocation = (SDCardRoot +placebooksfolder + "/" +filename);
			                //String unzipPath = (SDCardRoot +placebooksfolder + "/unzipped" + packagePath) ;
			                String unzipPath = (SDCardRoot +placebooksfolder + "/unzipped/");// + packagePath);	//NEW VERSION it doesn't need the package path - it just gets it from the zipped package structure. Old stable version needs packagePath
			                // pass these values to the unzipper method in the decompress class
			           //     Decompress unzipper=new Decompress(zipFileLocation, unzipPath);			                
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
				      			                
			                
			                if(downloadedSize==totalSize)   filepath=file.getPath();
			                reload();
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
			            
			        }
			    }
			   
			   
			   /*
			    * The Carrier to Interference-plus-Noise Ratio (CINR) is a primary measurement of signal effectiveness.
			    * The carrier is the desired signal, and the interference can either be noise or co-channel interference.
			    * (Co-channel interference is a particular problem when frequencies are reused at short distances).
			    */
			   
			   /* Called when the application is minimized */
			    @Override
			   protected void onPause()
			    {
			      super.onPause();
			      Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
			   }

			    /* Called when the application resumes */
			   @Override
			   protected void onResume()
			   {
			      super.onResume();
			      Tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
			   }

			   /* ÑÑÑÑÑÑÑÑÑÐ */
			    /* Start the PhoneState listener */
			   /* ÑÑÑÑÑÑÑÑÑÐ */
			    private class MyPhoneStateListener extends PhoneStateListener
			    {
			      /* Get the Signal strength from the provider, each time there is an update */
			      @Override
			      public void onSignalStrengthsChanged(SignalStrength signalStrength)
			      {
			         super.onSignalStrengthsChanged(signalStrength);
			     /*    Toast msg = Toast.makeText(getApplicationContext(), "GSM Cinr = "
			            + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT);
			            msg.show();
			       */     
			      }

			    };// End of private Class
			    
			   
			   
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
			          this.finish();
			       }
			       return super.onKeyDown(keyCode, event);
			   }
			   
			   
			   
	/*		   
			   private class GetShelfTask extends AsyncTask<String, Void, Boolean> {

				   @Override
				       protected void onPreExecute() {
				           super.onPreExecute();
				           // show a progress dialog indicating that its loading
      		        		myDialog = ProgressDialog.show( Shelf.this, " " , " Logging in.. ", true);	

				       }

				       @Override
				       protected Boolean doInBackground(String... params) {
				               // give your code that has to be loaded.
       		        	//	new Thread() {
	        		    //    	public void run() {
	        		        		
				    	   
				    	    String url =  "http://www.placebooks.org/placebooks/placebooks/a/admin/shelf/"+ username;
						    System.out.println("URL ===== " + url);
							json = JSONfunctions.getJSONfromURL(url);		//email address that the user enters (stuart@tropic.org.uk) (ktg@cs.nott.ac.uk/)
						          										  
						    //also need to update the shelf.xml file on the sd card with the latest version when you have an Internet connection
						    DownloadFromUrl(url, username+ "_shelf" + ".json"); 	
						          	
						    LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout);
							TextView tv = new TextView(Shelf.this);
							tv.setText("Reading the shelf from the Internet. Also updating the cached shelf.");	
							ll.addView(tv);	
	        		        		
	        		        		
  
	 	        		//        	}
	 	        		//            }.start();    
	        		        	
       		        	
				    	   return true;
				       }

				       @Override
				       protected void onPostExecute(Boolean success) {
				           super.onPostExecute(success);
				           //dismiss your progress dialog
                           	myDialog.dismiss();     

				       }
				   }
			   
	*/		   

}	//end of public shelf
