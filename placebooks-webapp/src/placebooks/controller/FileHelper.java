package placebooks.controller;

import java.io.File;
import java.io.FileOutputStream;
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


	public static String SaveFile(InputStream fileStream, String dir) throws IOException
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
		
		File tempFile = File.createTempFile("filehelper-", "");
		String tempSaveName = tempFile.getName();
		try
		{
		String tempSavedAs = FileHelper.SaveFile(input, dir, tempSaveName);
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
		boolean renamed = tempFile.renameTo(new File(saveName));
		if(!renamed)
		{
			log.warn("couldn't rename " + tempFile.getAbsolutePath() + " to " + saveName);
		}
		}
		finally
		{
			File tempCheck = new File(tempSaveName);
			if(tempCheck.exists())
			{
				log.debug("Deleting: " + tempCheck.getAbsolutePath());
				tempCheck.delete();
			}
		}
		return saveName;
	}
	
	public static String SaveFile(InputStream input, String dir, String saveName)
	{
		log.info("Saving new file as: " + saveName);

		String fullName = null;
		String savePath;
		try
		{
			savePath = GetSavePath(dir);
			final File filePath = new File(savePath + File.separator + saveName);
			if(!filePath.exists() || (filePath.length()==0))
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
		catch (IOException e)
		{
			log.error("Can't save file: " + e.getMessage());
		}
		return fullName;
	}
	
	public static boolean DeleteMediaItemFile(String fileName)
	{
		boolean fileDeleted = false;
		
		
		return fileDeleted;
	}
	
	/**
	 * Write a file to a path, with the given name and retirn the full path.
	 * @param path
	 * @param name
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public static String WriteFile(String path, String name, InputStream input) throws IOException
	{
		log.debug("Saving file " + name + " in path " + path);
		String saveName = null;
		String savePath = FileHelper.GetSavePath(path);
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
	
	
	/**
	 * Create folders and return full system path for saving a file
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static String GetSavePath(String dir) throws IOException
	{
		final File savePath = new File(dir).getAbsoluteFile();
		if (!savePath.exists() && !savePath.mkdirs()) { throw new IOException("Failed to create folder: " + dir); }
		if (!savePath.isDirectory()) { throw new IOException("Save Path not a directory"); }
		return savePath.getAbsolutePath();
	}
}

