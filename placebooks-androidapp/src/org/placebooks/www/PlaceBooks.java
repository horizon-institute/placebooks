package org.placebooks.www;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Environment;
import android.util.Log;

import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


//Log In needs to be a one time thing - store the credential vars in the shared preferences..

public class PlaceBooks extends Activity{
	
	private ProgressDialog myDialog = null;
    private EditText etUsername;
    private EditText etPassword;
    private String username; 
	private String password;  
    private Button btnLogin;
    private TextView tv;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        
        /*check if an SDCard exists. If it does then check if the PlaceBooks dir exists.
         * If it does not exist then create it. If there is no SDCard then alert user they need one.
         */
        SDCardCheck sdcardcheck = new SDCardCheck();

        if (sdcardcheck.isSdPresent()){
        	//SDCard IS mounted. Now check if PlaceBooks dir exists. Create it on first app startup.
    		File directory = new File(Environment.getExternalStorageDirectory()+ "/PlaceBooks");///");
    		
	    		if(!directory.exists()){
	    			//create the placebooks directory on the SDCard
	    			directory = new File(Environment.getExternalStorageDirectory()+"/PlaceBooks");//File.separator+"PlaceBooks");
	    			directory.mkdirs();
	    		}
	    		else{
	    			//directory exists so do nothing
	    		}
        }
         //SDCard is not mounted. Alert user.
        else{ 
        	
            Log.d("MyApp", "No SDCARD");
       
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("No SD Card!");
        	builder.setMessage("There is no sd card mounted to this device! You need an sd card for this app!");
        	builder.setPositiveButton("OK", null);
        	builder.show();
        }
        
        
        /*
         *  Get the app's shared preferences - check if a user connected their account to the app
         */
        SharedPreferences login_app_preferences = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
        String strUserName = login_app_preferences.getString("username", "");
        String strPassword = login_app_preferences.getString("password", "");
        if (strUserName != ""){
        	Intent intent = new Intent();
     		intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
     		intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
     		//intent.putExtra("password", password);
     		startActivity(intent);	
     		endThisActivity();	//kill the placebooks activity
        }
        else{	//first time using the app on the phone so let user log in..
        
	        // load up the layout
	        setContentView(R.layout.main);	//push main layout into the content view
	        
	        //placebooks logo
	        ImageView image = (ImageView) findViewById(R.id.imgLogo);
	        image.setImageResource(R.drawable.placebooks_logo);
	        
	        
	      
	
	        //set the 'login' button and 'username and password' textfields    
	        btnLogin = (Button) findViewById(R.id.btnLogin);
	        etUsername = (EditText) findViewById(R.id.txt_username);
	        etPassword = (EditText) findViewById(R.id.txt_password);
	       // LinearLayout ll = (LinearLayout)findViewById(R.id.linearlayout);
	       
	        
	        /*
	         * Sign in button pressed (action listener for button)
	         */
	        btnLogin.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v){
					   
	        				// Check Login	
	        		         username = etUsername.getText().toString();
	        		         password = etPassword.getText().toString();
	        		      
	        		         
	        		        if(username.length() > 0 && password.length() >0){
	        	        		
	        		        	//progress dialog for user feedback
	        		        	myDialog = ProgressDialog.show( PlaceBooks.this, " " , " Logging in.. ", true);				   	 
	        		        	
	        		        	new Thread() {
	        		        	public void run() {
	        		        	try{
		        		           final HttpClient httpClient = new DefaultHttpClient();
		                   		   final HttpPost httpPost = new HttpPost("http://horizac1.miniserver.com/placebooks/j_spring_security_check");
		
		                   		   
		                   		   try
		                   		   {
		                   		    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		                   		    parameters.add(new BasicNameValuePair("j_username", username));
		                   		    parameters.add(new BasicNameValuePair("j_password", password));
		                   		    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		
		                   		    final HttpResponse response = httpClient.execute(httpPost);
		
		                   		    Log.i("placebooks", "Status Code: " + response.getStatusLine().getStatusCode());
		                   		                                     		    
		                   		    for (final Header header : response.getAllHeaders())
		                   		    {
		                   		     Log.i("placebooks", header.getName() + "=" + header.getValue());
		                   		    }
		
		                   		    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		                   		    response.getEntity().writeTo(bos);
		                   		    Log.i("placebooks", "Content: " + bos.toString());
		                   		    
		        		        	
			                   		 if(response.getStatusLine().getStatusCode() == 200){ 
			                   		 	//login successful 
			                   			//write credentials to shared preferences (only need the username since password has been confirmed)
			                   			saveCredentials(username, password);
			                   			
			                   			//take user to their bookshelf
		             		        	Intent intent = new Intent();
		             	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
		             	        		intent.putExtra("username", username);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
		             	        		//intent.putExtra("password", password);
		             	        		startActivity(intent);	
		             	        		endThisActivity(); //end placebooks activity
			                   		 }	
			                   		 else{
			                            myDialog.dismiss();
			                         	invalidCredentials();
			                        	 
			                   		 }
			                   		 
	    
	                                 }
	                                 catch (final Exception e)
	                                 {
	                                   Log.e("placebooks", e.getMessage(), e);
	
	                                 }
	                               
	        		        	}     
	                           catch (Exception e) { }
	                           // Dismiss the Dialog
	                           	myDialog.dismiss();     
	        		        	}
	        		            }.start();       	
	
	        	        		
	        		        } //end of if username&password
	                   		   else{
	        		        	
	        		        	//login failed - displays a naff message telling them a username must be entered
	        		        	AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
	        		        	builder.setTitle("Username & Password need to be entered!");
	        		        	builder.setMessage("Please enter a username and a password!");
	        		        	builder.setPositiveButton("OK", null);
	        		        	AlertDialog dialog = builder.show();
	        		        }     		       	
	        		
	        	}
	        });
        
        } //end of else 
      }   //end of onCreate()
    
    
    public void invalidCredentials(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
     	builder.setTitle("Invalid Credentials!");
     	builder.setMessage("Please check that the username and password are correct");
     	builder.setPositiveButton("OK", null);
     	AlertDialog dialog = builder.show();
    }
    
    
    public void saveCredentials(String uname, String pass){
    	
    	    // Get the app's shared preferences
			SharedPreferences login_app_preferences =  this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
			// Update fields
			SharedPreferences.Editor editor = login_app_preferences.edit();
			editor.putString("username", uname);
			editor.putString("password", pass);
			editor.commit(); // Very important
    	
    }
    
    public void endThisActivity(){
    	this.finish();	//kill the placebooks activity
    }
	   
           
}