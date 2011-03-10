package placebooks.model;

import java.util.Collection;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class Account
{
	@Persistent
	private String email;

	@Persistent
	private String passwordHash;

	@Persistent
	private String name;

	@Persistent
	private Collection<PlaceBook> placebooks;

	@Persistent
	private Collection<Account> friends;

	@Persistent
	private Collection<Group> groups;

	public Account(final String email, final String passwordHash)
	{

	}

	Account()
	{

	}

	public void add(final Account friend)
	{
		friends.add(friend);
	}

	public void add(final PlaceBook placebook)
	{
		placebooks.add(placebook);
	}

	public Iterable<Account> friends()
	{
		return friends;
	}

	public String getEmail()
	{
		return email;
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