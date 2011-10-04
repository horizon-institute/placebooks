package org.placebooks.www;

public class TextItem extends Item {
	
	private String text;
	private String url;

	
	public String getURL() {
		return url;
	}
	public void setURL(String url) {
		this.url = url;
	}
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "Text=" + text + "\nurl=" + url + "\nPanel=" +super.getPanel() + "\nOrder=" + super.getOrder(); 
	}
	
}
