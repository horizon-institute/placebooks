package org.placebooks.www;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.preference.Preference.OnPreferenceChangeListener;


public class MyPreferences extends PreferenceActivity {

	ListPreference list;
	Context context;
	private static final String PREF_LIST = "listPref";
    private String languageSelected;

	 
 	@Override
    protected void onCreate(Bundle savedInstanceState) {
    
	super.onCreate(savedInstanceState);
    context = this.getApplicationContext();
    addPreferencesFromResource(R.layout.preferences);
    PreferenceScreen prefs = getPreferenceScreen();

    final CustomApp appState = ((CustomApp)getApplicationContext());
    languageSelected  = appState.getLanguage();  
    Locale locale = new Locale(languageSelected);   
    Locale.setDefault(locale);  
    Configuration config = new Configuration();  
    config.locale = locale;  
    getBaseContext().getResources().updateConfiguration(config,   
    getBaseContext().getResources().getDisplayMetrics());  
    

    
    list = (ListPreference) prefs.findPreference(PREF_LIST); 
    
    if (languageSelected == "En"){
    	list.setValue("1");
    }
    else if(languageSelected =="Cy"){
    	list.setValue("2");
    }
    
    
    list.setOnPreferenceChangeListener(new
    		Preference.OnPreferenceChangeListener() {
    	  @Override
    	  public boolean onPreferenceChange(Preference preference, Object newValue) {
    	    final String val = newValue.toString();
    	    int index = list.findIndexOfValue(val);
    	    String currValue = list.getValue();
    	    String theValue = list.getValue();
    	    System.out.println("theValue === " +theValue);
    	    System.out.println("current language === " +appState.getLanguage());
    	    System.out.println("language selected === " +languageSelected);

    	    if(theValue.equals("2")){
    	    	appState.setLanguage("en");
    	    	
    	    	//Take user to their bookshelf
	        	Intent intent = new Intent();//(PlaceBooks.this, TabLayoutActivity.class);
        		intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
        		intent.putExtra("username", "markdavies_@hotmail.com");  //Pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity
        		//intent.putExtra("password", password);
        		startActivity(intent);
    	    }
    	    else if (theValue.equals("1")){
    	    	appState.setLanguage("cy");
    	    	
    	    	//Take user to their bookshelf
	        	Intent intent = new Intent();//(PlaceBooks.this, TabLayoutActivity.class);
        		intent.setClassName("org.placebooks.www", "org.placebooks.www.TabLayoutActivity");
        		intent.putExtra("username", "markdavies_@hotmail.com");  //Pass the username variable along as an extra in the Intent object, and then retrieve it from the newly launched Activity
        		//intent.putExtra("password", password);
        		startActivity(intent);
    	    }
    	        	    
    	    return true;
    	  }
    	});
    
    

    
    
    // Get the custom preference
    Preference customPref = (Preference) findPreference("signoutPref");
            customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                                    public boolean onPreferenceClick(Preference preference) {
                                    	
                                    	AlertDialog.Builder helpBuilder = new AlertDialog.Builder(MyPreferences.this);
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
                     		               
                                    	
                                        return true;

                                    }

                            });
    }
 	

 	
	public void clearPreferences(){
		
  	    SharedPreferences prefs = MyPreferences.this.getSharedPreferences("LOGIN_DETAILS", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
	    editor.clear();
	    editor.commit();
	     
	}
 	
 
}
