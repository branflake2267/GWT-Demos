package com.gonevertical.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Transaction;
import javax.jdo.PersistenceManager;

import com.gonevertical.server.data.UserData;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public abstract class RequestFactoryUtilsJdo {

  private static final Logger log = Logger.getLogger(RequestFactoryUtilsJdo.class.getName());
  
  /**
   * get Persistence manager
   * @return
   */
  public static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }
  
  /**
   * get Google User
   * @return
   */
  private static User getGoogleUser() {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn() == false) {
      return null;
    }
    User user = userService.getCurrentUser();
    return user;
  }
  
  /**
   * get person logged in
   * @return
   */
  public static Long getLoggedInUserId() {
    User user = getGoogleUser();
    if (user == null) {
      return null;
    }
    UserData userData = UserData.findUserDataByGoogleUserId(user.getUserId());
    if (userData == null) {
      return null;
    }
    if (userData.getId() == null) {
      return null;
    }

    Key key = userData.getKey();
    if (key == null) {
      return null;
    }

    return key.getId();
  }

  /**
   * find[class] by id
   *  Id is of base64 representation of the key
   * @param clazz
   * @param id
   * @return
   */
  public static <T> T find(Class<T> clazz, String id) {
    if (id == null) {
      return null;
    }
    Key k = KeyFactory.stringToKey(id);
    PersistenceManager pm = getPersistenceManager();
    try {
      T e = pm.getObjectById(clazz, k);
      return e;
    } finally {
      pm.close();
    }
  }
  
  /**
   * find object by key
   * 
   * @param clazz
   * @param key
   * @return
   */
  public static <T> T find(Class<T> clazz, Key key) {
    PersistenceManager pm = getPersistenceManager();
    try {
      T e = pm.getObjectById(clazz, key);
      return e;
    } finally {
      pm.close();
    }
  }
  
  /**
   * persist object
   *  NOTE: be sure to increment version in jdo
   * @param jdo
   * @return
   */
  public static <T> T persist(T jdo) {
    if (jdo == null) {
      return null;
    }
    
    // Could be done via interface, but simpler to do it in parent method, b/c it doesn't expose the public increment on client side
    // jdo.incrementVersion();
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.makePersistent(jdo);
      tx.commit();
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils Error: persist(): this=" + jdo, e);
      e.printStackTrace();
      return null;
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    
    return jdo;
  }
  
  /**
   * only remove if admin
   * @param jdo
   * @return
   */
  public static <T> boolean removeByAdminOnly(T jdo) {

    UserData user = UserData.findLoggedInUserPrivileges();  
    if (user.canAdmin() == false) {
      return false;
    }

    Boolean success = null;
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.deletePersistent(jdo);
      tx.commit();
      success = true;
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils.removeAdminKeyOnly() Error: remove(): jdo=" + jdo, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    
    return success;
  }
 
  /**
   * remove object
   * @param jdo
   * @return
   */
  public static <T> boolean remove(T jdo) {
    
    Boolean success = null;
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.deletePersistent(jdo);
      tx.commit();
      success = true;
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils.removeAdminKeyOnly() Error: remove(): jdo=" + jdo, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    
    return success;
  }
  
  /**
   * query list
   * @param clazz
   * @param qfilter
   * @return
   */
  public static <T> List<T> findList(Class<T> clazz, ArrayList<Filter> filter, long rangeStart, long rangeEnd) {
    String qfilter = getFilter(filter);
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select from " + clazz.getName());
      query.setFilter(qfilter);
      query.setRange(rangeStart, rangeEnd);
      List<T> list = (List<T>) query.execute();
      List<T> r = (List<T>) pm.detachCopyAll(list);
      return r;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: " + clazz.getName() + ".findList(): qfilter=" + qfilter, e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }
  
  private static String getFilter(ArrayList<Filter> filter) {
    if (filter == null || filter.size() == 0) {
      return null;
    }
    String s = "";
    Iterator<Filter> itr = filter.iterator();
    int i=0;
    while(itr.hasNext()) {
      Filter e = itr.next();
      s += getFilterValues(e);
      if (i < filter.size() - 1) {
        s += " && ";
      }
      i++;
    }
    return s;
  }

  private static String getFilterValues(Filter e) {
    String key = e.getPropertyName();
    String operator = getOperator(e);
    String value = getValue(e);
    String s = key + " " + operator + " " + value;
    return s;
  }

  private static String getValue(Filter e) {
    if (e.getValue() == null) {
      return "null";
    }
    
    String s = "";
    if (e.getValue() instanceof String) {
      String v = (String) e.getValue();
      s = "\"" + v + "\"";
    }
    return s;
  }

  private static String getOperator(Filter e) {
    String s = "";
    if (e.getOperator() == FilterOperator.EQUAL) {
      s = "==";
    }
    return s;
  }

  /**
   * find list count
   * @param clazz
   * @param qfilter
   * @return
   */
  public static <T> Long findCount(Class<T> clazz, ArrayList<Filter> filter) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      String q = clazz.getSimpleName();
      Query query = new Query(q);
      query.setKeysOnly();
      if (filter != null) {
        Iterator<Filter> itr = filter.iterator();
        while (itr.hasNext()) {
          Filter f = itr.next();
          query.addFilter(f.getPropertyName(), f.getOperator(), f.getValue());
        }
      }
      PreparedQuery pq = datastore.prepare(query);
      
      long total = pq.countEntities(FetchOptions.Builder.withDefaults());
      return total;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: " + clazz.getName() + ".findList(): ", e);
      e.printStackTrace();
    } 
    return null;
  }

}
