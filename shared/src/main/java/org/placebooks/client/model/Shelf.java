package org.placebooks.client.model;

import java.util.ArrayList;
import java.util.List;

public class Shelf
{
	private List<Entry> entries = new ArrayList<Entry>();

	private Group group;
	private User user;

	public Shelf()
	{
	}

	public List<Entry> getEntries()
	{
		return entries;
	}

	public Group getGroup()
	{
		return group;
	}

	public User getUser()
	{
		return user;
	}

	public void setGroup(final Group group)
	{
		this.group = group;
	}

	public void setUser(final User user)
	{
		this.user = user;
	}
}
