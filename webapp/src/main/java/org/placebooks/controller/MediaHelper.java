package org.placebooks.controller;

import org.wornchaos.logger.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

// Adapted / stolen from http://balusc.blogspot.com/2009/02/fileservlet-supporting-resume-and.html
public class MediaHelper
{

	public static class Range
	{
		long start;
		long end;
		long length;
		long total;

		public Range(final long start, final long end, final long total)
		{
			this.start = start;
			this.end = end;
			length = end - start + 1;
			this.total = total;
		}
	}

	public static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.
	public static final long DEFAULT_EXPIRE_TIME = 31556952000l; // ..ms = 1 year.

	public static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

	public static final boolean accepts(final String acceptHeader, final String toAccept)
	{
		final String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
		Arrays.sort(acceptValues);
		return Arrays.binarySearch(acceptValues, toAccept) > -1
				|| Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
				|| Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	public static void close(final Closeable resource)
	{
		if (resource != null)
		{
			try
			{
				resource.close();
			}
			catch (final IOException ignore)
			{
				Log.warn("Client aborted request");
			}
		}
	}

	public static void copy(final RandomAccessFile input, final OutputStream output, final long start, final long length)
			throws IOException
	{
		final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int read;

		if (input.length() == length)
		{
			while ((read = input.read(buffer)) > 0)
			{
				output.write(buffer, 0, read);
			}
		}
		else
		{
			input.seek(start);
			long toRead = length;

			while ((read = input.read(buffer)) > 0)
			{
				if ((toRead -= read) > 0)
				{
					output.write(buffer, 0, read);
				}
				else
				{
					output.write(buffer, 0, (int) toRead + read);
					break;
				}
			}
		}
	}

	public static final boolean matches(final String matchHeader, final String toMatch)
	{
		final String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

	public static final long sublong(final String value, final int beginIndex, final int endIndex)
	{
		final String substring = value.substring(beginIndex, endIndex);
		return (substring.length() > 0) ? Long.parseLong(substring) : -1;
	}

}
