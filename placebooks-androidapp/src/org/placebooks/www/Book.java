package org.placebooks.www;

import java.util.ArrayList;

public class Book {
	
	// Data Members
	    ArrayList<Object> books = new ArrayList<Object>(); //java.util.ArrayList();
	   
	   
	    // mutator method
	    public void addBook( XMLDataSet r ){books.add( r );}
	   
	  
	   // accessor methods
	   public int getSize(){ return books.size();}
	   
	   public Reader getReader( int i ){
	     return (Reader)books.get( i );}
	   
	   
}
