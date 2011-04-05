package org.placebooks.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.net.URL;
import 	java.net.HttpURLConnection;
import 	java.io.File;
import 	android.os.Environment;
import android.util.Log;
import java.io.FileOutputStream;
import 	java.io.InputStream;
import java.net.MalformedURLException;
import java.io.IOException;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.ProgressDialog;
import android.content.Context;


//import android.widget.Toast;
//import java.util.zip.ZipInputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import java.io.FileInputStream;




public class PlaceBooks extends Activity {
	
	private static String placebooksfolder = new String("/PlaceBooks");
	private File file;
	private ProgressDialog progressDialog;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);	//push main layout into the content view
         	
        
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
        	           	        	String savedFilePath = Download("http://cs.swan.ac.uk/~csmarkd/package.zip");

        	           	           }
        	           	         });
       
        Button buttonR = (Button) findViewById(R.id.Button02);
        buttonR.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		Intent i = new Intent();
        		i.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
        		startActivity(i);		
        	}
        });
        
        
    }
    
    
    private void runDialog(final int seconds)
    
    {
    
            progressDialog = ProgressDialog.show(this, "Please wait....", "Here your message");
    
            new Thread(new Runnable(){
    
                public void run(){
    
                    try {
    
                                Thread.sleep(seconds * 1000);
    
                        progressDialog.dismiss();
    
                    } catch (InterruptedException e) {
    
                        e.printStackTrace();
    
                    }
    
                }
    
            }).start();
    }
    
    
    
    
    
    public String Download(String Url)
    {
		
     String filepath=null;
     try {
      //set the download URL, a url that points to a file on the internet
      //this is the file to be downloaded
      URL url = new URL(Url);
      //create the new connection
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      
      //set up some things on the connection
      urlConnection.setRequestMethod("GET");
      urlConnection.setDoOutput(true); 
       //and connect!
      urlConnection.connect();
      //set the path where we want to save the file
      //in this case, going to save it on the root directory of the
      //sd card.
      

      File SDCardRoot = Environment.getExternalStorageDirectory();
      //create a new file, specifying the path, and the filename
      //which we want to save the file as.
      
      
      String filename= "downloadFile.zip";   // you can download to any type of file ex:.jpeg (image) ,.txt(text file),.mp3 (audio file)
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

	 

   //   pbarDialog = ProgressDialog.show(mContext.this,
    //          "Please wait...", "Doing Extreme Calculations...", true);
      
      //now, read through the input buffer and write the contents to the file
      while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
    	  
       //add the data in the buffer to the file in the file output stream (the file on the sd card
       fileOutput.write(buffer, 0, bufferLength);
       //add up the size so we know how much is downloaded
       downloadedSize += bufferLength;
       //this is where you would do something to report the prgress, like this maybe
       Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;

      }
      
      //close the output stream when done
      fileOutput.close();
            
      if(downloadedSize==totalSize)   filepath=file.getPath();
      
     //catch some possible errors...
     } catch (MalformedURLException e) {
      e.printStackTrace();
     } catch (IOException e) {
      filepath=null;
      e.printStackTrace();
     }
     Log.i("filepath:"," "+filepath) ;

     return filepath;

    }
       
    
}