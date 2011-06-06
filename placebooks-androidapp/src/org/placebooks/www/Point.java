package org.placebooks.www;

public class Point implements Comparable<Point>{

	public String type;
	public String data;			//Text for Text Item, Filename for other Items
	public int panel;
	public int order;
	
	public Point(String data, int panel, int order, String type){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
	}
	
	
	/*
	 * Compare a given order number with this object.
	 * If order of this object is greater than the
	 * received object, then this object is
	 * greater than the other
	 */
	public int compareTo(Point o) {
        return this.order - o.order ;
    }
	
	
	@Override
	public String toString() {
		return "<type>" +type +"</type>" + data;
	}

	
}
