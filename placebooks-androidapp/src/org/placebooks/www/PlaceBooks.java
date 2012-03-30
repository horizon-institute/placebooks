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
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.content.SharedPreferences;



public class PlaceBooks extends Activity{
	
	private ProgressDialog myDialog = null;
    private EditText etUsername;
    private EditText etPassword;
    private String username; 
	private String password;  
    private Button btnLogin;
    private String strUserName;
    private String strPassword;
    private String root;
    private String authenticationUrl;
    private OnlineCheck oc;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //setContentView(R.layout.splash);
	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       

	
	        CustomApp appState = ((CustomApp)getApplicationContext());
	        root = appState.getRoot();
	        authenticationUrl =  appState.getAuthenticationUrl();
	        oc = new OnlineCheck();
	
	        
	        
	        
	        //Check if an SDCard exists. If it does then check if the PlaceBooks dir exists.
	        //If it does not exist then create it. If there is no SDCard then alert user they need one.
	        SDCardCheck sdcardcheck = new SDCardCheck();
	        if (sdcardcheck.isSdPresent()){
	        	//SDCard IS mounted. Now check if PlaceBooks root dir exists. Create it when app first starts up.
	    		File directory = new File(root);
	    		
		    		if(!directory.exists()){
		    			//Create the placebooks root directory on the SDCard
		    			directory = new File(root);
		    			directory.mkdirs();
		    		}
		    		else{
		    			//Directory exists so do nothing
		    		}
	        }
	         
	        else{ 
	        //SDCard is not mounted. Alert user.
	            Log.d("PlaceBooks", "No SDCARD");
	            
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("No SD Card!");
	        	builder.setMessage("There is no sd card mounted to this device! You need an sd card for this app!");
	        	builder.setPositiveButton("OK", null);
	        	builder.show();
	        }
	            
	        
<<<<<<< HEAD
	        
	        //Get the app's shared preferences - check if a user connected their account to the app
	        SharedPreferences loginAppPreferences = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
	        strUserName = loginAppPreferences.getString("username", "");
	        strPassword = loginAppPreferences.getString("password", "");
	        
	        
	        if (strUserName != ""){
=======
	        /*
	         * Sign in button pressed (action listener for button)
	         */
	        btnLogin.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v){
			        if (oc.isOnline(PlaceBooks.this)){
   
	        				// Check Login	
	        		         username = etUsername.getText().toString();
	        		         password = etPassword.getText().toString();
	        		      
	        		         
	        		        if(username.length() > 0 && password.length() >0){
	        	        		
	        		        	//progress dialog for logging in (gives user feedback)
	        		        	myDialog = ProgressDialog.show( PlaceBooks.this, " " , " Logging in.. ", true);				   	 
	        		        	
	        		        	new Thread() {
	        		        	public void run() {
	        		        	try{
		        		           final HttpClient httpClient = new DefaultHttpClient();
		                   		   final HttpPost httpPost = new HttpPost("http://www.placebooks.org/placebooks/j_spring_security_check");
		
		                   		   
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
>>>>>>> upstream/master
	
			     Intent intent = new Intent();
			     intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
			     intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
			     //intent.putExtra("password", password);
			     startActivity(intent);	
			     endThisActivity();	//kill the placebooks activity
	
	        }
	        
	        else{	
	         //First time using the app on the phone so let user log in..
	        
		        //Load up the layout
		        setContentView(R.layout.main);	//push main layout into the content view
		        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       

		        //Display the placebooks logo
		        ImageView image = (ImageView) findViewById(R.id.imgLogo);
		        image.setImageResource(R.drawable.placebooks_logo);
		        	      
		        //Set the 'login' button and 'username' and 'password' edit text fields    
		        btnLogin = (Button) findViewById(R.id.btnLogin);
		        etUsername = (EditText) findViewById(R.id.txt_username);
		        etPassword = (EditText) findViewById(R.id.txt_password);
		       
		        
		        //Sign in button pressed (action listener for button)
		        btnLogin.setOnClickListener(new OnClickListener() {
		        	public void onClick(View v){
				        if (oc.isOnline(PlaceBooks.this)){
	   
		        			// Check Login	
		        		    username = etUsername.getText().toString();
		        		    password = etPassword.getText().toString();
		        		         
		        		    if(username.length() > 0 && password.length() >0){
		        	        		
		        		      	//progress dialog for logging in (gives user feedback)
		        		       	myDialog = ProgressDialog.show( PlaceBooks.this, " " , " Logging in.. ", true);				   	 
		        		        	
		        		        	new Thread() {
		        		        	public void run() {
			        		        	try{
				        		           final HttpClient httpClient = new DefaultHttpClient();
				                   		   final HttpPost httpPost = new HttpPost(authenticationUrl);
				                   		   
				                   		   try
				                   		   {
					                   		    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					                   		    parameters.add(new BasicNameValuePair("j_username", username));
					                   		    parameters.add(new BasicNameValuePair("j_password", password));
					                   		    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
					
					                   		    final HttpResponse response = httpClient.execute(httpPost);
					
					                   		    Log.i("PlaceBooks", "Status Code: " + response.getStatusLine().getStatusCode());
					                   		                                     		    
					                   		    for (final Header header : response.getAllHeaders())
					                   		    {
					                   		    	Log.i("PlaceBooks", header.getName() + "=" + header.getValue());
					                   		    }
					
					                   		    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					                   		    response.getEntity().writeTo(bos);
					                   		    Log.i("PlaceBooks", "Content: " + bos.toString());
				        		        	
						                   		 if(response.getStatusLine().getStatusCode() == 200){ 
						                   		 	//Login successful 
						                   			//Write credentials to shared preferences (only need the username since password has been confirmed)
						                   			saveCredentials(username, password);
						                   			
						                   			//Take user to their bookshelf
					             		        	Intent intent = new Intent();
					             	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
					             	        		intent.putExtra("username", username);  //Pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
					             	        		//intent.putExtra("password", password);
					             	        		startActivity(intent);	
					             	        		endThisActivity(); //End placebooks activity
						                   		 }	
						                   		 else{
						                            myDialog.dismiss();
						                         	invalidCredentials();
						                   		 }
			    
			                                 }
			                                 catch (final Exception e)
			                                 {
			                                   Log.e("PlaceBooks", e.getMessage(), e);
			
			                                 }
			                               
			        		        	}     
		                           catch (Exception e) { }
		                           // Dismiss the Dialog
		                           	myDialog.dismiss();     
		        		        	}
		        		            }.start();       	
		
		        	        		
		        		    } //End of if username&password
		        		    
		                    else{
		                   		//Login failed - displays a naff message telling them a username must be entered
		        		        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
		        		        builder.setTitle("Username & Password need to be entered!");
		        		        builder.setMessage("Please enter a username and a password!");
		        		        builder.setPositiveButton("OK", null);
		        		        AlertDialog dialog = builder.show();
		        		    }     
		        		    
		        	  }//end of if oc
				        
				        
				      else if(!oc.isOnline(PlaceBooks.this)){
				    	//No internet connection!
	  		        	AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
	  		        	builder.setTitle("No Internet Connectivity!");
	  		        	builder.setMessage("You mobile needs to be online to log in");
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
	    	    
		  //Get the app's shared preferences
		  SharedPreferences login_app_preferences =  this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
		  // Update fields
		  SharedPreferences.Editor editor = login_app_preferences.edit();
		  editor.putString("username", uname);
		  editor.putString("password", pass);
		  editor.commit();  //Very important
	    	
	  }
	    
	  public void endThisActivity(){
		  this.finish();  //Kill the placebooks activity
	  }
	   
           
}