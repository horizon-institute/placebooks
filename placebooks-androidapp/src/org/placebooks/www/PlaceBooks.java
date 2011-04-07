package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import 	java.net.HttpURLConnection;
import 	java.io.File;
import 	android.os.Environment;
import android.util.Log;
import java.net.MalformedURLException;
import java.io.IOException;



import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.widget.ProgressBar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.os.AsyncTask;
import android.content.res.Configuration;


public class PlaceBooks extends Activity {
	
	private static String placebooksfolder = new String("/PlaceBooks");
	private File file;
//	private TextView statusTextView;
//	private String filepath;
//  private ProgressDialog progressDialog;
//    private Context myContext = PlaceBooks.this;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private String filename= "downloadFile.zip";   // you can download to any type of file ex:.jpeg (image) ,.txt(text file),.mp3 (audio file)
 
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);	//push main layout into the content view
       // statusTextView = (TextView)findViewById(R.id.status);
      
         	    
        //create /placebooks dir on app startup
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("MyApp", "No SDCARD");
        } else {
        File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"PlaceBooks");
        directory.mkdirs();
        }

       
        Button button = (Button) findViewById(R.id.Button01);
        button.setOnClickListener(new OnClickListener() {
        	           	           public void onClick(View v) {
   	           	        	   
        	           	        	 //call the download method passing it the url to the placebooks package
        	           	        	//String savedFilePath = Download("http://cs.swan.ac.uk/~csmarkd/package.zip");
        	                        startDownload();
    	   
        	           	           }
        	           	         });
        
       // setListener();  //call the setListner method

        Button buttonR = (Button) findViewById(R.id.Button02);
        buttonR.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		Intent i = new Intent();
        		i.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
        		startActivity(i);		
        	}
        });
        
        
    }
    
    @Override
    public void onConfigurationChanged (Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    }

    
    private void startDownload() {
    	/**
    	 * This needs to be changed to placebooks server depending on the user account that is being accessed.
    	 */
        String url = "http://cs.swan.ac.uk/~csmarkd/package.zip";
        new DownloadFileAsync().execute(url);
        
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading & Unpacking PlaceBook..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
    
   class DownloadFileAsync extends AsyncTask<String, String, String> {
        
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
                //URLConnection urlConnection  = url.openConnection();
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
                
                // location of the downloaded .zip file on the sd card
                String fileLoc = (Environment.getExternalStorageDirectory() +placebooksfolder + "/" +filename);
                File sdCard = Environment.getExternalStorageDirectory();
                String unzipLocation = sdCard.toString()+"/PlaceBooks";//(Environment.getExternalStorageDirectory() +placebooksfolder); 
                
                Decompress d = new Decompress(fileLoc, unzipLocation); 
                d.unzip();
                
    
                if(downloadedSize==totalSize)   filepath=file.getPath();
                
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
   
           
}