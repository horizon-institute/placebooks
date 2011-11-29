package placebooks.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.geom.Coordinate;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public abstract class BoundaryGenerator
{
	protected static final Logger log = 
		Logger.getLogger(BoundaryGenerator.class.getName());


	public abstract void calcBoundary();

	protected final Geometry calcBoundary(final Set<Geometry> geoms)
	{

		Geometry bounds = null;
		float minLat = Float.POSITIVE_INFINITY;
		float maxLat = Float.NEGATIVE_INFINITY;
		float minLon = Float.POSITIVE_INFINITY;
		float maxLon = Float.NEGATIVE_INFINITY;
		boolean emptySet = false;

		for (final Geometry g : geoms)
		{
			if (g != null)
			{
				// A Geometry with no dimensions has to be handled
				if (g.getBoundary().isEmpty()) 
				{
					Coordinate[] cs = g.getCoordinates();
					for (Coordinate c : cs)
					{
						minLat = Math.min(minLat, (float)c.x);
						maxLat = Math.max(maxLat, (float)c.x);
						minLon = Math.min(minLon, (float)c.y);
						maxLon = Math.max(maxLon, (float)c.y);
						emptySet = true;
					}
				}
				else
				{
					if (bounds != null)
						bounds = g.union(bounds);
					else
						bounds = g;
				}
			}
		}

		if (emptySet)
		{
			try
			{
				Geometry empty = new WKTReader().read(
								"POLYGON ((" + minLat + " " + minLon + ", "
											 + minLat + " " + maxLon + ", "
											 + maxLat + " " + maxLon + ", "
											 + maxLat + " " + minLon + ", "
											 + minLat + " " + minLon + "))");
				log.info("empty=" + empty);
				if (bounds != null)
					bounds = empty.union(bounds);
				else
					bounds = empty;
			}
			catch (final Throwable e)
			{
				log.error(e.toString());
			}

		}

		if (bounds != null)
		{
			geom = bounds.getBoundary();
		}
		else
		{
			geom = null;
		}

		return geom;
	}
}
