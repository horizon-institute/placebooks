package org.placebooks.server;

import java.io.File;
import java.io.InputStream;

import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.model.PlaceBookService;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Prefix;
import org.wornchaos.client.server.Request;
import org.wornchaos.client.server.Request.Method;
import org.wornchaos.client.server.RequestParam;

@Prefix("command")
public interface PlaceBookServerFull extends PlaceBookService
{
	@Request("media")
	public void getMedia(@RequestParam("type") final String type, @RequestParam("hash") final String hash,
			final AsyncCallback<File> callback);

	@Request("qrcode")
	public void qrcode(@RequestParam("type") final String type, @RequestParam("id") final String key,
			final AsyncCallback<InputStream> callback);

	@Request(value = "upload_item", method = Method.FILE)
	public void uploadFile(@RequestParam("type") final String type, @RequestParam("id") final String id,
			final InputStream file, final AsyncCallback<String> callback);

	@Request("oauth")
	public void oauth(@RequestParam("service") final String service, final AsyncCallback<String> callback);

	
	@Request(value = "upload_package", method = Method.FILE)
	public void uploadPackage(final InputStream file, final AsyncCallback<PlaceBook> callback);
}