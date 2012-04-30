package com.gonevertical.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
  
  //private static final Logger log = Logger.getLogger(PMF.class.getName());
    
  // GAE xml
  private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
  
  // tomcat6 txt file settings
  //private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");


  private PMF() {
  }

  public static PersistenceManagerFactory get() {
      return pmfInstance;
  }
  
}
