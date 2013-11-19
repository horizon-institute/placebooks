package org.placebooks.activity.item;

import java.io.File;
import java.io.IOException;

import org.placebooks.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class AudioViewer extends Activity
{

	private String filename;
	private String path;
	// Audio Variables
	private MediaPlayer mp = new MediaPlayer();
	private ImageButton ibAudioPlay;
	private ImageButton ibAudioPause;
	private ImageButton ibAudioStop;
	private LinearLayout llAudio; // Audio layout
	private LinearLayout pageView;

	public void displayAudio(final String audio)
	{

		final String myAudioPath = path + File.separator + audio;

		final File audioFile = new File(myAudioPath);
		if (audioFile.exists())
		{
			ibAudioPlay = new ImageButton(this);
			ibAudioPlay.setImageResource(R.drawable.play);

			ibAudioStop = new ImageButton(this);
			ibAudioStop.setImageResource(R.drawable.stop);

			ibAudioPause = new ImageButton(this);
			ibAudioPause.setImageResource(R.drawable.pause);

			llAudio = new LinearLayout(this); // Create a new linear layout for the Audio buttons
												// (play/pause/stop)
			llAudio.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT)); // This wraps the new linear
																		// layout to the original
																		// and centres it

			// Assign the thumbnail image to a new space in the ImageButton Thumbnail ArrayList
			ibAudioPlay.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			llAudio.addView(ibAudioPlay);

			ibAudioPause.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			llAudio.addView(ibAudioPause);

			ibAudioStop.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			llAudio.addView(ibAudioStop);

			pageView.addView(llAudio); // Add the audio linear layout to the main linear layout

			ibAudioPlay.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{

					playAudio(audio);

				} // End of public void

			});

			ibAudioPause.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{

					pauseAudio();

				} // End of public void

			});

			ibAudioStop.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{

					stopAudio();

				} // End of public void

			});
		}// end of if file exists

	} // End of displayAudio() method

	// Start of audio methods

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().setWindowAnimations(0); // Do not animate the view when it gets pushed on the
											// screen
		setContentView(R.layout.audioview);

		final Intent intent = getIntent();
		if (intent != null)
		{
			filename = intent.getStringExtra("filename");
		}
		if (intent != null)
		{
			path = intent.getStringExtra("path");
		}

		pageView = (LinearLayout) findViewById(R.id.linearLayoutAudioView);
		// New custom view that adds a bit of spacing to the end of image items
		final View view = new View(this);
		view.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 10));
		pageView.addView(view);
		displayAudio(filename);

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		stopAudio();
		System.gc(); // Call the garbage collector
		finish(); // Close the activity

	}

	public void pauseAudio()
	{

		mp.pause();

		ibAudioPlay.setEnabled(true);
		ibAudioPause.setEnabled(false);
		ibAudioStop.setEnabled(true);

	}

	public void playAudio(final String audioFile)
	{

		try
		{
			mp.setDataSource(path + File.separator + audioFile);
		}
		catch (final IllegalArgumentException e)
		{

			e.printStackTrace();

		}
		catch (final IllegalStateException e)
		{

			e.printStackTrace();

		}
		catch (final IOException e)
		{

			e.printStackTrace();
		}
		try
		{
			mp.prepare(); // This method is synchronous; as soon as it returns the clip is ready to
							// play. There is also prepareAsync() which is asynchronous
		}
		catch (final IllegalStateException e)
		{

			e.printStackTrace();

		}
		catch (final IOException e)
		{

			e.printStackTrace();

		}
		mp.start();
		ibAudioPlay.setEnabled(false);
		ibAudioPause.setEnabled(true);
		ibAudioStop.setEnabled(true);
	}

	public void stopAudio()
	{

		mp.stop();

		ibAudioPause.setEnabled(false);
		ibAudioStop.setEnabled(false);

		try
		{
			// mp.prepare(); //Prepare it again so it can play again if you want (restarts it)
			mp.seekTo(0); // Seek to the start of the audio file
			ibAudioPlay.setEnabled(true);
		}
		catch (final Throwable t)
		{
			goBlooey(t);
		}

	}

	private void goBlooey(final Throwable t)
	{

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Exception!").setMessage(t.toString()).setPositiveButton("OK", null).show();
	}
	// End of audio methods

}
