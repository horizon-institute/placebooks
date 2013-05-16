package placebooks.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SearchIndex
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@Column(name = "searchindex")
	@ElementCollection
	private Set<String> index = new HashSet<String>();

	public SearchIndex()
	{
	}

	public void add(final String element)
	{
		index.add(element);
	}

	public void addAll(final Set<String> elements)
	{
		index.addAll(elements);
	}

	public void clear()
	{
		index.clear();
	}

	public String getID()
	{
		return id;
	}

	public Set<String> getIndex()
	{
		return Collections.unmodifiableSet(index);
	}

	public void setID(final String id)
	{
		this.id = id;
	}

	public void setIndex(final Set<String> index)
	{
		this.index.clear();
		this.index.addAll(index);
	}
}
