package com.gonevertical.client.views.widgets.paging;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;

public class PageButton extends Composite implements ClickHandler {

  private int page = 0;
  
  public PageButton(int page) {
    this.page = page;
    String p = Integer.toString(page);
    PushButton b = new PushButton(p);
    initWidget(b);
    b.addClickHandler(this);
    b.addStyleName("Core-Page-Button");
  }
  
  public int getPage() {
    return --page;
  }
  
  private void fire() { 
  	NativeEvent nativeEvent = Document.get().createChangeEvent();
    ChangeEvent.fireNativeEvent(nativeEvent, this);
  }

  public void onClick(ClickEvent event) {
  	fire();
  }
  
  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }
  
  
}
