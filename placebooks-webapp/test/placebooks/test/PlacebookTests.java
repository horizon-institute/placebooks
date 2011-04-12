package placebooks.test;

import java.net.URL;

import javax.jdo.PersistenceManager;

import org.junit.Test;

import placebooks.controller.PMFSingleton;
import placebooks.controller.UserManager;
import placebooks.model.PlaceBook;
import placebooks.model.PlaceBookItem;
import placebooks.model.TextItem;
import placebooks.model.User;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class PlacebookTests
{
	@Test
	public void newPlacebook() throws Exception
	{
		final PersistenceManager manager = PMFSingleton.getPersistenceManager();

		try
		{
			final User owner = UserManager.getUser(manager, "ktg@cs.nott.ac.uk");
			final Geometry geometry = new WKTReader().read("POINT(52.5189367988799 -4.04983520507812)");

			PlaceBook placebook = new PlaceBook(owner, geometry);

			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			placebook.addItem(new TextItem(owner, geometry, new URL("http://www.google.com"), "Test text string"));
			// placebook.addItem(new ImageItem(owner, geometry, new URL("http://www.blah.com"), new
			// BufferedImage(100, 100,
			// BufferedImage.TYPE_INT_BGR)));
			//
			// Document gpxDoc = null;
			//
			// // Some example XML
			// final String trace =
			// "<gpx version=\"1.0\" creator=\"PlaceBooks 1.0\" 				 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" 				 xmlns=\"http://www.topografix.com/GPX/1/1\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">			<time>			2011-02-14T13:31:10.084Z			</time>			<bounds minlat=\"52.950665120\" minlon=\"-1.183738050\" 					maxlat=\"52.950665120\" maxlon=\"-1.183738050\"/>			<trkseg>				<trkpt lat=\"52.950665120\" lon=\"-1.183738050\">				<ele>0.000000</ele>				<time>				2011-02-14T13:31:10.084Z				</time>				</trkpt>			</trkseg>			</gpx>";
			//
			// final StringReader reader = new StringReader(trace);
			// final InputSource source = new InputSource(reader);
			// final DocumentBuilder builder =
			// DocumentBuilderFactory.newInstance().newDocumentBuilder();
			// gpxDoc = builder.parse(source);
			// reader.close();
			//
			// placebook.addItem(new GPSTraceItem(owner, geometry, new URL("http://www.blah.com"),
			// gpxDoc));

			
			manager.currentTransaction().begin();
			placebook = manager.makePersistent(placebook);
			manager.currentTransaction().commit();

			assert owner.getPlacebooks().iterator().hasNext();
			
			manager.currentTransaction().begin();
			for (final PlaceBookItem item : placebook.getItems())
			{
				manager.deletePersistent(item);
			}
			manager.deletePersistent(placebook);

			manager.currentTransaction().commit();			
		}
		finally
		{
			if(manager.currentTransaction().isActive())
			{
				manager.currentTransaction().rollback();
			}
			
			manager.close();
		}

	}
}
