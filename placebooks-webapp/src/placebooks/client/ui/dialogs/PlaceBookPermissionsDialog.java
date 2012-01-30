package placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.json.client.JSONString;
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

	private static PlaceBookPermissionsDialogUiBinder uiBinder = GWT.create(PlaceBookPermissionsDialogUiBinder.class);

	private static final ProvidesKey<Permission> keyProvider = new ProvidesKey<Permission>()
	{
		@Override
		public Object getKey(final Permission permission)
		{
			return permission.email;
		}
	};


	  /**
	   * The Cell used to render a {@link ContactInfo}.
	   */
	  private class PermissionsCell extends AbstractCell<Permission>
	  {
	    @Override
	    public void render(Context context, Permission value, SafeHtmlBuilder sb)
	    {
	      // Value can be null, so do a null check..
	      if (value == null) {
	        return;
	      }

	      sb.appendHtmlConstant("<div>");

	      // Add the contact image.
	      sb.appendHtmlConstant("<div>");
	      sb.appendHtmlConstant(value.email);
	      sb.appendHtmlConstant("</div>");

	      // Add the name and address.
	      sb.appendHtmlConstant("<div>");
	      if(value.email.equals(controller.getPages().getPlaceBook().getOwner().getEmail()))
	      {
		      sb.appendEscaped("Owner");	    	  
	      }
	      else if(value.email.equals("R_W"))
	      {
	    	  sb.appendEscaped("Read + Write");
	      }
	      else
	      {
	    	  sb.appendEscaped("Read");
	      }
	    	  
	      sb.appendHtmlConstant("</div></div>");
	    }
	  }
	
	private static class Permission
	{
		private String email;
		private String permission;
	}
	
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
		setTitle("Edit Permissions");
		this.controller = controller;
		setWidget(uiBinder.createAndBindUi(this));

		permissionsBox.addItem("Read");
		permissionsBox.addItem("Read + Write");		
		
		cellList = new CellList<Permission>(new PermissionsCell(), keyProvider);
		cellList.setWidth("100%");

		selectionModel = new SingleSelectionModel<Permission>(keyProvider);
		cellList.setSelectionModel(selectionModel);
		
		selectionModel.addSelectionChangeHandler(new Handler()
		{
			@Override
			public void onSelectionChange(SelectionChangeEvent event)
			{
				final Permission permission = selectionModel.getSelectedObject();
				if(permission != null)
				{
					emailBox.setText(permission.email);		
					
					if(permission.email.equals(controller.getPages().getPlaceBook().getOwner().getEmail()))
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
						
						if(permission.permission.equals("R_W"))
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
	
	@UiHandler("permissionsBox")
	void handleSelectPermissions(final ChangeEvent event)
	{
		if(selectionModel.getSelectedObject() != null)
		{
			Permission selection = selectionModel.getSelectedObject();

			if(permissionsBox.getSelectedIndex() == 0)
			{
				selection.permission = "R";				
			}
			else
			{

				selection.permission = "R_W";				
			}
			controller.getPages().getPlaceBook().getPermissions().put(selection.email, new JSONString(selection.permission));
			
			cellList.redraw();
			controller.markChanged();
		}
	}

	@UiHandler("addUser")
	void handleAddUser(final ClickEvent event)
	{
		controller.getPages().getPlaceBook().getPermissions().put("", new JSONString("R"));
		updatePermissionsList();
		for(Permission permission: permissionList)
		{
			if(permission.email.equals(""))
			{
				selectionModel.setSelected(permission, true);
				return;
			}
		}		
	}
	
	@UiHandler("removePermission")
	void handleRemovePermssion(final ClickEvent event)
	{
		if(selectionModel.getSelectedObject() != null)
		{
			Permission selection = selectionModel.getSelectedObject();

			controller.getPages().getPlaceBook().getPermissions().put(selection.email, null);
			controller.markChanged();			
			updatePermissionsList();
		}
	}

	@UiHandler("emailBox")
	void handleEditEmail(final KeyUpEvent event)
	{
		if(selectionModel.getSelectedObject() != null)
		{
			Permission selection = selectionModel.getSelectedObject();

			controller.getPages().getPlaceBook().getPermissions().put(selection.email, null);
			controller.getPages().getPlaceBook().getPermissions().put(emailBox.getText(), new JSONString(selection.permission));
			selection.email = emailBox.getText();			
			cellList.redraw();
			controller.markChanged();
		}
	}
	
	private void updatePermissionsList()
	{
		permissionList = new ArrayList<Permission>();
		Permission permission = new Permission();
		permission.email = controller.getPages().getPlaceBook().getOwner().getEmail();
		permission.permission = "R_W";
		permissionList.add(permission);

		for (final String user : controller.getPages().getPlaceBook().getPermissions().keySet())
		{
			if (user.equals(controller.getPages().getPlaceBook().getOwner().getEmail()))
			{
				continue;
			}

			permission = new Permission();
			permission.email = user;
			permission.permission = controller.getPages().getPlaceBook().getPermissions().get(user).isString().stringValue();

			permissionList.add(permission);
		}

		controller.getPages().getPlaceBook().getPermissions().put(controller.getPages().getPlaceBook().getOwner().getEmail(), new JSONString("R_W"));

		cellList.setRowData(permissionList);
	}
}