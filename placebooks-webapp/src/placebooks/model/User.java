package placebooks.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

@PersistenceCapable
public class User
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Unique
	@Persistent
	private String email;

	@Persistent
	private String passwordHash;

	@Persistent
	private String name;

	@Persistent
	private Collection<PlaceBook> placebooks = new ArrayList<PlaceBook>();

	@Persistent
	@Join
	private Collection<User> friends = new ArrayList<User>();

	@Persistent
	private Collection<Group> groups = new ArrayList<Group>();

	public User(final String name, final String email, final String passwordHash)
	{
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	User()
	{

	}

	public void add(final PlaceBook placebook)
	{
		placebooks.add(placebook);
	}

	public void add(final User friend)
	{
		friends.add(friend);
	}

	public Iterable<User> friends()
	{
		return friends;
	}

	public String getEmail()
	{
		return email;
	}

	public String getKey()
	{
		return key;
	}

	public String getName()
	{
		return name;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public Iterable<Group> groups()
	{
		return groups;
	}

	public Iterable<PlaceBook> placebooks()
	{
		return placebooks;
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}