package org.placebooks.client;

import org.placebooks.client.ui.images.Images;
import org.placebooks.client.ui.styles.Styles;

import com.google.gwt.core.client.GWT;

public class Resources
{
	public static final Images IMAGES = GWT.create(Images.class);
	public static final Styles STYLES = GWT.create(Styles.class);
}
