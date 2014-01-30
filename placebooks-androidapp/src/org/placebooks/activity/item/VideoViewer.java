package org.placebooks.activity.item;

import java.io.File;

import org.placebooks.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoViewer extends Activity
{
	private String videoFile;
	private String packagePath;
	private VideoView videoView;
	private MediaController ctlr;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().setWindowAnimations(0); // Do not animate the view when it gets pushed on the
											// screen
		setContentView(R.layout.videoview);

		// Get the extras (video filename) out of the new intent
		final Intent intent = getIntent();
		if (intent != null)
		{
			videoFile = intent.getStringExtra("video");
		}
		if (intent != null)
		{
			packagePath = intent.getStringExtra("path");
		}

		final File clip = new File(/* unzippedDir + */packagePath + File.separator + videoFile);
		System.out.println("Complete video path === " + packagePath + File.separator + videoFile);

		try
		{
			if (clip.exists())
			{

				videoView = new VideoView(VideoViewer.this);
				videoView.setVideoPath(clip.getAbsolutePath());

//				videoView.setLayoutParams(new Gallery.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
//						android.view.ViewGroup.LayoutParams.FILL_PARENT));

				ctlr = new MediaController(VideoViewer.this);
				ctlr.setMediaPlayer(videoView);
				videoView.setMediaController(ctlr);
				videoView.requestFocus();
				videoView.start();
				setContentView(videoView);

			}
			else
			{
				final TextView tv = new TextView(this);
				tv.setText("File does not exist");
				setContentView(tv);

			}
		}

		catch (final OutOfMemoryError E)
		{
			// Release some (all) of the above objects
			System.out.println("Out of Memory Exception");
			final TextView txtView = new TextView(VideoViewer.this);
			txtView.setText("Error: Out of Memory - video file is too big to load!");
			setContentView(txtView);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		videoView = null;
		packagePath = null;
		videoFile = null;
		ctlr = null;

		System.gc(); // Call the garbage collector
		finish(); // Close the activity

	}

}
