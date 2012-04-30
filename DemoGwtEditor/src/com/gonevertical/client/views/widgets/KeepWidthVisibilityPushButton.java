package com.gonevertical.client.views.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class KeepWidthVisibilityPushButton extends Composite {

  private static KeepWidthVisibilityPushButtonUiBinder uiBinder = GWT.create(KeepWidthVisibilityPushButtonUiBinder.class);
  @UiField PushButton button;
  @UiField FlowPanel flowPanel;

  interface KeepWidthVisibilityPushButtonUiBinder extends UiBinder<Widget, KeepWidthVisibilityPushButton> {
  }

  public KeepWidthVisibilityPushButton() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public PushButton getPushButton() {
    return button;
  }
  
  @Override
  public void setVisible(boolean visible) {
    setSize();
    button.setVisible(visible);
  }

  private void setSize() {
    int w = button.getOffsetWidth();
    int h = button.getOffsetHeight();
    if (w == 0 && h == 0) {
      return;
    }
    flowPanel.setWidth(w + "px");
    flowPanel.setHeight(h + "px");
  }

}
