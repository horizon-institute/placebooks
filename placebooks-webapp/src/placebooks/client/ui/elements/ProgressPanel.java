package placebooks.client.ui.elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite implements HasText
{
	interface ProgressPanelUiBinder extends UiBinder<Widget, ProgressPanel>
	{
	}

	private static final ProgressPanelUiBinder uiBinder = GWT.create(ProgressPanelUiBinder.class);

	@UiField
	Label progressLabel;

	@UiField
	Panel progressPanel;

	@UiField
	Panel progressText;

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
	public void setText(final String text)
	{
		progressLabel.setText(text);
	}

	@Override
	public void setVisible(final boolean visible)
	{
		super.setVisible(visible);
		if (visible)
		{
			centerText();
		}
	}

	private void centerText()
	{
		final Widget widget = progressPanel.getParent();
		final int left = (widget.getOffsetWidth() - progressText.getOffsetWidth()) >> 1;
		final int top = (widget.getOffsetHeight() - progressText.getOffsetHeight()) >> 1;
		GWT.log("LEFT = " + widget.getOffsetWidth() + " - " + progressText.getOffsetWidth() + " / 2 = " + left);
		GWT.log("TOP = " + widget.getOffsetHeight() + " - " + progressText.getOffsetHeight() + " / 2 = " + top);
		progressText.getElement().getStyle().setTop(top, Unit.PX);
		progressText.getElement().getStyle().setLeft(left, Unit.PX);
	}
}
