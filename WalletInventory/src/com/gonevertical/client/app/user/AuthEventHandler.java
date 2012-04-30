package com.gonevertical.client.app.user;

import com.google.gwt.event.shared.EventHandler;

public interface AuthEventHandler extends EventHandler {
  
  public void onAuthEvent(AuthEvent event);
  
}
