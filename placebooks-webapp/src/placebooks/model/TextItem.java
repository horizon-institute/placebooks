package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class TextItem extends PlaceBookItem
{
	private String text;

	public TextItem(final User owner, final Geometry geom, final URL sourceURL, final String text)
	{
		super(owner, geom, sourceURL);
		this.text = text;
		index.addAll(SearchHelper.getIndex(text));
	}

	TextItem()
	{
		super();
	}

	@Override
	public void appendConfiguration(final Document config, final Element root)
	{
		log.info("TextItem.appendConfiguration(), text=" + this.getText());
		final Element item = getConfigurationHeader(config);
		final Element text = config.createElement("text");
		text.appendChild(config.createTextNode(this.getText()));
		item.appendChild(text);
		root.appendChild(item);
	}

	@Override
	public void deleteItemData()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetCSS()
	 */
	@Override
	public String GetCSS()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getEntityName()
	{
		return TextItem.class.getName();
	}

	/*
	 * (non-Javadoc) /* (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetHTML()
	 */
	@Override
	public String GetHTML()
	{
		final StringBuilder output = new StringBuilder();
		output.append("<div class='placebook-item-text' id='");
		output.append(this.getPlaceBook().getKey());
		output.append("'>");
		output.append(this.text);
		output.append("'</div>");
		return output.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see placebooks.model.PlaceBookItem#GetJavaScript()
	 */
	@Override
	public String GetJavaScript()
	{
		// TODO Auto-generated method stub
		return "";
	}

	public String getText()
	{
		return text;
	}

	public void setText(final String text)
	{
		this.text = text;
	}
}