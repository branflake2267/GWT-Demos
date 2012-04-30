package com.gonevertical.client;

import com.gonevertical.client.app.ClientFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class InitApp implements EntryPoint {

  /**
   * I change this when I deploy to app engine
   */
  public static final int BUILD_VERSION = 3;

  public static final String URL = "https://demogwtpeople.appspot.com";
  
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
