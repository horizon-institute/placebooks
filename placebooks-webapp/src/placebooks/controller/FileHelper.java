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


	public static String SaveFile(InputStream fileStream, String dir)
	{
		log.info("Attemtping to save a file: generting hash");
		String hash = "";
		
		MessageDigest md = null;
		InputStream input = null;
		try
		{
			md = MessageDigest.getInstance("MD5");
			input = new DigestInputStream(fileStream, md);
		}
		catch (final Exception e)
		{
			input = fileStream;
		}

		if (md != null)
		{
			hash = String.format("%032x", new BigInteger(1, md.digest()));
			log.debug("File hash  is:" + hash);
		}
		else
		{
			hash = Long.toString(System.currentTimeMillis());
			log.error("Unable to hash file, using time: " + hash);
		}
		
		return SaveFile(input, dir, hash);
	}
	
	
	public static String SaveFile(InputStream input, String dir, String hash)
	{
		
		String saveName = hash;
		log.info("Saving new file as: " + saveName);

		String fullName = null;
		String savePath;
		try
		{
			savePath = GetSavePath(dir);
			final File filePath = new File(savePath + File.separator + saveName);
			final OutputStream output = new FileOutputStream(filePath);
			int byte_;
			while ((byte_ = input.read()) != -1)
			{
				output.write(byte_);
			}
			output.close();

			log.info("Wrote " + saveName + " file " + filePath.getAbsolutePath());
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

