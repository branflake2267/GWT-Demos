package com.gonevertical.client.app.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

public interface EditEventHandler<T extends EntityProxy> extends EventHandler {
  
  public void onEditEvent(EditEvent<T> event);
  
}
