package placebooks.controller;

import java.util.Set;
import java.util.HashSet;

import java.io.StringReader;

import org.apache.log4j.Logger;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public final class SearchHelper
{
	protected static final Logger log = 
		Logger.getLogger(SearchHelper.class.getName());

	public static Set<String> getIndex(String input)
	{
		return getIndex(input, 0);
	}

	public static Set<String> getIndex(String input, int maxTokens) 
	{
		Set<String> returnSet = new HashSet<String>();
		try 
		{
			Analyzer analyzer = new EnglishAnalyzer(
				org.apache.lucene.util.Version.LUCENE_31);
			TokenStream tokenStream = analyzer.tokenStream("content", 
				new StringReader(input));
		    while (tokenStream.incrementToken()) 
			{
				if (maxTokens > 0 && returnSet.size() >= maxTokens)
					break;

				if (tokenStream.hasAttribute(TermAttribute.class)) 
				{
			        TermAttribute attr = 
						tokenStream.getAttribute(TermAttribute.class);
			        log.debug(attr.term());
			        returnSet.add(attr.term());
				}
    		}
		}
		catch (Exception e) 
		{
			log.error(e.toString());
		}
		
		return returnSet;
	}

}
