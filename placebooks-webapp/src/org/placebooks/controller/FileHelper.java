package org.placebooks.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

public class FileHelper
{

	protected static final Logger log = Logger.getLogger(FileHelper.class.getName());

	public static boolean DeleteMediaItemFile(final String fileName)
	{
		final boolean fileDeleted = false;

		return fileDeleted;
	}

	/**
	 * Attempt to find file in given dir, uses the first file found starting with the name if it's
	 * not.
	 * 
	 * @param dir
	 * @param name
	 * @return
	 */
	public static String FindClosestFile(final String dir, final String name)
	{
		String itemPath = null;
		final File serveFile = new File(dir + File.separator + name);
		if (serveFile.exists())
		{
			itemPath = serveFile.getAbsolutePath();
		}
		else
		{
			final File path = new File(dir);
			File[] files = null;
			files = path.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(final File dir, final String name)
				{
					final int index = name.indexOf('.');
					if (name.startsWith(name) && index == name.length())
					{
						log.debug("Found: " + name);
						return true;
					}
					return false;
				}
			});
			if (files != null && files.length > 0)
			{
				itemPath = files[0].getPath();
			}
		}
		return itemPath;
	}

	/**
	 * Create folders and return full system path for saving a file
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static String GetSavePath(final String dir) throws IOException
	{
		final File savePath = new File(dir).getAbsoluteFile();
		if (!savePath.exists() && !savePath.mkdirs()) { throw new IOException("Failed to create folder: " + dir); }
		if (!savePath.isDirectory()) { throw new IOException("Save Path not a directory"); }
		return savePath.getAbsolutePath();
	}

	public static String SaveFile(final InputStream fileStream, final String dir) throws IOException
	{
		log.info("Writing file from: " + fileStream);
		String hash;
		String saveName = null;

		InputStream input;
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			input = new DigestInputStream(fileStream, md);
		}
		catch (final Exception e)
		{
			input = fileStream;
		}

		final File tempFile = File.createTempFile("filehelper-", "");
		final String tempSaveName = tempFile.getName();
		try
		{
			final String tempSavedAs = FileHelper.SaveFile(input, dir, tempSaveName);
			log.debug("Created temporary file: " + tempSavedAs);

			if (md != null)
			{
				hash = String.format("%032x", new BigInteger(1, md.digest()));
				log.debug("File hash is: " + hash);
			}
			else
			{
				hash = Long.toString(System.currentTimeMillis());
				log.error("Couldn't create a hash for file  using time instead: " + hash);
			}

			saveName = dir + File.separator + hash;
			log.info("Saving new file as: " + saveName);
			final boolean renamed = tempFile.renameTo(new File(saveName));
			if (!renamed)
			{
				log.warn("couldn't rename " + tempFile.getAbsolutePath() + " to " + saveName);
			}
		}
		finally
		{
			log.debug("Trying to delete " + tempSaveName);
			final File tempCheck = new File(tempSaveName);
			if (tempCheck.exists())
			{
				log.debug("Deleting: " + tempCheck.getAbsolutePath());
				tempCheck.delete();
			}
		}
		return saveName;
	}

	/**
	 * Save a file from an input stream to a given directory name with a given name in the
	 * configured
	 * 
	 * @param input
	 * @param dir
	 * @param saveName
	 * @return String Full path + name of the saved file
	 */
	public static String SaveFile(final InputStream input, final String dir, final String saveName)
	{
		log.info("Saving new file as: " + saveName);

		String fullName = null;
		String savePath;
		try
		{
			savePath = GetSavePath(dir);
			final File filePath = new File(savePath + File.separator + saveName);
			if (!filePath.exists() || (filePath.length() == 0))
			{
				final OutputStream output = new FileOutputStream(filePath);
				int byte_;
				while ((byte_ = input.read()) != -1)
				{
					output.write(byte_);
				}
				output.close();
				log.info("Wrote " + saveName + " file " + filePath.getAbsolutePath());
			}
			else
			{
				log.info("No need to write, file exists: " + filePath.getAbsolutePath());
			}
			fullName = filePath.getAbsolutePath();
		}
		catch (final IOException e)
		{
			log.error("Can't save file: " + e.getMessage());
		}
		return fullName;
	}

	/**
	 * Write a file to a path, with the given name and retirn the full path.
	 * 
	 * @param path
	 * @param name
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String WriteFile(final String path, final String name, final InputStream input) throws IOException
	{
		log.debug("Saving file " + name + " in path " + path);
		String saveName = null;
		final String savePath = FileHelper.GetSavePath(path);
		final File filePath = new File(savePath + File.separator + name);
		final OutputStream output = new FileOutputStream(filePath);
		int byte_;
		while ((byte_ = input.read()) != -1)
		{
			output.write(byte_);
		}
		output.close();

		saveName = filePath.getAbsolutePath();
		log.info("Wrote " + name + " file " + saveName);
		return saveName;
	}

}