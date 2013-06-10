package org.placebooks.www;

public class VideoItem extends Item {

	private String filename;
	private int videoHeight;
	private int mapPage = -1;
	private int mapMarker = -1;
	
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setVideoHeight(int height){
		this.videoHeight = height;
	}
	
	public int getVideoHeight(){
		return videoHeight;
	}
	
	public int getMapPage(){
		return mapPage;
	}
	
	public void setMapPage(int pageNumber){
		this.mapPage = pageNumber;
	}
	

	public int getMapMarker(){
		return mapMarker;
	}
	
	public void setMapMarker(int mapMarker){
		this.mapMarker = mapMarker;
	}
	
	@Override
	public String toString() {
		return "\nFilename=" + filename + "\nColumn=" +super.getColumn() + "\nOrder=" +super.getOrder(); 
	}
	
}
