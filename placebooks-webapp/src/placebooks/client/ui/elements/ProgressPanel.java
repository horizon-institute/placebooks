package placebooks.client.ui.elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite implements HasText
{
	interface ProgressPanelUiBinder extends UiBinder<Widget, ProgressPanel>
	{
	}

	private static final ProgressPanelUiBinder uiBinder = GWT.create(ProgressPanelUiBinder.class);

	@UiField
	Label progressLabel;
	
	public ProgressPanel()
	{
		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);
	}

	@Override
	public String getText()
	{
		return progressLabel.getText();
	}

	@Override
	public void setText(String text)
	{
		progressLabel.setText(text);		
	}
}
