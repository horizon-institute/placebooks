package org.placebooks.services;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.placebooks.client.model.ServiceInfo;
import org.placebooks.controller.ItemFactory;
import org.placebooks.controller.PlaceBooksAdminHelper;
import org.placebooks.controller.PropertiesSingleton;
import org.placebooks.model.LoginDetails;
import org.placebooks.model.MediaItem;
import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.TextItem;
import org.placebooks.model.User;
import org.wornchaos.logger.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DeltaEntry;
import com.dropbox.client2.DropboxAPI.DeltaPage;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;
import com.google.gson.Gson;

public class DropBoxService extends Service
{
	private static final ServiceInfo SERVICE_INFO = new ServiceInfo("Dropbox", "http://www.dropbox.com", true);

	@Override
	public boolean checkLogin(final String username, final String password)
	{
		return false;
	}

	@Override
	public String getAuthenticationURL(final EntityManager manager, final User user, final String callbackURL)
	{
		try
		{
			final DropboxAPI<WebAuthSession> api = getDropboxAPI();
			final WebAuthInfo authInfo = api.getSession().getAuthInfo(callbackURL);

			// Store request token
			final LoginDetails details = user.getLoginDetails(SERVICE_INFO.getName());
			manager.getTransaction().begin();
			if (details == null)
			{
				final LoginDetails newDetails = new LoginDetails(user, SERVICE_INFO.getName(),
						authInfo.requestTokenPair.key, null, authInfo.requestTokenPair.secret);
				manager.persist(newDetails);
				user.add(newDetails);
				manager.merge(user);
			}
			else
			{
				details.setUserID(authInfo.requestTokenPair.key);
				details.setUsername(null);
				details.setPassword(authInfo.requestTokenPair.secret);
				manager.merge(details);
			}

			manager.getTransaction().commit();

			return authInfo.url;
		}
		catch(final Exception e)
		{
			Log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public ServiceInfo getInfo()
	{
		return SERVICE_INFO;
	}

	@Override
	protected void search(final EntityManager em, final User user, final double lon, final double lat,
			final double radius)
	{

	}

	@Override
	protected boolean shouldSync(final LoginDetails details)
	{
		if (details.getLastSync() != null)
		{
			final long difference = new Date().getTime() - details.getLastSync().getTime();
			if (difference < 240000) { return false; }
		}
		return true;
	}

	@Override
	protected void sync(final EntityManager manager, final User user, final LoginDetails details, final double lon,
			final double lat, final double radius)
	{
		try
		{
			final DropboxAPI<WebAuthSession> api = getDropboxAPI();
			if (details.getUsername() == null)
			{
				finishAuthentication(manager, api, details);
			}
			else
			{
				final AccessTokenPair authTokens = new AccessTokenPair(details.getUserID(), details.getPassword());
				api.getSession().setAccessTokenPair(authTokens);
			}

			final String cursor = details.getMetadata("cursor");
			DeltaPage<Entry> delta = api.delta(cursor);
			while (true)
			{
				for (final DeltaEntry<Entry> entry : delta.entries)
				{
					handleEntry(manager, user, details, api, entry);
				}

				if (delta.hasMore)
				{
					delta = api.delta(cursor);
				}
				else
				{
					break;
				}
			}

			details.setMetadata("cursor", delta.cursor);
		}
		catch (final DropboxUnlinkedException e)
		{
			manager.getTransaction().begin();
			user.remove(details);
			manager.merge(user);
			manager.getTransaction().commit();
		}
		catch (final DropboxServerException e)
		{
			if (e.error == 304)
			{
				Log.info("No changes");
			}
			else
			{
				Log.info(e.error + ":" + e.body);
			}
		}
		catch (final Exception e)
		{
			Log.error(e.getMessage(), e);
		}
	}

	private void finishAuthentication(final EntityManager manager, final DropboxAPI<WebAuthSession> api,
			final LoginDetails login)
	{
		try
		{
			final RequestTokenPair requestTokenPair = new RequestTokenPair(login.getUserID(), login.getPassword());
			api.getSession().retrieveWebAccessToken(requestTokenPair);

			login.setUserID(api.getSession().getAccessTokenPair().key);
			login.setPassword(api.getSession().getAccessTokenPair().secret);

			login.setUsername("");

			try
			{
				final String displayName = api.accountInfo().displayName;
				Log.info("Dropbox Display Name: " + displayName);
				if (displayName != null)
				{
					login.setUsername(displayName);
				}
			}
			catch (final Exception e)
			{
				Log.info(e.getMessage(), e);
			}

			manager.getTransaction().begin();
			manager.merge(login);
			manager.getTransaction().commit();
		}
		catch (final Exception e)
		{
			// TODO If dropbox auth refused - delete logindetails
			Log.error(e);
		}
	}

	private DropboxAPI<WebAuthSession> getDropboxAPI() throws Exception
	{
		final Properties properties = PropertiesSingleton.get(PlaceBooksAdminHelper.class.getClassLoader());
		final String key = properties.getProperty(PropertiesSingleton.DROPBOX_APIKEY);
		final String secret = properties.getProperty(PropertiesSingleton.DROPBOX_APISECRET);
		final AccessType access = AccessType.valueOf(properties.getProperty(PropertiesSingleton.DROPBOX_ACCESSTYPE,
																			AccessType.APP_FOLDER.name()));

		if (key == null || secret == null) { throw new Exception("Can't find Dropbox properties"); }

		final AppKeyPair appKeys = new AppKeyPair(key, secret);
		return new DropboxAPI<WebAuthSession>(new WebAuthSession(appKeys, access));
	}

	private void handleEntry(final EntityManager manager, final User user, final LoginDetails details,
			final DropboxAPI<WebAuthSession> api, final DeltaEntry<Entry> entry)
	{
		try
		{
			final Gson gson = new Gson();
			Log.info(gson.toJson(entry));

			if (entry.metadata == null)
			{
				final PlaceBookItem item = ItemFactory.GetExistingItem("dropbox" + entry.lcPath, manager);
				if (item != null)
				{
					manager.getTransaction().begin();
					item.deleteItemData();
					manager.remove(item);
					manager.getTransaction().commit();
				}
			}
			else
			{
				if (!entry.metadata.isDir)
				{
					manager.getTransaction().begin();
					final PlaceBookItem item = ItemFactory.createItem(	manager, "dropbox" + entry.lcPath,
																		entry.metadata.mimeType);
					if (item != null)
					{
						if (entry.metadata.isDeleted)
						{
							item.deleteItemData();
							manager.remove(item);
						}
						else
						{
							item.setOwner(user);
							item.addMetadataEntryIndexed("title", entry.metadata.fileName());
							item.addMetadataEntry("source", DropBoxService.SERVICE_INFO.getName());
							item.addMetadataEntry("dbhash", entry.metadata.hash);
							final String path = "Dropbox"
									+ entry.metadata.path.substring(0, entry.metadata.path.lastIndexOf("/"));
							item.addMetadataEntry("path", path);

							if (item instanceof MediaItem)
							{
								((MediaItem) item).writeDataToDisk(api.getFileStream(entry.metadata.path, null));
								// ((MediaItem)item).
							}
							else if (item instanceof TextItem)
							{
								final StringWriter writer = new StringWriter();
								final DropboxInputStream inputStream = api.getFileStream(entry.metadata.path, null);
								Log.info("Charset:" + inputStream.getFileInfo().getCharset());
								if (inputStream.getFileInfo().getCharset() != null)
								{
									IOUtils.copy(inputStream, writer, inputStream.getFileInfo().getCharset());
								}
								else
								{
									IOUtils.copy(inputStream, writer);
								}

								Log.info("Text: " + writer.toString());

								((TextItem) item).setText(writer.toString());
							}
							manager.merge(item);
						}
					}
					manager.getTransaction().commit();
				}
			}
		}
		catch (final Exception e)
		{
			if (manager.getTransaction().isActive())
			{
				manager.getTransaction().rollback();
			}
			Log.error(e);
		}
	}
}