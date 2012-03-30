package org.placebooks.www;

import java.util.ArrayList;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.ImageButton;
import android.os.Vibrator;
import android.app.AlertDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;


/**
 * The purpose of this Activity is to manage the activities in a tab.
 * Note: Child Activities can handle Key Presses before they are seen here.
 * @author Eric Harlow
 */
public class TabGroupActivity extends ActivityGroup {

    private Stack<String> mIds;
    private LocalActivityManager mActivityManager;
    private ViewAnimator mAnimator;
    private int mSerial;
    private String username;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        Intent intent1 = getIntent();
        if(intent1 != null) username = intent1.getStringExtra("username");
        System.out.println("USERNAME======== "+ username);
                
        setContentView(R.layout.searchmain);
        mIds = new Stack<String>();
        mAnimator = (ViewAnimator) findViewById(R.id.animator);
        mActivityManager = getLocalActivityManager();
        
         
        Intent intent2 = new Intent(this, SearchForm.class);
        intent2.putExtra("username", username);

        startActivity(intent2);

        
    }
    
    @Override
    public void startActivity(Intent intent) {
        System.out.println("starting " + intent);
        String id = "id" + mSerial++;
        mIds.push(id);
        View view = mActivityManager.startActivity(id, intent).getDecorView();
        mAnimator.addView(view);
        mAnimator.setDisplayedChild(mIds.size() - 1);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int size = mIds.size();
            if (size > 0) {
                String topId = mIds.pop();
                View view = mActivityManager.destroyActivity(topId, true).getDecorView();
                mAnimator.removeView(view);
                if (size > 1) {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    
    
 /*   
    public static TextView makeTextView(Context context, String text, int bgColor) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(20);
        tv.setTextColor(0xeeeeeeee);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(bgColor);
        return tv;
    }
    */
    
    public static View makeSpinner(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.spinner, null);
        Spinner spinnerDistance = (Spinner) v.findViewById(R.id.spinnerDistance);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);//dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Up to 10 Miles");
        adapter.add("Up to 25 Miles");
        adapter.add("Up to 50 Miles");
        adapter.add("Everywhere");
        spinnerDistance.setAdapter(adapter);
        
        Spinner spinnerActivity = (Spinner) v.findViewById(R.id.spinnerActivity);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);//dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.add("<No Activity>");
        adapter2.add("Walking");
        adapter2.add("Running");
        adapter2.add("Driving");
        adapter2.add("Road Biking");
        adapter2.add("Mountain Biking");
        adapter2.add("Hiking");
        adapter2.add("Motor Cycling");
        adapter2.add("Sightseeing");
        adapter2.add("Trail Running");
        adapter2.add("Alpine Skiing");
        adapter2.add("Kayaking / Canoeing");
        adapter2.add("Geocaching");
        adapter2.add("Cross-Country Skiing");
        adapter2.add("Flying");
        adapter2.add("Mountaineering");
        adapter2.add("Sailing");
        adapter2.add("Backpacking");
        adapter2.add("Train");
        adapter2.add("Back-Country Skiing");
        adapter2.add("Offroading");
        adapter2.add("Roller Skating");
        adapter2.add("Snowshoeing");
        adapter2.add("ATV / Offroading");
        adapter2.add("Boating");
        adapter2.add("Relaxation");
        adapter2.add("Horseback Riding");
        adapter2.add("Photography");
        adapter2.add("Snowboarding");
        adapter2.add("Ice Skating");
        adapter2.add("Snowmobiling");
        adapter2.add("Hang Gliding / Paragliding");
        adapter2.add("Fly-Fishing");
        adapter2.add("Romantic Getaway");
        adapter2.add("Skateboarding");
        adapter2.add("Bird Watching");
        adapter2.add("Rock Climbing");
        adapter2.add("Paddleboarding");
        adapter2.add("Fishing");
        adapter2.add("Other");
        spinnerActivity.setAdapter(adapter2);
        
        return v;
        
       
    }
    	
	
}

