package org.placebooks.client.model;

import java.util.ArrayList;
import java.util.List;

public class User
{
	private String email;
	private String id;
	private List<LoginDetails> details = new ArrayList<LoginDetails>();
	private String name;
	private List<Group> groups = new ArrayList<Group>();

	public List<LoginDetails> getDetails()
	{
		return details;
	}

	public String getEmail()
	{
		return email;
	}

	public List<Group> getGroups()
	{
		return groups;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}
