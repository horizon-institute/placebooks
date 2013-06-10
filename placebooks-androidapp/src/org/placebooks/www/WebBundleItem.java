package org.placebooks.www;

public class WebBundleItem extends Item {
	
	private String url;
	private String filename;
	
	public String getURL() {
		return url;
	}
	public void setURL(String url) {
		this.url = url;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String toString() {

		return "\nFilename=" + filename + "\nurl=" + url + "\nColumn=" +super.getColumn() + "\nOrder=" +super.getOrder();
		
	}
	

}
