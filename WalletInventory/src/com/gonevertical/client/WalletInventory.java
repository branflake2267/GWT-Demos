package com.gonevertical.client;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.views.widgets.LoginWidget;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WalletInventory implements EntryPoint {

  /**
   * I change this when I deploy to app engine
   */
  public static final int VERSION = 14;
  
  /**
   * Global App Objects/Classes - using deferred binding to create object
   */
  private ClientFactory clientFactory = GWT.create(ClientFactory.class);
  
  
  /**
   * entry or first thing to load
   */
  public void onModuleLoad() {
   
    // rootpanel in clientfactory
    
  }
  
  

}
