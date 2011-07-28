package org.placebooks.www;

import android.text.format.Time;
import java.util.*;
import com.vividsolutions.jts.geom.Geometry;

public class GPSTraceItem extends Item {
	
	private String name; //name of trace - we will put this as data
	//private StringBuilder gpxData;
	
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	/*
	public void setGpxData(StringBuilder gpx){
		this.gpxData = gpx;
	}
	public StringBuilder getGpxData(){
		return gpxData;
	}
	*/
	
	

}
