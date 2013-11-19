package org.placebooks.activity.item;

import org.placebooks.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ScrollView;

public class TextViewer extends Activity
{

	private String text;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getWindow().setWindowAnimations(0); // Do not animate the view when it gets pushed on the
											// screen
		setContentView(R.layout.textview);

		final Intent intent = getIntent();
		if (intent != null)
		{
			text = intent.getStringExtra("text");
		}

		final ScrollView sv = (ScrollView) findViewById(R.id.svTextViewer);
		final WebView wv = new WebView(this);
		wv.loadData(text, "text/html", "utf-8");
		wv.setBackgroundColor(0x00000000);
		sv.addView(wv);
	}
}
