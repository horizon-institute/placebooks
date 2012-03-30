package placebooks.services.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * Class to encapsulate response from Peoples Collection API for Properties from trail items and super class for trail properties
 * @author pszmp
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionProperties {
    protected int id;
    protected String markup;
    protected String title;
    protected String titlecym;
    
    @JsonProperty("ex-small-thumbpath")
    protected String ex_small_thumbpath;
    protected String icontype;
    
    protected String objecttype;
    protected String mediaurl;
    protected String dateupdated;
    
    
    public PeoplesCollectionProperties()
    {
    	
    }
    
    public PeoplesCollectionProperties(int id, String markup, String title, String titlecym, String ex_small_thumbpath, String icontype, String mediaurl, String objecttype, String dateupdated)
    {
    	this.id = id;
    	this.markup = markup;
    	this.title = title;
    	this.titlecym = titlecym;
    	this.ex_small_thumbpath = ex_small_thumbpath;
    	this.icontype = icontype;
    	this.objecttype = objecttype;
    	this.mediaurl = mediaurl;
    	this.dateupdated = dateupdated;
    }
    
    public int GetId()
    {
    	return id;
    }
    
    public String GetMarkup()
    {
    	return markup;
    }
    
    public String GetTitle()
    {
    	return title;
    }
    
    public String GetExSmallThumbPath()
    {
    	return ex_small_thumbpath;
    }
    
    public String GetIcontype()
    {
    	return icontype;
    }
    
    public String GetTytleCym()
    {
    	return titlecym;
    }
    
    public String GetObjectType()
    {
    	return objecttype;
    }
    
    public String GetMediaURL()
    {
    	return mediaurl;
    }
    
    public String GetDateUpdated()
    {
    	return dateupdated;
    }
    
}
