package org.placebooks.www;

public class ImageItem extends Item {
	private String filename;
	private String URL;


	public String getFilename() {
		return filename;
	}
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String toString() {
		return "\nImageItem filename=" + filename + "\nURL= " + URL;
		//+ "\ntoString()=" + super.toString();
	}
}
