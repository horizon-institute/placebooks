package placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookBinder;
import placebooks.client.ui.elements.EnabledButtonCell;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

public class PlaceBookPermissionsDialog extends PlaceBookDialog
{
	interface PlaceBookPermissionsDialogUiBinder extends UiBinder<Widget, PlaceBookPermissionsDialog>
	{
	}

	private class Permission
	{
		private String email;
		private String permission;
	}

	private static PlaceBookPermissionsDialogUiBinder uiBinder = GWT.create(PlaceBookPermissionsDialogUiBinder.class);

	private static final ProvidesKey<Permission> keyProvider = new ProvidesKey<Permission>()
	{
		@Override
		public Object getKey(final Permission details)
		{
			return details.email;
		}
	};

	@UiField
	Panel tablePanel;

	private final PlaceBookController controller;

	private final CellTable<Permission> cellTable;
	private final PlaceBookBinder placebook;

	public PlaceBookPermissionsDialog(final PlaceBookController controller, final PlaceBookBinder placebook)
	{
		this.placebook = placebook;
		this.controller = controller;
		setWidget(uiBinder.createAndBindUi(this));
		setTitle("Edit Permissions");

		cellTable = new CellTable<Permission>(keyProvider);
		cellTable.setWidth("100%", false);

		final SelectionModel<Permission> selectionModel = new NoSelectionModel<Permission>(keyProvider);
		cellTable.setSelectionModel(selectionModel);

		initTableColumns(selectionModel);

		tablePanel.add(cellTable);

		updatePermissions();

		center();
	}

	@UiHandler("addUser")
	void handleAddUser(final ClickEvent event)
	{

	}

	/**
	 * Add the columns to the table.
	 */
	private void initTableColumns(final SelectionModel<Permission> selectionModel)
	{
		// Username
		final Column<Permission, String> serviceColumn = new Column<Permission, String>(new TextCell())
		{
			@Override
			public String getValue(final Permission object)
			{
				return object.email;
			}
		};
		cellTable.addColumn(serviceColumn, "User");

		// Permissions
		final Column<Permission, String> userNameColumn = new Column<Permission, String>(new TextCell())
		{
			@Override
			public String getValue(final Permission object)
			{
				return object.permission;
			}
		};
		cellTable.addColumn(userNameColumn, "Permissions");

		// Remove
		final EnabledButtonCell<Permission> removeCell = new EnabledButtonCell<Permission>()
		{
			@Override
			public String getText(final Permission object)
			{
				return "Remove";
			}

			@Override
			public boolean isEnabled(final Permission object)
			{
				return !object.email.equals(placebook.getOwner().getEmail());
			}
		};

		final Column<Permission, Permission> removeColumn = new Column<Permission, Permission>(removeCell)
		{
			@Override
			public Permission getValue(final Permission details)
			{
				return details;
			}
		};
		removeColumn.setFieldUpdater(new FieldUpdater<Permission, Permission>()
		{
			@Override
			public void update(final int index, final Permission object, final Permission value)
			{
				placebook.getPermissions().put(object.email, null);
				controller.markChanged();
				updatePermissions();
			}
		});
		cellTable.addColumn(removeColumn);
	}

	private void updatePermissions()
	{
		final List<Permission> permissions = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.email = placebook.getOwner().getEmail();
		permission.permission = "Owner";
		permissions.add(permission);

		for (final String user : placebook.getPermissions().keySet())
		{
			if (user.equals(placebook.getOwner().getEmail()))
			{
				continue;
			}

			permission = new Permission();
			permission.email = user;
			permission.permission = placebook.getPermissions().get(user).toString();
			permissions.add(permission);
		}

		placebook.getPermissions().put(placebook.getOwner().getEmail(), new JSONString("R_W"));

		cellTable.setRowData(permissions);
	}
}