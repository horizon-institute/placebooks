package org.placebooks.www;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import java.net.URL;
import java.io.FileOutputStream;
import java.io.InputStream;
//import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.io.File;
import android.os.Environment;
import android.util.Log;
import java.net.MalformedURLException;
import java.io.IOException;

import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;



public class PlaceBooks extends Activity {
	
   
    private EditText etUsername;
   // private EditText etPassword;
    private String username; 
	//private String password;  
    private Button btnLogin;

		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load up the layout
        setContentView(R.layout.main);	//push main layout into the content view
        
        /*check if an SDCard exists. If it does then check if the PlaceBooks dir exists.
         * If it does not exist then create it. If there is no SDCard then alert user they need one.
         */
        
        if (isSdPresent()){
        	//SDCard IS mounted. Now check if PlaceBooks dir exists. Create it on first app startup.
    		File directory = new File(Environment.getExternalStorageDirectory()+ "/PlaceBooks/");
    		
	    		if(!directory.exists()){
	    			//create the placebooks directory on the SDCard
	    			directory = new File(Environment.getExternalStorageDirectory()+File.separator+"PlaceBooks");
	    			directory.mkdirs();
	    		}
	    		else{
	    			//directory exists so do nothing
	    		}
        
        }
        
        else{  //SDCard is not mounted. Alert user.
        	
            Log.d("MyApp", "No SDCARD");
       
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("No SD Card!");
        	builder.setMessage("There is no sd card mounted to this device! You need an sd card for this app!");
        	builder.setPositiveButton("OK", null);
        	builder.show();
        }
        
      

        //set the 'login' button and 'username and password' textfields    
        btnLogin = (Button) findViewById(R.id.btnLogin);
        etUsername = (EditText) findViewById(R.id.txt_username);
        //etPassword = (EditText) findViewById(R.id.txt_password);
       // LinearLayout ll = (LinearLayout)findViewById(R.id.linearlayout);
       

        
        /*
         * Sign in button pressed (action listener for button)
         */
        btnLogin.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		
        				// Check Login	
        		         username = etUsername.getText().toString();
        		         //password = etPassword.getText().toString();
        		      
        		         
        		        if(username.length() > 0){
        		
        		        	//login successful - take user to their bookshelf
        		        	Intent intent = new Intent();
        	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
        	        		intent.putExtra("username", username);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
        	        		startActivity(intent);		
        		
        		        } else{
        		        	
        		        	//login failed - displays a naff message telling them a username must be entered
        		        	AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
        		        	builder.setTitle("Username not entered!");
        		        	builder.setMessage("Please enter a username!");
        		        	builder.setPositiveButton("OK", null);
        		        	AlertDialog dialog = builder.show();
        		        }     		       	
        		
        	}
        });
      }
           
   
	   /*
	    * A method that checks if an SDCard is present on the mobile device
	    */  
	   public static boolean isSdPresent() {
		   
		   return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		   
	   }
 
           
}