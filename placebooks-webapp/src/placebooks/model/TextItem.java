package placebooks.model;

import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
public class TextItem extends PlaceBookItem
{
	@Lob
	private String text;

	public TextItem(final User owner, final Geometry geom, final URL sourceURL,
					final String text)
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

	@Override
	public String getEntityName()
	{
		return TextItem.class.getName();
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
