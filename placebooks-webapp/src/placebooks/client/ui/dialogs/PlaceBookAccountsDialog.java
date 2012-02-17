package placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.DataStore;
import placebooks.client.model.LoginDetails;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.ui.elements.DatePrinter;
import placebooks.client.ui.elements.EnabledButtonCell;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

public class PlaceBookAccountsDialog extends PlaceBookDialog
{
	interface PlaceBookAccountsDialogUiBinder extends UiBinder<Widget, PlaceBookAccountsDialog>
	{
	}

	private static PlaceBookAccountsDialogUiBinder uiBinder = GWT.create(PlaceBookAccountsDialogUiBinder.class);

	private static final String[] SERVICES = { "Everytrail", "PeoplesCollection" };

	private static final ProvidesKey<LoginDetails> keyProvider = new ProvidesKey<LoginDetails>()
	{
		@Override
		public Object getKey(final LoginDetails details)
		{
			return details.getID();
		}
	};

	@UiField
	Panel tablePanel;

	@UiField
	Panel buttonPanel;

	private CellTable<LoginDetails> cellTable;
	private User user;
	
	private final DataStore<User> userStore = new DataStore<User>()
	{
		@Override
		protected String getRequestURL(final String id)
		{
			return getHostURL() + "placebooks/a/currentUser";
		}

		@Override
		protected String getStorageID(final String id)
		{
			return "current.user";
		}
	};

	public PlaceBookAccountsDialog(final User user)
	{
		this.user = user;
		setWidget(uiBinder.createAndBindUi(this));
		onInitialize();
		center();
		updateUser();
	}

	/**
	 * Add the columns to the table.
	 */
	private void initTableColumns(final SelectionModel<LoginDetails> selectionModel)
	{
		// Service
		final Column<LoginDetails, String> serviceColumn = new Column<LoginDetails, String>(new TextCell())
		{
			@Override
			public String getValue(final LoginDetails object)
			{
				return object.getService();
			}
		};
		cellTable.addColumn(serviceColumn, "Service");

		// Username.
		final Column<LoginDetails, String> userNameColumn = new Column<LoginDetails, String>(new TextCell())
		{
			@Override
			public String getValue(final LoginDetails object)
			{
				return object.getUsername();
			}
		};
		cellTable.addColumn(userNameColumn, "Username");

		// Last Update
		final Column<LoginDetails, String> lastUpdateColumn = new Column<LoginDetails, String>(new TextCell())
		{
			@Override
			public String getValue(final LoginDetails object)
			{
				if (object.isSyncInProgress())
				{
					return "Sync In Progress";
				}
				else if (object.getLastSync() == 0)
				{
					return "Never Synced";
				}
				else
				{
					return "Last Synced: " + DatePrinter.formatDate(new Date((long) object.getLastSync()));
				}
			}
		};
		cellTable.addColumn(lastUpdateColumn, "Status");

		final EnabledButtonCell<LoginDetails> syncCell = new EnabledButtonCell<LoginDetails>()
		{

			@Override
			public String getText(final LoginDetails object)
			{
				return "Sync Now";
			}

			@Override
			public boolean isEnabled(final LoginDetails object)
			{
				return !object.isSyncInProgress();
			}
		};

		final Column<LoginDetails, LoginDetails> updateColumn = new Column<LoginDetails, LoginDetails>(syncCell)
		{
			@Override
			public LoginDetails getValue(final LoginDetails details)
			{
				return details;
			}
		};
		updateColumn.setFieldUpdater(new FieldUpdater<LoginDetails, LoginDetails>()
		{
			@Override
			public void update(final int index, final LoginDetails object, final LoginDetails value)
			{
				PlaceBookService.sync(object.getService(), new AbstractCallback()
				{
					@Override
					public void success(final Request request, final Response response)
					{
					}
				});
				object.setSyncInProgress(true);
				setUser(user);
			}
		});
		cellTable.addColumn(updateColumn);
	}

	private void onInitialize()
	{
		cellTable = new CellTable<LoginDetails>(keyProvider);
		cellTable.setWidth("100%", false);

		final SelectionModel<LoginDetails> selectionModel = new NoSelectionModel<LoginDetails>(keyProvider);
		cellTable.setSelectionModel(selectionModel);

		initTableColumns(selectionModel);

		tablePanel.add(cellTable);

		setUser(user);
	}

	private void setUser(final User user)
	{
		this.user = user;

		final List<String> serviceList = new ArrayList<String>();
		for (final String service : SERVICES)
		{
			serviceList.add(service);
		}

		boolean syncing = false;
		final List<LoginDetails> list = new ArrayList<LoginDetails>();
		for (final LoginDetails details : user.getLoginDetails())
		{
			if (details.isSyncInProgress())
			{
				syncing = true;
			}
			serviceList.remove(details.getService());
			list.add(details);
		}

		if (syncing)
		{
			new Timer()
			{
				@Override
				public void run()
				{
					updateUser();
				}
			}.schedule(1000);
		}

		buttonPanel.clear();
		cellTable.setRowData(list);

		for (final String service : serviceList)
		{
			buttonPanel.add(new Button("Link " + service + " Account", new ClickHandler()
			{

				@Override
				public void onClick(final ClickEvent arg0)
				{
					final PlaceBookLoginDialog account = new PlaceBookLoginDialog("Link " + service + " Account",
							"Link Account", service + " Username:");
					account.addClickHandler(new ClickHandler()
					{

						@Override
						public void onClick(final ClickEvent event)
						{
							account.setProgress(true);
							PlaceBookService.linkAccount(	account.getUsername(), account.getPassword(), service,
															new JSONResponse<Shelf>()
															{

																@Override
																public void handleError(final Request request,
																		final Response response,
																		final Throwable throwable)
																{
																	account.setError(service + " Login Failed");
																	account.setProgress(false);
																}

																@Override
																public void handleResponse(final Shelf shelf)
																{
																	account.hide();
																	setUser(shelf.getUser());
																}
															});
						}
					});

					account.show();
				}
			}));
		}

		center();
	}

	private void updateUser()
	{
		if (!isShowing()) { return; }
		userStore.get(null, new JSONResponse<User>()
		{	
			@Override
			public void handleResponse(User object)
			{
				setUser(object);
			}
		}, true);
	}
}
