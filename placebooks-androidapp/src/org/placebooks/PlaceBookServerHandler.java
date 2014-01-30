package org.placebooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.NameValuePair;
import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.model.PlaceBookService;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Request;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.parser.gson.GsonParser;
import org.wornchaos.server.JSONServerHandler;

import android.content.Context;

public class PlaceBookServerHandler extends JSONServerHandler
{
	private static final Parser parser = new GsonParser();
	private final Context context;

	public static final PlaceBookService createServer(String host, Context context)
	{
		final PlaceBookService service = (PlaceBookService) Proxy.newProxyInstance(	PlaceBookService.class.getClassLoader(),
															new Class[] { PlaceBookService.class },
															new PlaceBookServerHandler(context));
		service.setHostURL(host);
		service.setConnectionStatus(new AndroidConnectionStatus(context));
		return service;
	}

	public PlaceBookServerHandler(Context context)
	{
		super(new GsonParser());
		this.context = context;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		// TODO Auto-generated method stub
		if (method.getName().equals("placebookPackage"))
		{
			final String id = (String) args[0];
			@SuppressWarnings("unchecked")
			final AsyncCallback<PlaceBook> callback = (AsyncCallback<PlaceBook>) args[1];
			try
			{
				final PlaceBook cached = getPlaceBook(getPlaceBookPath(id));
				if(cached != null)
				{
					callback.onSuccess(cached);
				}

				if (!isOnline())
				{
					return null;
				}
				
				final Request request = method.getAnnotation(Request.class);
				String url = getURL(method, request);
				url = url.replace("{id}", id);
				
				call(	url, new ArrayList<NameValuePair>(), org.wornchaos.client.server.Request.Method.GET,
						new AsyncCallback<InputStream>()
						{
							@Override
							public void onSuccess(final InputStream inputStream)
							{
								try
								{
									final File placebookPath = getPlaceBookPath(id);
									final File cacheDir = context.getExternalCacheDir();

									final File download = new File(cacheDir, id + "_download.zip");
									download.createNewFile();

									final FileOutputStream fileOutput = new FileOutputStream(download);

									copy(inputStream, fileOutput);

									fileOutput.flush();
									fileOutput.close();

									unzip(new FileInputStream(download), placebookPath);

									download.delete();

									getPlaceBook(placebookPath, callback);
								}
								catch (final Exception e)
								{
									Log.error(e);
								}
							}
						}, InputStream.class);
				return null;				
			}
			catch (final Exception e)
			{
				Log.error(e);
				callback.onFailure(e);
			}

		}
		return super.invoke(proxy, method, args);
	}

	private File getPlaceBookPath(final String id) throws IOException
	{
		final URL url = new URL(hostURL);
		File file = new File(context.getExternalFilesDir(null), url.getHost());
		if(url.getPath().length() != 0)
		{
			file = new File(file, url.getPath());
		}
		
		return new File(file, id);
	}
	
	public static void getPlaceBook(final File placebookPath, final AsyncCallback<PlaceBook> callback)
	{
		try
		{
			final PlaceBook placebook = getPlaceBook(placebookPath);
			if(placebook == null)
			{
				callback.onFailure(new Exception("Not Found"));
			}
			else
			{
				callback.onSuccess(placebook);
			}
		}
		catch(Exception e)
		{
			callback.onFailure(e);
		}
	}
	
	private static PlaceBook getPlaceBook(final File placebookPath) throws IOException
	{
		if (placebookPath.exists())
		{
			final File placebookJSON = new File(placebookPath, "data.json");
			Log.info("Placebook json " + placebookJSON.toString());
			if (placebookJSON.exists())
			{
				Log.info("Placebook json exists");
				final FileReader reader = new FileReader(placebookJSON);
				final PlaceBook placebook = parser.parse(PlaceBook.class, reader);
				placebook.setDirectory(placebookPath.getAbsolutePath());
				return placebook;
			}
		}
		
		return null;
	}
	
	private static void copy(final InputStream input, final OutputStream output) throws IOException
	{
		final byte[] buffer = new byte[4096];
		int size;
		while ((size = input.read(buffer)) != -1)
		{
			output.write(buffer, 0, size);
		}
	}

	public static void unzip(final InputStream inputStream, File targetDir) throws Exception
	{
		targetDir.mkdirs();
		final ZipInputStream zipFile = new ZipInputStream(inputStream);
		try
		{
			ZipEntry entry;
			while((entry = zipFile.getNextEntry()) != null)
			{
				final File targetFile = new File(targetDir, entry.getName());
				final OutputStream output = new FileOutputStream(targetFile);
				try
				{
					copy(zipFile, output);
				}
				finally
				{
					output.close();
				}
			}
		}
		finally
		{
			zipFile.close();
		}	
	}
}
