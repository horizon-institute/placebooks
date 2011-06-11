package org.placebooks.www;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Gallery;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.TextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class VideoViewer extends Activity {
	
 private String videoFile;
 private String packagePath;
 private VideoView video;
 private MediaController ctlr;
	
	
	 @Override
 	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);	//icicle

	        //View v = new View(this);
	        //LinearLayout ll = new LinearLayout(this);
	        getWindow().setWindowAnimations(0);	//do not animate the view when it gets pushed on the screen

	        setContentView(R.layout.videoview);
	        
	        
	         // get the extras (video filename) out of the new intent
	        Intent intent = getIntent();
	        if(intent != null) videoFile = intent.getStringExtra("video");
	        if(intent != null) packagePath = intent.getStringExtra("path");
	        
	        File clip=new File(Environment.getExternalStorageDirectory(), "/placebooks/unzipped" + packagePath + "/" + videoFile);

			if (clip.exists()) {

					video = new VideoView(VideoViewer.this);
					video.setVideoPath(clip.getAbsolutePath());

					video.setLayoutParams(new Gallery.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

					ctlr=new MediaController(VideoViewer.this);
					ctlr.setMediaPlayer(video);
					video.setMediaController(ctlr);
					video.requestFocus();
					video.start();
                    setContentView(video);
	        
	        
			}
			else{
				TextView tv = new TextView(this);
				tv.setText("File does not exist");
				setContentView(tv);
				
				
			}

	 }
	 
}
