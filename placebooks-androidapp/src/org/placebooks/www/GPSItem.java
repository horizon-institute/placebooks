package org.placebooks.www;

import android.text.format.Time;

public class GPSItem extends Item {
	//timestamp
	//geometry
	private String URL;
	//private StringBuilder gpx;
	private Time time;
	//strip out all the non alphanumeric chars (dashes and colons), then..
	//Time t = new Time();
	//time.parse("20110214T133110084Z");
	
	//bounds
	private float maxlat;
	private float maxlon;
	private float minlat;
	private float minlon;
	
	//trkseg
	//trkpt
	private float lat;
	private float lon;
	private float ele;
	
	//another time var
	
	
	
	
}
