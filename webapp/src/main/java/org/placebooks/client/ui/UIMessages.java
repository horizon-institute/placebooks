package org.placebooks.client.ui;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

@DefaultLocale("en")
@GenerateKeys("com.google.gwt.i18n.rebind.keygen.MD5KeyGenerator")
@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" })
public interface UIMessages extends Messages
{
	@DefaultMessage("Audio")
	public String audio();

	@DefaultMessage("Back")
	public String back();

	@DefaultMessage("Body Text")
	public String bodyText();

	@DefaultMessage("Bulleted Text")
	public String bulletedText();

	@DefaultMessage("by {0}")
	public String by(String ownerName);

	@DefaultMessage("Click on the Map to Move {0}")
	public String clickMapMove(String metadata);

	@DefaultMessage("Click on the Map to Place {0}")
	public String clickMapPlace(String metadata);

	@DefaultMessage("Confirm Delete")
	public String confirmDelete();

	@DefaultMessage("You will not be able to get your placebook back after deleting it. Are you sure?")
	public String confirmDeleteMessage();

	@DefaultMessage("Create Account")
	public String createAccount();

	@DefaultMessage("Create New PlaceBook")
	public String createNewPlaceBook();

	@DefaultMessage("Start editing a new placebook")
	public String createNewPlaceBookDesc();

	@DefaultMessage("Delete")
	public String delete();

	@DefaultMessage("{0} miles")
	public String distance(String format);

	@DefaultMessage("Edit Location")
	public String editLocation();

	@DefaultMessage("Edit Permissions")
	public String editPermissions();

	@DefaultMessage("Edit PlaceBook {0}")
	public String editPlaceBook(String title);

	@DefaultMessage("Edit Title")
	public String editTitle();

	@DefaultMessage("Email")
	public String email();

	@DefaultMessage("Fit to Content")
	public String fitToContent();

	@DefaultMessage("Header")
	public String header();

	@DefaultMessage("Hide Trail")
	public String hideTrail();

	@DefaultMessage("Image")
	public String image();

	@DefaultMessage("Item is Not Currently on a Map")
	public String itemNotOnMap();

	@DefaultMessage("Last Synced: {0}")
	public String lastSynced(String formatDate);

	@DefaultMessage("Link Account")
	public String linkAccount();

	@DefaultMessage("Link {0} Account")
	public String linkAccount(String name);

	@DefaultMessage("Linked Accounts")
	public String linkedAccounts();

	@DefaultMessage("Locate {0} on Map")
	public String locateOnMap(String metadata);

	@DefaultMessage("Logging In...")
	public String loggingIn();

	@DefaultMessage("Login")
	public String login();

	@DefaultMessage("Error logging in")
	public String loginError();

	@DefaultMessage("Login not recognised. Check username and password.")
	public String loginFail();

	@DefaultMessage("{0} Login Failed")
	public String loginFailed(String service);

	@DefaultMessage("Map")
	public String map();

	@DefaultMessage("Hide Map")
	public String mapHide();

	@DefaultMessage("Show Map")
	public String mapShow();

	@DefaultMessage("Maximum {0} File Size: {1}Mb")
	public String maxSize(String type, int size);

	@DefaultMessage("Never Synced")
	public String neverSynced();

	@DefaultMessage("No Description")
	public String noDesc();

	@DefaultMessage("No Title")
	public String noTitle();

	@DefaultMessage("On Map on Page {0}")
	public String onMap(int i);

	@DefaultMessage("On {0} Map (page {1})")
	public String onMap(String title, int i);

	@DefaultMessage("Owner")
	public String owner();

	@DefaultMessage("Page {0}/{1}")
	public String page(int index, int size);

	@DefaultMessage("PlaceBooks")
	public String placebooks();

	@DefaultMessage("PlaceBooks Editor")
	public String placebooksEditor();

	@DefaultMessage("PlaceBooks Library")
	public String placebooksLibrary();

	@DefaultMessage("PlaceBooks Search")
	public String placebooksSearch();

	@DefaultMessage("Publish PlaceBook")
	public String publishPlaceBook();

	@DefaultMessage("Read")
	public String read();

	@DefaultMessage("Read + Write")
	public String readwrite();

	@DefaultMessage("Save")
	public String save();

	@DefaultMessage("Saved")
	public String saved();

	@DefaultMessage("Error Saving")
	public String saveError();

	@DefaultMessage("Saving")
	public String saving();

	@DefaultMessage("Searching")
	public String searching();

	@DefaultMessage("Search PlaceBooks")
	public String searchPlaceBooks();

	@DefaultMessage("Service")
	public String service();

	@DefaultMessage("Set Title")
	public SafeHtml setTitle();

	@DefaultMessage("Set URL")
	public String setURL();

	@DefaultMessage("Show Trail")
	public String showTrail();

	@DefaultMessage("Status")
	public String status();

	@DefaultMessage("Sync In Progress")
	public String syncInProgress();

	@DefaultMessage("Sync Now")
	public String syncNow();

	@DefaultMessage("Unnamed")
	public String unnamed();

	@DefaultMessage("The current PlaceBook has unsaved changes. Are you sure you want to leave?")
	public String unsavedChanges();

	@DefaultMessage("Upload")
	public String upload();

	@DefaultMessage("Upload {0}")
	public String upload(String string);

	@DefaultMessage("Upload Failed")
	public String uploadFailed();

	@DefaultMessage("Uploading File...")
	public String uploading();

	@DefaultMessage("Cannot publish while there are items which require uploading")
	public String uploadRequired();

	@DefaultMessage("Username")
	public String username();

	@DefaultMessage("Video")
	public String video();

	@DefaultMessage("View PlaceBook {0} (published)")
	public String viewPlaceBookPublished(String title);
}
