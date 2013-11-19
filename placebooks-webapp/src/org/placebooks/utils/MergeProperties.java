package org.placebooks.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.tapestry.util.text.LocalizedProperties;
import org.mortbay.log.Log;

public class MergeProperties
{
	public static void main(final String[] args)
	{
		final String directory = args[0];
		final File dir = new File(directory);

		// Map<String, String> properties = new HashMap<String, String>();
		final LocalizedProperties properties = new LocalizedProperties();
		for (final File file : dir.listFiles())
		{
			if (file.getName().endsWith(".properties"))
			{
				try
				{
					properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		try
		{
			final File newFile = new File(dir.getAbsolutePath() + "/LocalizableResource.properties");
			newFile.createNewFile();

			final FileWriter fileWriter = new FileWriter(newFile);
			final BufferedWriter writer = new BufferedWriter(fileWriter);
			for (final Object key : properties.getPropertyMap().keySet())
			{
				Log.info(key + "=" + properties.getPropertyMap().get(key));
				writer.write(key + "=" + properties.getPropertyMap().get(key) + "\n");
			}
			writer.flush();
			writer.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

		for (final File file : dir.listFiles())
		{
			final int index = file.getName().indexOf("_");
			if (file.getName().endsWith(".properties") && index != -1)
			{
				final String langCode = file.getName().substring(index + 1, file.getName().length() - 11);
				final LocalizedProperties langProps = new LocalizedProperties(properties.getPropertyMap());
				try
				{
					langProps.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));

					final File newFile = new File(dir.getAbsolutePath() + "/LocalizableResource_" + langCode
							+ ".properties");
					newFile.createNewFile();

					Log.info("----" + langCode + "----");

					final FileOutputStream fileOS = new FileOutputStream(newFile);
					final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOS, "UTF-8"));
					for (final Object key : langProps.getPropertyMap().keySet())
					{
						Log.info(key + "=" + langProps.getPropertyMap().get(key));
						writer.write(key + "=" + langProps.getPropertyMap().get(key) + "\n");
					}
					writer.flush();
					writer.close();
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
