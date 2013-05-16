package placebooks.model;

import java.util.Set;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public abstract class BoundaryGenerator
{
	protected static final Logger log = Logger.getLogger(BoundaryGenerator.class.getName());

	public abstract void calcBoundary();

	protected final Geometry calcBoundary(final Set<Geometry> geoms)
	{
		Geometry geom = null;

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
					final Coordinate[] cs = g.getCoordinates();
					for (final Coordinate c : cs)
					{
						minLat = Math.min(minLat, (float) c.x);
						maxLat = Math.max(maxLat, (float) c.x);
						minLon = Math.min(minLon, (float) c.y);
						maxLon = Math.max(maxLon, (float) c.y);
						emptySet = true;
					}
				}
				else
				{
					if (bounds != null)
					{
						bounds = g.union(bounds);
					}
					else
					{
						bounds = g;
					}
				}
			}
		}

		if (emptySet)
		{
			if (minLat == Float.POSITIVE_INFINITY || maxLat == Float.NEGATIVE_INFINITY
					|| minLon == Float.POSITIVE_INFINITY || maxLon == Float.NEGATIVE_INFINITY)
			{
				log.error("Warning: empty bounds to calculate were not valid, ignoring");
			}
			else
			{
				try
				{
					final Geometry empty = new WKTReader().read("POLYGON ((" + minLat + " " + minLon + ", " + minLat
							+ " " + maxLon + ", " + maxLat + " " + maxLon + ", " + maxLat + " " + minLon + ", "
							+ minLat + " " + minLon + "))");
					log.info("empty=" + empty);
					if (bounds != null)
					{
						bounds = empty.union(bounds);
					}
					else
					{
						bounds = empty;
					}
				}
				catch (final Throwable e)
				{
					log.error(e.toString());
				}
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
