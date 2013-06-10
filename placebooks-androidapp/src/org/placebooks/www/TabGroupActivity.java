package org.placebooks.www;

import java.util.ArrayList;
import java.util.Locale;
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
import android.content.res.Configuration;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;



public class TabGroupActivity extends ActivityGroup {

    private Stack<String> mIds;
    private LocalActivityManager mActivityManager;
    private ViewAnimator mAnimator;
    private int mSerial;
    private String username;
    private static String distance1;
    private static String distance2;
    private static String distance3;
    private static String distance4;
    
    private static String item1;
    private static String item2;
    private static String item3;
    private static String item4;
    private static String item5;
    private static String item6;
    private static String item7;
    private static String item8;
    private static String item9;
    private static String item10;
    private static String item11;
    private static String item12;
    private static String item13;
    private static String item14;
    private static String item15;
    private static String item16;
    private static String item17;
    private static String item18;
    private static String item19;
    private static String item20;
    private static String item21;
    private static String item22;
    private static String item23;
    private static String item24;
    private static String item25;
    private static String item26;
    private static String item27;
    private static String item28;
    private static String item29;
    private static String item30;
    private static String item31;
    private static String item32;
    private static String item33;
    private static String item34;
    private static String item35;
    private static String item36;
    private static String item37;
    private static String item38;
    private static String item39;
    private static String item40;

    private String languageSelected;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
        Intent intent1 = getIntent();
        if(intent1 != null) username = intent1.getStringExtra("username");
        System.out.println("USERNAME======== "+ username);
        
        CustomApp appState = ((CustomApp)getApplicationContext());
        languageSelected  = appState.getLanguage();  
        Locale locale = new Locale(languageSelected);   
        Locale.setDefault(locale);  
        Configuration config = new Configuration();  
        config.locale = locale;  
        getBaseContext().getResources().updateConfiguration(config,   
        getBaseContext().getResources().getDisplayMetrics()); 
        
        distance1 = getResources().getString(R.string.distance1);
        distance2 = getResources().getString(R.string.distance2);
        distance3 = getResources().getString(R.string.distance3);
        distance4 = getResources().getString(R.string.distance4);
        
        item1 = getResources().getString(R.string.activity_list_item1);
        item2 = getResources().getString(R.string.activity_list_item2);
        item3 = getResources().getString(R.string.activity_list_item3);
        item4 = getResources().getString(R.string.activity_list_item4);
        item5 = getResources().getString(R.string.activity_list_item5);
        item6 = getResources().getString(R.string.activity_list_item6);
        item7 = getResources().getString(R.string.activity_list_item7);
        item8 = getResources().getString(R.string.activity_list_item8);
        item9 = getResources().getString(R.string.activity_list_item9);
        item10 = getResources().getString(R.string.activity_list_item10);
        item11 = getResources().getString(R.string.activity_list_item11);
        item12 = getResources().getString(R.string.activity_list_item12);
        item13 = getResources().getString(R.string.activity_list_item13);
        item14 = getResources().getString(R.string.activity_list_item14);
        item15 = getResources().getString(R.string.activity_list_item15);
        item16 = getResources().getString(R.string.activity_list_item16);
        item17 = getResources().getString(R.string.activity_list_item17);
        item18 = getResources().getString(R.string.activity_list_item18);
        item19 = getResources().getString(R.string.activity_list_item19);
        item20 = getResources().getString(R.string.activity_list_item20);
        item21 = getResources().getString(R.string.activity_list_item21);
        item22 = getResources().getString(R.string.activity_list_item22);
        item23 = getResources().getString(R.string.activity_list_item23);
        item24 = getResources().getString(R.string.activity_list_item24);
        item25 = getResources().getString(R.string.activity_list_item25);
        item26 = getResources().getString(R.string.activity_list_item26);
        item27 = getResources().getString(R.string.activity_list_item27);
        item28 = getResources().getString(R.string.activity_list_item28);
        item29 = getResources().getString(R.string.activity_list_item29);
        item30 = getResources().getString(R.string.activity_list_item30);
        item31 = getResources().getString(R.string.activity_list_item31);
        item32 = getResources().getString(R.string.activity_list_item32);
        item33 = getResources().getString(R.string.activity_list_item33);
        item34 = getResources().getString(R.string.activity_list_item34);
        item35 = getResources().getString(R.string.activity_list_item35);
        item36 = getResources().getString(R.string.activity_list_item36);
        item37 = getResources().getString(R.string.activity_list_item37);
        item38 = getResources().getString(R.string.activity_list_item38);
        item39 = getResources().getString(R.string.activity_list_item39);
        item40 = getResources().getString(R.string.activity_list_item40);
        
        
                
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
        adapter.add(distance1);
        adapter.add(distance2);
        adapter.add(distance3);
        adapter.add(distance4);
        spinnerDistance.setAdapter(adapter);
        
        Spinner spinnerActivity = (Spinner) v.findViewById(R.id.spinnerActivity);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);//dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.add(item1);
        adapter2.add(item2);
        adapter2.add(item3);
        adapter2.add(item4);
        adapter2.add(item5);
        adapter2.add(item6);
        adapter2.add(item7);
        adapter2.add(item8);
        adapter2.add(item9);
        adapter2.add(item10);
        adapter2.add(item11);
        adapter2.add(item12);
        adapter2.add(item13);
        adapter2.add(item14);
        adapter2.add(item15);
        adapter2.add(item16);
        adapter2.add(item17);
        adapter2.add(item18);
        adapter2.add(item19);
        adapter2.add(item20);
        adapter2.add(item21);
        adapter2.add(item22);
        adapter2.add(item23);
        adapter2.add(item24);
        adapter2.add(item25);
        adapter2.add(item26);
        adapter2.add(item27);
        adapter2.add(item28);
        adapter2.add(item29);
        adapter2.add(item30);
        adapter2.add(item31);
        adapter2.add(item32);
        adapter2.add(item33);
        adapter2.add(item34);
        adapter2.add(item35);
        adapter2.add(item36);
        adapter2.add(item37);
        adapter2.add(item38);
        adapter2.add(item39);
        adapter2.add(item40);
        spinnerActivity.setAdapter(adapter2);
        
        return v;
        
       
    }
    	
	
}

