package org.placebooks.www;

public class TextItem extends Item {

	private String URL;
	private String Text;
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	
	@Override
	public String toString() {
		return "TextItem " + "\nURL= " + URL + "\nText=" + Text; 
	}
	
}
