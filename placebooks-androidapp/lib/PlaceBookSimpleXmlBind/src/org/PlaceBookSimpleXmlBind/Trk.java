package org.PlaceBookSimpleXmlBind;

import java.util.*;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

//@Element(name="trk")
public class Trk {
	
	@Element(required=false)
	public String number;
	
	@ElementList
    public ArrayList<Trkpt> trkseg;
	
	public ArrayList<Trkpt> getTrkseg() {
         return trkseg;
    }
	
	
	public String getNumber(){
		return number;
	}
	
	
}
