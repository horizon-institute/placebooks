package org.placebooks.www;

import java.util.ArrayList;
import java.util.Locale;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.content.Intent;
import android.os.Bundle;
import android.content.Intent.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.app.Activity;



public class TabLayoutActivity extends TabActivity {
	
	private TabHost tabHost;
	private String username;
	private String languageSelected;

		//Called when the activity is first created
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tablayout);
	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen		       

	        Intent intent = getIntent();
	        if(intent != null) username = intent.getStringExtra("username");
	        Log.i("username=",username);
	        
	        CustomApp appState = ((CustomApp)getApplicationContext());
	        languageSelected  = appState.getLanguage();  
	        Locale locale = new Locale(languageSelected);   
	        Locale.setDefault(locale);  
	        Configuration config = new Configuration();  
	        config.locale = locale;  
	        getBaseContext().getResources().updateConfiguration(config,   
	        getBaseContext().getResources().getDisplayMetrics()); 
			
	        
	        setTabs() ;
	        

	    }
	    
	    private void setTabs()
		{
			addTab(getResources().getString(R.string.downloads), R.drawable.downloads_tab_icons, BookDownloads.class);//, username);
			addTab(getResources().getString(R.string.online_shelf), R.drawable.myshelf_tab_icons, Shelf.class);//, username);
			addTab(getResources().getString(R.string.search), R.drawable.search_tab_icons, TabGroupActivity.class);//, username);
			addTab(getResources().getString(R.string.settings), R.drawable.settings_tab_icons, MyPreferences.class);

		}
	 
		
	    //add tab with username
		private void addTab(String labelId, int drawableId, Class<?> c)//, String uname)
		{
			TabHost tabHost = getTabHost();
			Intent intent = new Intent(this, c);
			TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
			
			View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
			TextView title = (TextView) tabIndicator.findViewById(R.id.title);
			title.setText(labelId);
			ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
			icon.setImageResource(drawableId);
			
			spec.setIndicator(tabIndicator);
			intent.putExtra("username", username);	//passes the username to each of the activities
			spec.setContent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);	//refreshes the tab views
			tabHost.addTab(spec);

		}
		
	

	    
}
