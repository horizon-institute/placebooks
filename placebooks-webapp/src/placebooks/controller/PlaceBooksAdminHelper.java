package placebooks.controller;

import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookSearchIndex;
import placebooks.model.PlaceBookItemSearchIndex;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;

public final class PlaceBooksAdminHelper
{

	public static final Set<Map.Entry<PlaceBook, Integer>> search(String terms)
	{

		final Set<String> search = SearchHelper.getIndex(terms, 5);

		final EntityManager pm = EMFSingleton.getEntityManager();

		final TypedQuery<PlaceBookSearchIndex> query1 = 
			pm.createQuery("SELECT p FROM PlaceBookSearchIndex p",
						   PlaceBookSearchIndex.class);
		final List<PlaceBookSearchIndex> pbIndexes = query1.getResultList();

		// Search rationale: ratings are accumulated per PlaceBook for that
		// PlaceBook plus any PlaceBookItems
		final Map<PlaceBook, Integer> hits = new HashMap<PlaceBook, Integer>();

		for (final PlaceBookSearchIndex index : pbIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			Integer rating = hits.get(index.getPlaceBook());
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(index.getPlaceBook(), 
					 new Integer(keywords.size() + rating.intValue()));
		}

		final TypedQuery<PlaceBookItemSearchIndex> query2 = 
			pm.createQuery("SELECT p FROM PlaceBookItemSearchIndex p",
						   PlaceBookItemSearchIndex.class);
		final List<PlaceBookItemSearchIndex> pbiIndexes = 
			query2.getResultList();

		for (final PlaceBookItemSearchIndex index : pbiIndexes)
		{
			final Set<String> keywords = new HashSet<String>();
			keywords.addAll(index.getIndex());
			keywords.retainAll(search);
			final PlaceBook p = index.getPlaceBookItem().getPlaceBook();
			Integer rating = hits.get(p);
			if (rating == null)
			{
				rating = new Integer(0);
			}
			hits.put(p, new Integer(keywords.size() + rating.intValue()));
		}

		pm.close();

		return hits.entrySet();
	}

}
