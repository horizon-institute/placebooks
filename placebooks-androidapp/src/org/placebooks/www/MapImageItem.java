package org.placebooks.www;


public class MapImageItem extends Item {
	
	String filename;
	
	public String getFilename(){
		return filename;
	}
	
	public void setFilename(String fname){
		this.filename = fname;
	}
	
	
	
	@Override
	public String toString() {

		return "<Filename>" + filename + "</Filename>" + "<Geometry>" + super.getGeometry() + "</Geometry>" + "\nColumn=" +super.getColumn() + "\nOrder=" +super.getOrder();
		
	}

}
