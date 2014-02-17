package org.placebooks.controller;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wornchaos.logger.Log;

public final class SearchHelper
{
	public static Set<String> getIndex(final String input)
	{
		return getIndex(input, 0);
	}

	public static Set<String> getIndex(final String input, final int maxTokens)
	{
		final Set<String> returnSet = new HashSet<String>();
		final Analyzer analyzer = new EnglishAnalyzer(org.apache.lucene.util.Version.LUCENE_31);		
		try
		{
			final TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(input));
			final StringBuilder sb = new StringBuilder();
			while (tokenStream.incrementToken())
			{
				if (maxTokens > 0 && returnSet.size() >= maxTokens)
				{
					break;
				}

				if (tokenStream.hasAttribute(CharTermAttribute.class))
				{
					final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
					sb.append("\"");
					sb.append(attr.toString());
					sb.append("\" ");
					returnSet.add(attr.toString());
				}
			}
			Log.debug("getIndex() terms: " + sb.toString());
		}
		catch (final Exception e)
		{
			Log.error(e);
		}
		finally
		{
			analyzer.close();
		}

		return returnSet;
	}
}