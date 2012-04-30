package com.gonevertical.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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

public abstract class RequestFactoryUtilsJpa {

  private static final Logger log = Logger.getLogger(RequestFactoryUtilsJpa.class.getName());

  public static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
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
    EntityManager em = getEntityManager();
    try {
      T e = em.find(clazz, k);
      return e;
    } finally {
      em.close();
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
    EntityManager em = getEntityManager();
    try {
      T e = em.find(clazz, key);
      return e;
    } finally {
      em.close();
    }
  }

  /**
   * persist object
   *  NOTE: be sure to increment version in o
   * @param o
   * @return
   */
  public static <T> T persist(T o) {
    if (o == null) {
      return null;
    }

    // Could be done via interface, but simpler to do it in parent method, b/c it doesn't expose the public increment on client side
    // o.incrementVersion();

    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      em.persist(o);
      tx.commit();
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils Error: persist(): this=" + o, e);
      e.printStackTrace();
      return null;
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      em.close();
    }

    return o;
  }

  /**
   * only remove if admin
   * @param o
   * @return
   */
  public static <T> boolean removeByAdminOnly(T o) {

    UserData user = UserData.findLoggedInUserPrivileges();  
    if (user.canAdmin() == false) {
      return false;
    }

    Boolean success = null;
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      em.persist(o);
      tx.commit();
      success = true;
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils.removeAdminKeyOnly() Error: remove(): o=" + o, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      em.close();
    }

    return success;
  }

  /**
   * remove object
   * @param o
   * @return
   */
  public static <T> boolean remove(T o) {

    Boolean success = null;
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      em.remove(o);
      tx.commit();
      success = true;
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils.removeAdminKeyOnly() Error: remove(): o=" + o, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      em.close();
    }

    return success;
  }

  public static <T> boolean remove(Class<T> clazz, String id) {

    Key key = KeyFactory.stringToKey(id);
    T o = find(clazz, key); 
   
    Boolean success = null;
    EntityManager em = getEntityManager();
    EntityTransaction tx = em.getTransaction();
    try {
      tx.begin();
      em.remove(o);
      tx.commit();
      success = true;
    } catch (Exception e) {
      log.log(Level.SEVERE, "RequestFactoryUtils.removeAdminKeyOnly() Error: remove(): o=" + o, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      em.close();
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

    EntityManager em = getEntityManager();
    try {
      javax.persistence.Query q = em.createQuery("select o from " + clazz.getSimpleName() + " o " + qfilter);
      q.setFirstResult((int) rangeStart);
      q.setMaxResults((int) rangeEnd);
      List<T> list = q.getResultList();
      // force to get all the employees
      list.size();
      return list;

    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: " + clazz.getName() + ".findList(): qfilter=" + qfilter, e);
      e.printStackTrace();
    } finally {
      em.close();
    }
    return null;
  }

  private static String getFilter(ArrayList<Filter> filter) {
    if (filter == null || filter.size() == 0) {
      return "";
    }
    String s = " WHERE ";
    Iterator<Filter> itr = filter.iterator();
    int i=0;
    while(itr.hasNext()) {
      Filter e = itr.next();
      s += getFilterValues(e);
      if (i < filter.size() - 1) {
        s += " AND ";
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
      s = "=";
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
