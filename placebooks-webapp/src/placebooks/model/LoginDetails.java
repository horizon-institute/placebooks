package placebooks.model;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class LoginDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@Temporal(TIMESTAMP)
	private Date lastSync;

	private boolean syncInProgress = false;

	@JsonIgnore
	private String password;

	private String service;

	@ManyToOne
	@JsonIgnore
	private User user;

	@JsonIgnore
	private String userid;

	private String username;

	@JsonIgnore
	@ElementCollection
	@Column(columnDefinition = "LONGTEXT")
	private Map<String, String> metadata = new HashMap<String, String>();
	
	public LoginDetails(final User user, final String service, final String userid, final String username,
			final String password)
	{
		this.user = user;
		this.userid = userid;
		this.service = service;
		this.username = username;
		this.password = password;
	}
	
	public String getMetadata(final String key)
	{
		return metadata.get(key);
	}
	
	public void setMetadata(final String key, final String value)
	{
		metadata.put(key, value);
	}

	LoginDetails()
	{
	}

	public String getID()
	{
		return id;
	}

	public Date getLastSync()
	{
		return lastSync;
	}

	public String getPassword()
	{
		return password;
	}

	public String getService()
	{
		return service;
	}

	public User getUser()
	{
		return user;
	}

	public String getUserID()
	{
		return userid;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isSyncInProgress()
	{
		return syncInProgress;
	}

	public void setLastSync()
	{
		lastSync = new Date();
	}

	public void setPassword(final String password)
	{
		this.password = password;
	}

	public void setSyncInProgress(final boolean inProgress)
	{
		syncInProgress = inProgress;
	}

	public void setUserID(final String userID)
	{
		userid = userID;
	}

	public void setUsername(final String username)
	{
		this.username = username;
	}
}