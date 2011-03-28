package org.placebooks.www;

public class VideoItem extends Item {

	private String filename;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return "\nVideoItem " + "\nFilename= " + filename; 
	}
	
}
