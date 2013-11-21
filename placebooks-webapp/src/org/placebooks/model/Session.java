package org.placebooks.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Session
{
	@Id
	private String id;
	
	private User user;
	
	private Date expire;

	public Session()
	{
		
	}
	
	public Session(final User user)
	{
		id = UUID.randomUUID().toString();
		this.user = user;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Date getExpire()
	{
		return expire;
	}

	public void setExpire(Date expire)
	{
		this.expire = expire;
	}
}