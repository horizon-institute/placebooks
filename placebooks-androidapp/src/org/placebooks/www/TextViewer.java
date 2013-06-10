package org.placebooks.www;

import org.apache.commons.lang.StringEscapeUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;

public class TextViewer extends Activity {
	
	private String text;
	
	 @Override
 	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	

	        getWindow().setWindowAnimations(0);	//Do not animate the view when it gets pushed on the screen
	        setContentView(R.layout.textview);

	        Intent intent = getIntent();
	        if(intent != null) text = intent.getStringExtra("text");
	        

	        ScrollView sv = (ScrollView) findViewById(R.id.svTextViewer);
	        String escapedHtml = StringEscapeUtils.escapeHtml(text);
			WebView wv = new WebView(this);
			wv.loadData(text, "text/html", "utf-8");
			wv.setBackgroundColor(0x00000000);
			sv.addView(wv);
	        
	        
	 }
}
