package placebooks.controller;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class SearchHelper
{
	protected static final Logger log = Logger.getLogger(SearchHelper.class.getName());

	public static Set<String> getIndex(final String input)
	{
		return getIndex(input, 0);
	}

	public static Set<String> getIndex(final String input, final int maxTokens)
	{
		final Set<String> returnSet = new HashSet<String>();
		try
		{
			final Analyzer analyzer = new EnglishAnalyzer(org.apache.lucene.util.Version.LUCENE_31);
			final TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(input));
			StringBuilder sb = new StringBuilder();
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
			log.debug("getIndex() terms: " + sb.toString());
		}
		catch (final Exception e)
		{
			log.error(e.toString());
		}

		return returnSet;
	}

}
