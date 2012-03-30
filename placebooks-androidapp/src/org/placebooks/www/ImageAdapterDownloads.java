package org.placebooks.www;

import java.io.File;
import java.util.List;

import android.content.*;
import android.widget.BaseAdapter;
import android.os.Environment;
import android.view.*;
import android.widget.TextView;
import android.widget.ImageButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Toast;



public class ImageAdapterDownloads extends BaseAdapter {
    Context mContext;
    private String unzippedDir;
    private List<MyListItemModel> items;
    private LayoutInflater mInflater;



    public ImageAdapterDownloads(Context c) {
        mContext = c;
    } 
    
    /*
     *  Method for setting the list of items
     *  Call it when there is data ready and want to display it
     */
    public void setModel(List<MyListItemModel> items){
        this.items = items;
        notifyDataSetChanged();
    }
    
    public void setUnzippedDir(String dir){
    	unzippedDir = dir;
    }
    
    @Override
    public int getCount() {
        return items!=null?items.size():0;
    }
    
    @Override
    public Object getItem(int position) {
        return items!=null?items.get(position):null;
    }
    
    @Override
    public long getItemId(int position) {
        return items!=null?items.get(position).getBookId():-1;
    }


    //Create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
    	
        if (convertView == null) {  // if it's not recycled, initialize some attributes
   	        	LayoutInflater li = LayoutInflater.from(mContext);
				view = li.inflate(R.layout.griditem2, null);
        }
        else{
        	view = convertView;
        }	
		        MyListItemModel item = items.get(position);
		         
		
			        ImageButton greenImageButton = (ImageButton)view.findViewById(R.id.icon_greenbutton);
				 	greenImageButton.setBackgroundColor(android.R.color.transparent);
			        greenImageButton.setOnClickListener(item.view_listener);
			        greenImageButton.setOnLongClickListener(item.delete_listener);
			        TextView textView = (TextView)view.findViewById(R.id.icon_text2);
			        
					File f = new File(unzippedDir + item.getPackagePath());
			        if(f.exists()){
			        				        	
						greenImageButton.setVisibility(View.VISIBLE);
						textView.setVisibility(View.VISIBLE);
						
						//TextView textView = (TextView)view.findViewById(R.id.icon_text2);
				        //Check the length of the book title and make sure it takes up 2 lines, because android seems to keep setting the gravity to bottom and not top
				        //14 chars per line approx
				        int titleLength = item.getBookTitle().length();
					        if (titleLength < 15){
					        	textView.setText(item.getBookTitle() + "\n");
					        }
					        else if (titleLength >= 24){
					        	String cut = item.getBookTitle().substring(0, 22);
					        	textView.setText(cut + "..");
					        }
					        else{
					        	textView.setText(item.getBookTitle());
					        }
					}
			        /*else{
						greenImageButton.setVisibility(View.GONE);
						//textView.setVisibility(View.GONE);
					}*/
			        
			        
	        return view;
        
        
    }
    
}