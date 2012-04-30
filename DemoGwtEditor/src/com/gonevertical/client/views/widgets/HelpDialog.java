package com.gonevertical.client.views.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpDialog extends PopupPanel {

  private static HelpDialogUiBinder uiBinder = GWT.create(HelpDialogUiBinder.class);
  @UiField HTML htmlText;

  interface HelpDialogUiBinder extends UiBinder<Widget, HelpDialog> {
  }

  public HelpDialog() {
    super();
    setWidget(uiBinder.createAndBindUi(this));
    
    setAnimationEnabled(true);
    setAutoHideEnabled(true);
  }
  
  public void setHelpText(String html) {
    // TODO I'm going to skip this for now, b/c I don't want it to encode my html overkill...
    //SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(html);
    htmlText.setHTML(html);
  }
  
  public void draw(Widget showRelativeToThisWidget) {
    if (showRelativeToThisWidget == null) {
      return;
    }
    showRelativeTo(showRelativeToThisWidget);
  }

}
