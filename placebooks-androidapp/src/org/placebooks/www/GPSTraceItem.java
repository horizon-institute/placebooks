package org.placebooks.www;

import android.text.format.Time;
import java.util.*;

public class GPSTraceItem extends Item {
	
	//trkpt attributes to get are: lat, lon, <ele> </ele> <time></time><speed></speed> </trkpt

	private String name; //name of trace - we will put this as data

	//wpt attributes lat/lon is what we want out of it
	
	//strip out all the non alphanumeric chars (dashes and colons), then..
	//Time t = new Time();
	//time.parse("20110214T133110084Z");
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	

}
