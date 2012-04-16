package org.placebooks.www;

import java.io.File;
import java.util.List;

import android.content.*;
import android.widget.BaseAdapter;
import android.os.Environment;
import android.view.*;
import android.widget.TextView;
import android.widget.ImageButton;


public class ImageAdapter extends BaseAdapter {
    Context mContext;
    private String unzippedDir;
    private List<MyListItemModel> items;
    private LayoutInflater mInflater;


    public ImageAdapter(Context c) {
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
				view = li.inflate(R.layout.griditem, null);
        }
        else{
        	view = convertView;
        }	
		        MyListItemModel item = items.get(position);
		         
		        
			        ImageButton redImageButton = (ImageButton)view.findViewById(R.id.icon_redbutton);
				 	redImageButton.setBackgroundColor(android.R.color.transparent);	//Makes the button background transparent
			        redImageButton.setOnClickListener(item.dl_listener);
		
			        ImageButton greenImageButton = (ImageButton)view.findViewById(R.id.icon_greenbutton);
				 	greenImageButton.setBackgroundColor(android.R.color.transparent);
			        greenImageButton.setOnClickListener(item.view_listener);
			        
			        //Check if the package has already been downloaded to the SDCard
					File f = new File(Environment.getExternalStorageDirectory() + "/PlaceBooks/Unzipped" + item.getPackagePath());
					if(f.exists()){
						//If exists then hide 'download' button and show 'view' button
						redImageButton.setVisibility(View.GONE);
						greenImageButton.setVisibility(View.VISIBLE);
					}
					else{
						//If it does not exist then hide 'view' button and show 'download' button
						
						redImageButton.setVisibility(View.VISIBLE);
						greenImageButton.setVisibility(View.GONE);
					}
			        
			        TextView textView = (TextView)view.findViewById(R.id.icon_text);
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
	        return view;
        
        
    }
    
}