package org.placebooks.www;

import java.util.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.vividsolutions.jts.geom.Geometry;

@Root
public class Trkpt {
	
   // private ArrayList<Geometry> coordinates;
    	
	@Attribute
	public Double lat;	
	
	@Attribute
	public Double lon;
	
	@Element(required=false)
	public String ele;
	
	@Element(required=false)
	public String time;
	
	
	
	public Double getLat() {
		return lat;
     }

     public Double getLon() {
          return lon;
     }

     public String getEle() {
          return ele;
     }

     public String getTime() {
          return time;
     }

     @Override
 	public String toString(){

 		return "latitude=" + lat.toString() + "longitude=" + lon.toString();
 		
 	}



}
