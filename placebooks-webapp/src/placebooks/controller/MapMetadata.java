package placebooks.controller;

import java.io.File;

import com.vividsolutions.jts.geom.Geometry;

public class MapMetadata
{
	private File file;
	private Geometry boundingBox;
	private int selectedScale;

	public MapMetadata(final File file, final Geometry boundingBox, final int selectedScale)
	{
		this.file = file;
		this.boundingBox = boundingBox;
		this.selectedScale = selectedScale;
	}

	public final Geometry getBoundingBox()
	{
		return boundingBox;
	}

	public final File getFile()
	{
		return file;
	}

	public final int getSelectedScale()
	{
		return selectedScale;
	}
}
