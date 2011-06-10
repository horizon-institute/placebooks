package org.placebooks.www;

import android.text.format.Time;
import java.util.*;

public class GPSTraceItem extends Item {
	
	//trkpt attributes to get are: lat, lon, <ele> </ele> <time></time><speed></speed> </trkpt

	private StringBuilder data;
	private String URL;
	private String name; //name of trace
	//private StringBuilder gpx;
	private Time time;
	//private ArrayList <CustomType> trail;	
	
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
