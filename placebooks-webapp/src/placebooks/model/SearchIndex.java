package placebooks.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Extension(vendorName = "datanucleus", key = "mysql-engine-type", 
		   value = "MyISAM")
public class SearchIndex
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private Set<String> index = new HashSet<String>();

	public SearchIndex() { }

	public Set<String> getIndex() 
	{ 
		return Collections.unmodifiableSet(index); 
	}
	public void setIndex(Set<String> index)
	{ 
		this.index.clear();
		this.index.addAll(index);
	}

	public void add(String element) { index.add(element); }

	public void addAll(Set<String> elements) { index.addAll(elements); }

}

