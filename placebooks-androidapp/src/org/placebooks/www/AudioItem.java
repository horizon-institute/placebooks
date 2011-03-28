package org.placebooks.www;

public class AudioItem extends Item {
	
	private String filename;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return "\nFilename= " + filename; 
	}
	

}
