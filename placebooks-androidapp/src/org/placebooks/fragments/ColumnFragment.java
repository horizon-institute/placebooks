package org.placebooks.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.placebooks.R;
import org.placebooks.activity.item.MapCanvas;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Page;
import org.placebooks.client.model.PlaceBook;
import org.wornchaos.logger.Log;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class ColumnFragment extends Fragment
{
	private int pageNumber;
	private int column;
	private Page page;
	private PlaceBook placebook;

	public ColumnFragment()
	{

	}

	public int getColumn()
	{
		return column;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.column, null);

		final LinearLayout layout = (LinearLayout) view.findViewById(R.id.itemPanel);

		populate(layout);

		return view;
	}

	public void setPage(final PlaceBook placebook, final Page page, final int pageNumber, final int column)
	{
		this.placebook = placebook;
		this.page = page;
		this.pageNumber = pageNumber;
		this.column = column;
	}

	private String getPath(final Item item)
	{
		return new File(placebook.getDirectory(), item.getHash()).getAbsolutePath();
	}

	private Uri getURI(final Item item)
	{
		final File file = new File(placebook.getDirectory(), item.getHash());
		return Uri.fromFile(file);
	}

	private void populate(final ViewGroup container)
	{
		if (page == null) { return; }
		Log.info("Populate page " + pageNumber + "." + column);
		final List<Item> items = new ArrayList<Item>();
		for (final Item item : page.getItems())
		{
			if (item.getParameter("column", 0) == column)
			{
				items.add(item);
			}
		}

		Collections.sort(items, new Comparator<Item>()
		{
			@Override
			public int compare(final Item lhs, final Item rhs)
			{
				final Integer lorder = lhs.getParameter("order", 0);
				final Integer rorder = rhs.getParameter("order", 0);
				return lorder - rorder;
			}
		});

		container.removeAllViews();

		for (final Item item : items)
		{
			switch (item.getType())
			{
				case TextItem:
					final WebView textView = new WebView(container.getContext());
					textView.loadData(item.getText(), "text/html", null);
					container.addView(textView);
					break;

				case ImageItem:
					final ImageView imageView = new ImageView(container.getContext());
					imageView.setImageURI(getURI(item));
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.setAdjustViewBounds(true);
					container.addView(imageView);
					break;

				case MapImageItem:
					final MapCanvas mapView = new MapCanvas(container.getContext());
					mapView.setImageURI(getURI(item));
					mapView.setScaleType(ScaleType.FIT_XY);
					mapView.setAdjustViewBounds(true);
					mapView.setGeometry(item.getGeom());
					for (final Page page : placebook.getPages())
					{
						for (final Item mapItem : page.getItems())
						{
							if (mapItem.getParameter("mapPage", -1) == (pageNumber - 1) && mapItem.getGeom() != null)
							{
								mapView.addMapItem(mapItem);
							}
						}
					}

					container.addView(mapView);
					break;

				case VideoItem:
					Log.info("Adding video");
					final VideoView videoView = new VideoView(container.getContext());
					final MediaController controller = new MediaController(container.getContext(), false);
					controller.setAnchorView(videoView);
					videoView.setLayoutParams(new LinearLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT, 600));
					videoView.setMediaController(controller);
					videoView.setVideoPath(getPath(item));

					container.addView(videoView);
					break;

				default:
					break;
			}
		}
	}
}
