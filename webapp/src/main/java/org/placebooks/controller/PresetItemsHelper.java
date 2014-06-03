package org.placebooks.controller;

import java.util.ArrayList;

import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.User;

import com.google.gson.Gson;

/**
 * Class to getnerate precofigured items for the everyTrail palate
 * 
 * @author pszmp
 * 
 */
public class PresetItemsHelper
{
	// private static final Logger log = Logger.getLogger(CommunicationHelper.class.getName());

	private static final String MAGIC_SEAWEED_ITEM = "{\"@class\":\"org.model.WebBundleItem\",\"sourceURL\":\"http://magicseaweed.com/Borth-Ynyslas--Surf-Report/84/\",\"metadata\":{\"title\":\"Borth / Ynyslas Surf Report and Forecast\",\"source\":\"Live Data\"},\"parameters\":{\"height\":10000}}";
	private static final String BBC_WEATHER_ITEM = "{\"@class\":\"org.model.WebBundleItem\",\"sourceURL\":\"http://news.bbc.co.uk/weather/forecast/20/ObservationsEmbed.xhtml?target=_parent\",\"metadata\":{\"title\":\"BBC Weather Observations, Aberystwyth\",\"source\":\"Live Data\"},\"parameters\":{\"height\":6903}}";
	private static final String METCHECK_ITEM = "{\"@class\":\"org.model.TextItem\",\"metadata\":{\"title\":\"metcheck Forcast for Borth\",\"source\":\"Live Data\"},\"parameters\":{\"height\":4964},\"text\":\"<a href='http://www.metcheck.com/' target='_blank'><img src='http://www.metcheck.com/REMOTE/CLIENTS/UK/METCHECK/STICKIES/GEN_STICKY.ASP?LOCATIONID=4936&CO=UK' border='0' title='Latest Weather Forecast from www.metcheck.com - Click for full forecast'></a>\"}";

	protected static final Gson gson = new Gson();

	public static ArrayList<org.placebooks.model.PlaceBookItem> getPresetItems(final User user)
	{
		final ArrayList<PlaceBookItem> items = new ArrayList<PlaceBookItem>();
		PresetItemsHelper.deserialiseAndAdd(MAGIC_SEAWEED_ITEM, items);
		PresetItemsHelper.deserialiseAndAdd(METCHECK_ITEM, items);
		PresetItemsHelper.deserialiseAndAdd(BBC_WEATHER_ITEM, items);
		return items;
	}

	protected static void deserialiseAndAdd(final String json, final ArrayList<PlaceBookItem> items)
	{
		/*
		 * mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT
		 * ); try { PlaceBookItem item = mapper.readValue(json, PlaceBookItem.class);
		 * log.debug("Adding Preset Item: " + item.getMetadataValue("title")); items.add(item); }
		 * catch(Exception ex) { log.error("Couldn't deserialise preset item: " + json, ex); }
		 */
	}

}
