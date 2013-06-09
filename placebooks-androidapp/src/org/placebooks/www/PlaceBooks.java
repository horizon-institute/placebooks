package org.placebooks.www;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.app.Dialog;
import android.os.Handler;
//import android.content.res.Resources;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.HttpVersion;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;
import android.view.WindowManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.cookie.Cookie;
import android.content.res.Configuration; 
import java.util.Locale;



public class PlaceBooks extends Activity{
	
	private ProgressDialog loginDialog = null;
    private EditText etUsername;
    private EditText etPassword;
    private String username; 
	private String password;  
    private Button btnLogin;
    private String strUserName;
    private String strPassword;
    private String unzippedDir;
    private String authenticationUrl;
    private OnlineCheck oc;
    private String unzippedRoot;
    
    private String languageSelected;

    
    protected Dialog mSplashDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //setContentView(R.layout.splash);
	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	        
	        //MyStateSaver data = (MyStateSaver) getLastNonConfigurationInstance();
	        
	        
	        CustomApp appState = ((CustomApp)getApplicationContext());
	        unzippedDir = appState.getUnzippedDir();
	        authenticationUrl =  appState.getAuthenticationUrl();
	        unzippedRoot = appState.getUnzippedRoot();
	        
	        
	        
	        languageSelected  = appState.getLanguage();  
	        Locale locale = new Locale(languageSelected);   
	        Locale.setDefault(locale);  
	        Configuration config = new Configuration();  
	        config.locale = locale;  
	        getBaseContext().getResources().updateConfiguration(config,   
	        getBaseContext().getResources().getDisplayMetrics());  
	   
	        
	        oc = new OnlineCheck();	 //check online connection
	        
	        
	        //Check if an SDCard exists. If it does then check if the PlaceBooks dir exists.
	        //If it does not exist then create it. If there is no SDCard then alert user they need one.
	        SDCardCheck sdcardcheck = new SDCardCheck();
	        if (sdcardcheck.isSdPresent()){
	        	//SDCard IS mounted. Now check if PlaceBooks root dir exists. Create it when app first starts up.
	    		File directory = new File(unzippedDir);
	    		
		    		if(!directory.exists()){
		    			//Create the placebooks root directory on the SDCard
		    			directory = new File(unzippedDir);
		    			directory.mkdirs();
		    		}
		    		else{
		    			//Directory exists so do nothing
		    		}
	        }
	         
	        else{ 
	        //SDCard is not mounted. Alert user.
	            Log.d("Placebooks", getResources().getString(R.string.no_sdcard));
	            
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle(getResources().getString(R.string.no_sdcard));
	        	builder.setMessage(getResources().getString(R.string.sdcard_needed));
	        	builder.setPositiveButton(getResources().getString(R.string.ok), null);
	        	builder.show();
	        }
	            
	        
/*	        
	        //Get the app's shared preferences - check if a user connected their account to the app
	        SharedPreferences loginAppPreferences = this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
	        strUserName = loginAppPreferences.getString("username", "");
	        strPassword = loginAppPreferences.getString("password", "");
	        
	        if (data != null) {
		            // Show splash screen if still loading
		            if (data.showSplashScreen) {
		                showSplashScreen();
		            }
		            if (strUserName != ""){
		            	
					     Intent intent = new Intent();
					     //intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
					     intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
					     intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
					     //intent.putExtra("password", password);
					     startActivity(intent);	
					     mSplashDialog.dismiss();
					     endThisActivity();	//kill the placebooks activity
			
			        }
			        
			        else{	
			         //First time using the app on the phone so let user log in..
			        
			        presentLogin();
			        
			        } //end of else         
		     
		            // Rebuild your UI with your saved state here
	        } else {
	            showSplashScreen();
		            if (strUserName != ""){
		            	
					     Intent intent = new Intent();
					     //intent.setClassName("org.placebooks.www", "org.placebooks.www.Shelf");
					     intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
					     intent.putExtra("username", strUserName);  //pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity in the Shelf class
					     //intent.putExtra("password", password);
					     startActivity(intent);	
					     mSplashDialog.dismiss();
					     endThisActivity();	//kill the placebooks activity
			
			        }
			        
			        else{	
			         //First time using the app on the phone so let user log in..
			        
			        presentLogin();
			        
			        } //end of else 
		            // Do your heavy loading here on a background thread
	        }
	        */
	        
	        //User isn't logged in so let them log in
	        presentLogin();

	        
	        
      }   //end of onCreate()
    
    
    
    public void presentLogin(){
        //Load up the layout
        setContentView(R.layout.main);	//push main layout into the content view
        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       

        //Display the placebooks logo
        ImageView image = (ImageView) findViewById(R.id.imgLogo);
        //image.setImageResource(R.drawable.placebooks_logo);
        
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
        	        		
        		      new AttemptLogin(username, password).execute();	

        	        		
        		    } //End of if username&password
        		    
                    else{
                   		//Login failed - displays a naff message telling them a username must be entered
        		        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
        		        builder.setTitle(getResources().getString(R.string.usr_pass_needed));
        		        builder.setMessage(getResources().getString(R.string.please_use_pass));
        		        builder.setPositiveButton(getResources().getString(R.string.ok), null);
        		        AlertDialog dialog = builder.show();
        		    }     
        		    
        	  }//end of if oc
		        
		        
		      else if(!oc.isOnline(PlaceBooks.this)){
		    	//No internet connection!
		        	AlertDialog.Builder builder = new AlertDialog.Builder(PlaceBooks.this);
		        	builder.setTitle(getResources().getString(R.string.no_connectivity));
		        	builder.setMessage(getResources().getString(R.string.need_signed_in));
		        	builder.setPositiveButton("OK", null);
		        	AlertDialog dialog = builder.show();
		      }
        	}
        });
    }
    
    public void createDownloadsFile(String uname){
    	
    	//create a downloads.json file - this will keep track of all the downloaded books

    	//JSONObject jsonObject = new JSONObject();
	    //JSONArray jsonArray = new JSONArray();
	    
    	try{
   			Writer output = null;

   			//String text = "JSON code for "how to" book";
   			File file = new File(unzippedRoot + uname + "_downlaods.json");
   			output = new BufferedWriter(new FileWriter(file));

   			//jsonArray.put("");
   			//jsonObject.put("downloads", new JSONArray());
   			//String out = jsonObject.toString();
   			//output.write(out);
   			output.close();

   		 	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    	/*catch(JSONException je){
    		je.printStackTrace();
    	}*/
   			
    	
    }

	    
	  public void saveCredentials(String uname, String pass, String cookieName, String cookieValue, int cookieVersion, String cookieDomain, String cookiePath, String cookieExpiry){
	    	    
		  //Get the app's shared preferences
		  SharedPreferences login_app_preferences =  this.getSharedPreferences("LOGIN_DETAILS", MODE_PRIVATE);
		  // Update fields
		  SharedPreferences.Editor editor = login_app_preferences.edit();
		  editor.putString("username", uname);
		  editor.putString("password", pass);
		  editor.putString("cookieName", cookieName);
		  editor.putString("cookieValue", cookieValue);
		  editor.putInt("cookieVersion", cookieVersion);
		  editor.putString("cookieDomain", cookieDomain);
		  editor.putString("cookiePath", cookiePath);
		  editor.putString("cookieExpiry", cookieExpiry);
		  
		  System.out.println("cookieName== " + cookieName);
		  System.out.println("cookieValue== " + cookieValue);
		  System.out.println("cookieDomain== " + cookieDomain);
		  System.out.println("cookiePath== " + cookiePath);

		  editor.commit();  //Very important
	    	
	  }
	    
	  public void endThisActivity(){
		  this.finish();  //Kill the placebooks activity
		 //SplashDialog.dismiss();
	  }
	  
	  /**
	   * Simple class for storing important data across config changes
	   */
/*
  	  private class MyStateSaver {
 
	      public boolean showSplashScreen = false;
	      // Your other important fields here
	  }
	  
	  @Override
	  public Object onRetainNonConfigurationInstance() {
	      MyStateSaver data = new MyStateSaver();
	      // Save your important data here
	   
	      if (mSplashDialog != null) {
	          data.showSplashScreen = true;
	          removeSplashScreen();
	      }
	      return data;
	  }
	   
	 // Removes the Dialog that displays the splash screen  
	  protected void removeSplashScreen() {
	      if (mSplashDialog != null) {
	          mSplashDialog.dismiss();
	          mSplashDialog = null;
	      }
	  }
	   
	  
	   //Shows the splash screen over the full Activity
	  protected void showSplashScreen() {
	      mSplashDialog = new Dialog(this, R.style.SplashScreen);
	      mSplashDialog.setContentView(R.layout.splash);
	      mSplashDialog.setCancelable(false);
	      mSplashDialog.show();
	   
	      // Set Runnable to remove splash screen just in case
	      final Handler handler = new Handler();
	      handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	          removeSplashScreen();
	        }
	      }, 3000);
	  }
*/	   
	   
	  private class AttemptLogin extends AsyncTask<Void, Void, Void> {
		  
		   HttpResponse response;
		   AlertDialog.Builder builder;
		   String uname;
		   String pass;
		   String message;
		   String cookieName;
		   String cookieValue;
		   int cookieVersion;
		   String cookieDomain;
		   String cookiePath;
		   String cookieExpiry;
		   
		   public AttemptLogin(String username, String password){
			   uname = username;
			   pass = password;
			   message = getResources().getString(R.string.signing_in);
		   }

		   @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	        	//progress dialog for logging in (gives user feedback)
	            loginDialog = new ProgressDialog(PlaceBooks.this);
	            loginDialog.setMessage(message);
	            loginDialog.setIndeterminate(true);
	            loginDialog.setCancelable(false);
	            loginDialog.show();

	        }
			 
			@Override 
			protected Void doInBackground(Void... strings) {
										

		        	try{
    		           final HttpClient httpClient = new DefaultHttpClient();
               		   final HttpPost httpPost = new HttpPost(authenticationUrl);
               		   //added
               		   httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, 
               			    HttpVersion.HTTP_1_0); // Default to HTTP 1.0
               			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, 
               			    "UTF-8");
               		   
               		

	               		   try
	               		   {
	                   		    final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	                   		    parameters.add(new BasicNameValuePair("j_username", uname));
	                   		    parameters.add(new BasicNameValuePair("j_password", pass));
	                   		    parameters.add(new BasicNameValuePair("_spring_security_remember_me", "true"));
	                   		    
	                   		    System.out.println("Parameters === " +parameters);
	                   		    
	                   		    httpPost.setEntity(new UrlEncodedFormEntity(parameters));
	                   		    
		                   		    // Create a local instance of cookie store
		                   		    CookieStore cookieStore = new BasicCookieStore();
	
		                   		    // Create local HTTP context
		                   		    HttpContext localContext = new BasicHttpContext();
		                   		    // Bind custom cookie store to the local context
		                   		    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	                   		    
	                   		    //added
	                   		    httpPost.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, 
	                   			    HttpVersion.HTTP_1_1); // Use HTTP 1.1 for this request only
	                   			httpPost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, 
	                   			    Boolean.FALSE);
	                   		    
	                   		    response = httpClient.execute(httpPost, localContext);
	                   		    	HttpEntity entity = response.getEntity();
	                   		    	System.out.println("----------------------------------------");
	                   		    	System.out.println(response.getStatusLine());
	                   		    	
	                   		        CustomApp appState = ((CustomApp)getApplicationContext());
	                   		        List<Cookie> cookies = cookieStore.getCookies();	                   		        	                   		        
	                   		        
	                   		        cookieName = cookies.get(0).getName();
	                   		        cookieValue = cookies.get(0).getValue();
	                   		        cookieVersion = cookies.get(0).getVersion();
	                   		        cookieDomain = cookies.get(0).getDomain();
	                   		        cookiePath = cookies.get(0).getPath();
	                   		        cookieExpiry = null;

	                   		    	
	                   	            for (int i = 0; i < cookies.size(); i++) {
	                   	                System.out.println("Local cookie: " + cookies.get(i));
	                   	            }
	                   	            //EntityUtils.consume(entity);

	
	                   		    Log.i("PlaceBooks", "Status Code: " + response.getStatusLine().getStatusCode());
	                   		                                     		    
	                   		    for (final Header header : response.getAllHeaders())
	                   		    {
	                   		    	Log.i("PlaceBooks", header.getName() + "=" + header.getValue());
	                   		    }
	
	                   		    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                   		    response.getEntity().writeTo(bos);
	                   		    Log.i("PlaceBooks", "Content: " + bos.toString());
	
	                         }
	                         catch (final Exception e)
	                         {
	                           Log.e("PlaceBooks", e.getMessage(), e);
	                           // Dismiss the Dialog
	                           loginDialog.dismiss(); 
	                         }
                       
			             }
			             catch(Exception e){
			            	 e.printStackTrace();
			            	 loginDialog.dismiss();  
			             }
			             
			    
				return null;
		 	}
		 	
			 
			   @Override
		        protected void onPostExecute(Void unused) {
				   
				   if(response != null){
					   
						   if(response.getStatusLine().getStatusCode() == 200){ 
		              		 	//Login successful 
		              			//Write credentials to shared preferences (only need the username since password has been confirmed)
		              			saveCredentials(uname, pass, cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieExpiry);
	
		              			
		              			//createDownloadsFile(username); //TAKEN OUT FOR NOW
		              			
		              			//Take user to their bookshelf
		    		        	Intent intent = new Intent();//(PlaceBooks.this, TabLayoutActivity.class);
		    	        		intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
		    	        		intent.putExtra("username", uname);  //Pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity
		    	        		//intent.putExtra("password", password);
		    	        		startActivity(intent);
		    	        		//PlaceBooks.this.startActivity(intent);
		                        loginDialog.dismiss();
		    	        		endThisActivity(); //End placebooks activity
		              		 }
							 else if(response.getStatusLine().getStatusCode() == 401){
							 	 //Invalid username or password http 401
								 builder = new AlertDialog.Builder(PlaceBooks.this);
							     builder.setTitle(getResources().getString(R.string.invalid_usr_pass));
							     builder.setMessage(getResources().getString(R.string.please_check_usr_pass));
							     builder.setPositiveButton("OK", null);
							     AlertDialog dialog = builder.show();
			                     loginDialog.dismiss();
	
								   
							 }
							 else if(response.getStatusLine().getStatusCode() == 500){
							   	 //The service encountered an error. Please try again later.
								 builder = new AlertDialog.Builder(PlaceBooks.this);
							     builder.setTitle(getResources().getString(R.string.good_bad_news));
							     builder.setMessage(getResources().getString(R.string.good_bad_news2));
							     builder.setPositiveButton(getResources().getString(R.string.ok), null);
							     AlertDialog dialog = builder.show();
			                     loginDialog.dismiss();
	
							 }
		              		else{
		                        loginDialog.dismiss();
		                    	//invalidCredentials();
		              		 }
				   }
				   else{
					   //response = null
				       builder = new AlertDialog.Builder(PlaceBooks.this);
					   builder.setTitle(getResources().getString(R.string.server_problem));
					   builder.setMessage(getResources().getString(R.string.server_problem2));
					   builder.setPositiveButton(getResources().getString(R.string.ok), null);
					   AlertDialog dialog = builder.show();
                       loginDialog.dismiss();
				   }
				   
		            //loginDialog.dismiss();


		        }
		 
		
		     }
	  
	  @Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	       if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	           Log.d(this.getClass().getName(), "back button pressed");
	          finish();
	          
	       }
	       return super.onKeyDown(keyCode, event);
	   }

           
}