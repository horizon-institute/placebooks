package org.placebooks.www;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Environment;

import java.io.*;
import java.util.Locale;


public class BookDownloads extends ActivityGroup {
	
	private String username;
	private String unzippedRoot;
	private String root;
	private String unzippedDir;
	
	private JSONObject json;
	
	//private File file;
	private ProgressDialog myDialog = null;
    public static final int dialogDownloadProgress = 0;
    private ProgressDialog mProgressDialog;
    private String filename= "downloadFile.zip"; 
    
    private String languageSelected;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.bookdownloads);
	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		   
	        
	        
	        CustomApp appState = ((CustomApp)getApplicationContext());
	        unzippedRoot = appState.getUnzippedRoot();
	        root = appState.getRoot();
	        unzippedDir = appState.getUnzippedDir();
	        
	        languageSelected  = appState.getLanguage();  
	        Locale locale = new Locale(languageSelected);   
	        Locale.setDefault(locale);  
	        Configuration config = new Configuration();  
	        config.locale = locale;  
	        getBaseContext().getResources().updateConfiguration(config,   
	        getBaseContext().getResources().getDisplayMetrics()); 
	        
	        
	        //Retrieve the username.
	        Intent intent = getIntent();
	        if(intent != null) username = intent.getStringExtra("username");
	        //if(intent != null) password = intent.getStringExtra("password");
	        System.out.println("Username = " + username);
	        
//	        ImageButton logoutButton = (ImageButton) findViewById(R.id.logoutButton);

	        
	      //Load cached shelf on startup
	      //getCachedShelf();
		  displayCachedShelf();
		  
/*		  logoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
	        	
		            //Vibrator vib = (Vibrator) getSystemService(BookDownloads.this.VIBRATOR_SERVICE);
		            //vib.vibrate(300);
		            
		            //ask user are they sure they want to log out
		            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(BookDownloads.this);
		            helpBuilder.setTitle(getResources().getString(R.string.sign_out));
		            helpBuilder.setMessage(getResources().getString(R.string.sign_out_q));
		            helpBuilder.setPositiveButton(getResources().getString(R.string.yes),
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

	        
	}
	
	public void clearPreferences(){
		
  	    SharedPreferences prefs = BookDownloads.this.getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
	    editor.clear();
	    editor.commit();
	     
	}
	
	public void getCachedShelf(){
		
		json = JSONfunctions.getJSONfromSDCard(unzippedRoot + username + "_bookDownloads.json");	//JSON file containing all the info on the books that have been downloaded
        System.out.println("Reading the cached shelf on startup");
        //display the cached shelf
		displayCachedShelf();
		
	}
	
	public void displayCachedShelf(){
		 
		GridView gridview = (GridView) findViewById(R.id.gridview2);		       
        List<MyListItemModel> myListModel = new ArrayList<MyListItemModel>();
        
        try{
            FileInputStream fstream = new FileInputStream(unzippedRoot + username + "_downloads.json");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
          //Read File Line By Line
            int i=0;
            while ((strLine = br.readLine()) != null)   {
            	
        				
	        	final MyListItemModel item = new MyListItemModel(this);
	        	JSONObject e = new JSONObject(strLine);
	        	
	        	
	        		item.setTitle(e.optString("title"));
	           		item.setID(i);	//Owner ID
		        	item.setKey(e.optString("key"));   //Book key
		        	item.setDescription(e.optString("description"));	//Book description
		        	item.setPackagePath(e.optString("packagePath"));
		        	
		        	//item.setOwner(Integer.parseInt(jObject.getString("owner"))); 
		        	//item.setTimestamp(Integer.parseInt(jObject.getString("timestamp")));
		        	//item.setPreviewImage(e.getString("previewImage"));
		        	//item.setNumItems(Integer.parseInt(jObject.getString("numItems")));
	       		
	        	 
			
		        	item.view_listener = new OnClickListener(){
					       public void  onClick  (View  v){
					    	   

	        				          //Vibrator vib = (Vibrator) getSystemService(BookDownloads.this.VIBRATOR_SERVICE);
	        				          //vib.vibrate(300);
	        				          try{  
	        				            //Open book
        				            	Intent intent = new Intent();
   						        		intent.setClassName("org.placebooks.www", "org.placebooks.www.Reader");
   						        		intent.putExtra("packagePath", item.getPackagePath());
   						        		startActivity(intent);
	        				          }
	        				          catch(Exception e){
	        				        	  e.printStackTrace();
	        				          }

					        		
	        		        	}

							   };
							   
							   
							   
							   item.delete_listener = new OnLongClickListener() {
						             @Override
						             public boolean onLongClick(View v) {
						             
						            	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(BookDownloads.this);
	        							helpBuilder.setIcon(R.drawable.icon);
	        				            helpBuilder.setTitle(item.getBookTitle());
	        				            helpBuilder.setMessage(getResources().getString(R.string.delete_q));
	        				            helpBuilder.setPositiveButton(getResources().getString(R.string.delete),
	        				              new DialogInterface.OnClickListener() {

	        				               public void onClick(DialogInterface dialog, int which) {
		        				                
	        				            	 //Delete book from SDCard
		        				            	 File book = new File(unzippedDir + item.getPackagePath());
		        				            	 deleteDirectory(book);
		        				            	 
		        				            	
	      				            	 
		        				            	 
		        				            	 //Delete book from JSON file
		        				            	 try {

		        				            		  File jFile = new File(unzippedRoot + username + "_downloads.json");
		        				            	      File inFile = jFile;
		        				            	      
		        				            	      if (!inFile.isFile()) {
		        				            	        System.out.println("Parameter is not an existing file");
		        				            	        return;
		        				            	      }
		        				            	 
		        				            	 
		        				            	    //Construct the new JSON file that will later be renamed to the original filename. 
		        				            	      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
		        				            	      
		        				            	      BufferedReader br = new BufferedReader(new FileReader(jFile));
		        				            	      
		        				            	      
		        				            	      
		        				    			     JSONObject jsonObject = new JSONObject();
		        				    			     try {
			        				            	     jsonObject.put("title", item.getBookTitle());
			        				 				     jsonObject.put("description", item.getDescription());
			        				 				     jsonObject.put("packagePath", item.getPackagePath());
			        				 				     jsonObject.put("key", item.getKey());
		        				    			     
		        				    			     	}
		        				    			     catch(JSONException jsone){
		        				   			    	  jsone.printStackTrace();
		        				    			     	}
		        				            	     
		        				 				      String lineToRemove = jsonObject.toString();
		        				            	      //String lineToRemove = item.getKey();
		        				            	      //look for the line that has a matching package key (as this is unique to each book)
		        				            	      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		        				            	      
		        				            	      String line = null;

		        				            	      //Read from the original JSON file and write to the new 
		        				            	      //unless content matches data to be removed.
		        				            	      while ((line = br.readLine()) != null) {
		        				            	        
		        				            	        if (!line.trim().equals(lineToRemove)) {

		        				            	          pw.println(line);
		        				            	          pw.flush();
		        				            	          
		        				            	        }
		        				            	      }
		        				            	      pw.close();
		        				            	      br.close();
		        				            	      
		        				            	      //Delete the original JSON file
		        				            	      if (!inFile.delete()) {
		        				            	        System.out.println("Could not delete file");
		        				            	        return;
		        				            	      } 
		        				            	      
		        				            	      //Rename the new JSON file to the filename the original file had.
		        				            	      if (!tempFile.renameTo(inFile))
		        				            	        System.out.println("Could not rename file");
		        				            	      
		        				            	   
		        				            	      //Finally, refresh the view so the book disappears
				        							  //switchTabInActivity(0);	//refresh the view
		        				            	      Intent intent = new Intent();
				   						        	  intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
				   						        	  intent.putExtra("username", username);
				   			     				      intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				   						        	  startActivity(intent);
				   						        	  
				   						        	  //Display a message to the user to let them know that the book has been deleted
				   						        	  Toast msg = Toast.makeText(BookDownloads.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT);
				   						        	  msg.show();
		        				            	      
		        				            	    }
		        				            	    catch (FileNotFoundException ex) {
		        				            	      ex.printStackTrace();
		        				            	    }
		        				            	    catch (IOException ex) {
		        				            	      ex.printStackTrace();
		        				            	    }

	        				            	   
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

						            	 
						            	 
						            	 
						            	 return true;
						             }
						       };
							   
							   
							   
							   
							   
							   
							   
							   
							   
	        	
	        	myListModel.add(item);	//Add the item to the arraylist of ListItems

	        		
	        	
		        	/*if(jObject.getString("name") != null){
		        		shelfOwner = jObject.getString("name");  //owner's name
		        	}*/
		        	
	        	
	        // Print the content on the console
            System.out.println (strLine);
            i++;
            }
            //Close the input stream
            in.close();

	        	}catch(Exception e) {
	        		Log.e("log_tag", "Error parsing data "+e.toString());
	        	}
	 
	 
	    //Code for the new bookshelf using a gridview   	
	    ImageAdapterDownloads adapter = new ImageAdapterDownloads(this);
	    adapter.setModel(myListModel);		//pass the ArrayList into the Adapter
	    adapter.setUnzippedDir(unzippedDir);
        gridview.setAdapter(adapter);
		
		
        
	}
	

	 public void switchTabInActivity(int indexTabToSwitchTo){
         TabLayoutActivity ParentActivity;
         ParentActivity = (TabLayoutActivity) this.getParent();
         ParentActivity.getTabHost().setCurrentTab(indexTabToSwitchTo);//switchTab(indexTabToSwitchTo);
	   }
	 
	 static public boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      if (files == null) {
		          return true;
		      }
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }
 
	

}
