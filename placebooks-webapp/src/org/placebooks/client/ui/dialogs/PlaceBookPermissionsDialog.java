package org.placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.client.controllers.PlaceBookController;
import org.placebooks.client.ui.UIMessages;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class PlaceBookPermissionsDialog extends PlaceBookDialog
{
	interface PlaceBookPermissionsDialogUiBinder extends UiBinder<Widget, PlaceBookPermissionsDialog>
	{
	}

	private static class Permission
	{
		private String email;
		private String permission;
	}

	/**
	 * The Cell used to render a {@link ContactInfo}.
	 */
	private class PermissionsCell extends AbstractCell<Permission>
	{
		@Override
		public void render(final Context context, final Permission value, final SafeHtmlBuilder sb)
		{
			// Value can be null, so do a null check..
			if (value == null) { return; }

			sb.appendHtmlConstant("<div>");

			// Add the contact markerImage.
			sb.appendHtmlConstant("<div>");
			sb.appendHtmlConstant(value.email);
			sb.appendHtmlConstant("</div>");

			// Add the name and address.
			sb.appendHtmlConstant("<div>");
			if (value.email.equals(controller.getItem().getOwner().getEmail()))
			{
				sb.appendEscaped(uiMessages.owner());
			}
			else if (value.permission.equals("R_W"))
			{
				sb.appendEscaped(uiMessages.readwrite());
			}
			else
			{
				sb.appendEscaped(uiMessages.read());
			}

			sb.appendHtmlConstant("</div></div>");
		}
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookPermissionsDialogUiBinder uiBinder = GWT.create(PlaceBookPermissionsDialogUiBinder.class);

	private static final ProvidesKey<Permission> keyProvider = new ProvidesKey<Permission>()
	{
		@Override
		public Object getKey(final Permission permission)
		{
			return permission.email;
		}
	};

	@UiField
	Panel listPanel;

	@UiField
	TextBox emailBox;

	@UiField
	ListBox permissionsBox;

	@UiField
	Button removePermission;

	private final CellList<Permission> cellList;
	private final PlaceBookController controller;
	private final SingleSelectionModel<Permission> selectionModel;
	private List<Permission> permissionList;

	public PlaceBookPermissionsDialog(final PlaceBookController controller)
	{
		setTitle(uiMessages.editPermissions());
		this.controller = controller;
		setWidget(uiBinder.createAndBindUi(this));

		permissionsBox.addItem(uiMessages.read());
		permissionsBox.addItem(uiMessages.readwrite());

		cellList = new CellList<Permission>(new PermissionsCell(), keyProvider);
		cellList.setWidth("100%");

		selectionModel = new SingleSelectionModel<Permission>(keyProvider);
		cellList.setSelectionModel(selectionModel);

		selectionModel.addSelectionChangeHandler(new Handler()
		{
			@Override
			public void onSelectionChange(final SelectionChangeEvent event)
			{
				final Permission permission = selectionModel.getSelectedObject();
				if (permission != null)
				{
					emailBox.setText(permission.email);

					if (permission.email.equals(controller.getItem().getOwner().getEmail()))
					{
						emailBox.setEnabled(false);
						permissionsBox.setEnabled(false);
						removePermission.setEnabled(false);
					}
					else
					{
						emailBox.setEnabled(true);
						permissionsBox.setEnabled(true);
						removePermission.setEnabled(true);

						if (permission.permission.equals("R_W"))
						{
							permissionsBox.setSelectedIndex(1);
						}
						else
						{
							permissionsBox.setSelectedIndex(0);
						}
						emailBox.setFocus(true);
					}

				}
				else
				{
					emailBox.setEnabled(false);
					permissionsBox.setEnabled(false);
					removePermission.setEnabled(false);
				}
			}
		});

		listPanel.add(cellList);

		center();
		updatePermissionsList();
		selectionModel.setSelected(permissionList.get(0), true);
	}

	@UiHandler("addUser")
	void handleAddUser(final ClickEvent event)
	{
		controller.getItem().getPermissions().put("", "R");
		updatePermissionsList();
		for (final Permission permission : permissionList)
		{
			if (permission.email.equals(""))
			{
				selectionModel.setSelected(permission, true);
				return;
			}
		}
	}

	@UiHandler("emailBox")
	void handleEditEmail(final KeyUpEvent event)
	{
		if (selectionModel.getSelectedObject() != null)
		{
			final Permission selection = selectionModel.getSelectedObject();

			controller.getItem().getPermissions().remove(selection.email);
			controller.getItem().getPermissions().put(emailBox.getText(), selection.permission);
			selection.email = emailBox.getText();
			cellList.redraw();
			controller.markChanged();
		}
	}

	@UiHandler("removePermission")
	void handleRemovePermssion(final ClickEvent event)
	{
		if (selectionModel.getSelectedObject() != null)
		{
			final Permission selection = selectionModel.getSelectedObject();

			controller.getItem().getPermissions().put(selection.email, null);
			controller.markChanged();
			updatePermissionsList();
		}
	}

	@UiHandler("permissionsBox")
	void handleSelectPermissions(final ChangeEvent event)
	{
		if (selectionModel.getSelectedObject() != null)
		{
			final Permission selection = selectionModel.getSelectedObject();

			if (permissionsBox.getSelectedIndex() == 0)
			{
				selection.permission = "R";
			}
			else
			{

				selection.permission = "R_W";
			}
			controller.getItem().getPermissions().put(selection.email, selection.permission);

			cellList.redraw();
			controller.markChanged();
		}
	}

	private void updatePermissionsList()
	{
		permissionList = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.email = controller.getItem().getOwner().getEmail();
		permission.permission = "R_W";
		permissionList.add(permission);

		for (final String user : controller.getItem().getPermissions().keySet())
		{
			if (user.equals(controller.getItem().getOwner().getEmail()))
			{
				continue;
			}

			permission = new Permission();
			permission.email = user;
			permission.permission = controller.getItem().getPermissions().get(user);

			permissionList.add(permission);
		}

		controller.getItem().getPermissions().put(controller.getItem().getOwner().getEmail(), "R_W");

		cellList.setRowData(permissionList);
	}
}