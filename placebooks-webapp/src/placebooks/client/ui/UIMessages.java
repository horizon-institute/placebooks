package placebooks.client.ui;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.safehtml.shared.SafeHtml;

@DefaultLocale("en")
@GenerateKeys("com.google.gwt.i18n.rebind.keygen.MD5KeyGenerator")
@Generate(format = {"com.google.gwt.i18n.rebind.format.PropertiesFormat"})
public interface UIMessages extends Messages
{
	@DefaultMessage("Linked Accounts")
	public String linkedAccounts();
	
	@DefaultMessage("Service")
	public String service();

	@DefaultMessage("Username")
	public String username();

	@DefaultMessage("Sync In Progress")
	public String syncInProgress();

	@DefaultMessage("Never Synced")
	public String neverSynced();

	@DefaultMessage("Last Synced: {0}")
	public String lastSynced(String formatDate);

	@DefaultMessage("Status")
	public String status();

	@DefaultMessage("Sync Now")
	public String syncNow();
	
	@DefaultMessage("Link {0} Account")
	public String linkAccount(String name);

	@DefaultMessage("Link Account")
	public String linkAccount();

	@DefaultMessage("{0} Login Failed")
	public String loginFailed(String service);

	@DefaultMessage("Create Account")
	public String createAccount();
	
	@DefaultMessage("Logging In...")
	public String loggingIn();

	@DefaultMessage("Locate {0} on Map")
	public String locateOnMap(String metadata);

	@DefaultMessage("Item is Not Currently on a Map")
	public String itemNotOnMap();

	@DefaultMessage("On Map on Page {0}")
	public String onMap(int i);

	@DefaultMessage("On {0} Map (page {1})")
	public String onMap(String title, int i);

	@DefaultMessage("Click on the Map to Place {0}")
	public String clickMapPlace(String metadata);
	
	@DefaultMessage("Click on the Map to Move {0}")
	public String clickMapMove(String metadata);

	@DefaultMessage("Owner")
	public String owner();

	@DefaultMessage("Read + Write")
	public String readwrite();

	@DefaultMessage("Read")
	public String read();

	@DefaultMessage("Edit Permissions")
	public String editPermissions();

	@DefaultMessage("Publish PlaceBook")
	public String publishPlaceBook();

	@DefaultMessage("No Title")
	public String noTitle();

	@DefaultMessage("Cannot publish while there are items which require uploading")
	public String uploadRequired();

	@DefaultMessage("Upload")
	public String upload();

	@DefaultMessage("Upload {0}")
	public String upload(String string);

	@DefaultMessage("Image")
	public String image();
	
	@DefaultMessage("Video")
	public String video();
	
	@DefaultMessage("Audio")
	public String audio();

	@DefaultMessage("Maximum {0} File Size: {1}Mb")
	public String maxSize(String type, int size);

	@DefaultMessage("Uploading File...")
	public String uploading();

	@DefaultMessage("Upload Failed")
	public String uploadFailed();

	@DefaultMessage("Saved")
	public String saved();

	@DefaultMessage("Save")
	public String save();

	@DefaultMessage("Saving")
	public String saving();

	@DefaultMessage("Error Saving")
	public String saveError();

	@DefaultMessage("Hide Map")
	public String mapHide();

	@DefaultMessage("Show Map")
	public String mapShow();

	@DefaultMessage("Error logging in")
	public String loginError();

	@DefaultMessage("Login not recognised. Check username and password.")
	public String loginFail();

	@DefaultMessage("Login")
	public String login();

	@DefaultMessage("Email")
	public String email();

	@DefaultMessage("Header")
	public String header();

	@DefaultMessage("Body Text")
	public String bodyText();

	@DefaultMessage("Bulleted Text")
	public String bulletedText();

	@DefaultMessage("Map")
	public String map();

	@DefaultMessage("Delete")
	public String delete();

	@DefaultMessage("Edit Location")
	public String editLocation();

	@DefaultMessage("Edit Title")
	public String editTitle();

	@DefaultMessage("Set Title")
	public SafeHtml setTitle();

	@DefaultMessage("Fit to Content")
	public String fitToContent();

	@DefaultMessage("Hide Trail")
	public String hideTrail();

	@DefaultMessage("Set URL")
	public String setURL();

	@DefaultMessage("Show Trail")
	public String showTrail();

	@DefaultMessage("The current PlaceBook has unsaved changes. Are you sure you want to leave?")
	public String unsavedChanges();

	@DefaultMessage("PlaceBooks Editor")
	public String placebooksEditor();

	@DefaultMessage("You will not be able to get your placebook back after deleting it. Are you sure?")
	public String confirmDeleteMessage();

	@DefaultMessage("Confirm Delete")
	public String confirmDelete();

	@DefaultMessage("PlaceBooks")
	public String placebooks();

	@DefaultMessage("Search PlaceBooks")
	public String searchPlaceBooks();

	@DefaultMessage("PlaceBooks Library")
	public String placebooksLibrary();

	@DefaultMessage("PlaceBooks Search")
	public String placebooksSearch();

	@DefaultMessage("Searching")
	public String searching();

	@DefaultMessage("Page {0}/{1}")
	public String page(int index, int size);

	@DefaultMessage("Create New PlaceBook")
	public String createNewPlaceBook();

	@DefaultMessage("Start editing a new placebook")
	public String createNewPlaceBookDesc();

	@DefaultMessage("No Description")
	public String noDesc();

	@DefaultMessage("Edit PlaceBook {0}")
	public String editPlaceBook(String title);

	@DefaultMessage("by {0}")
	public String by(String ownerName);

	@DefaultMessage("{0} miles")
	public String distance(String format);

	@DefaultMessage("View PlaceBook {0} (published)")
	public String viewPlaceBookPublished(String title);

	@DefaultMessage("Back")
	public String back();

	@DefaultMessage("Unnamed")
	public String unnamed();
}
