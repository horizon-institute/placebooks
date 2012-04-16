package org.placebooks.www;

public class GPSTraceItem extends Item {
	
	private String name; //name of trace (e.g my trip) - we will put this as data
	private String gpxFilename;
	
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getGpxFilename(){
		return gpxFilename;
	}
	
	public void setGpxFilename(String fname){
		this.gpxFilename = fname;
	}

}
