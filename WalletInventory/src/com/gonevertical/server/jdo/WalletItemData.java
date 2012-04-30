package com.gonevertical.server.jdo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.gonevertical.server.PMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, extensions = { @Extension(vendorName = "datanucleus", key = "key", value = "version") })
public class WalletItemData {
 
  @NotPersistent
  private static final Logger log = Logger.getLogger(WalletItemData.class.getName());

  public static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }

  public static WalletItemData findWalletItemData(String id) {
    //Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    Key key = KeyFactory.stringToKey(id);
    PersistenceManager pm = getPersistenceManager();
    try {
      WalletItemData e = pm.getObjectById(WalletItemData.class, key);
      // if (e.getUserId() != uid) { // i'm not going to enforce this on a child here.
      //e = null;
      //}
      return e;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }

  public static long countAll() {
    PersistenceManager pm = getPersistenceManager();
    try {
      // TODO change to JDO
      //return ((Number) em.createQuery("select count(o) from WalletItemData o").getSingleResult()).longValue();
    } finally {
      pm.close();
    }
    return 0l;
  }

  public static long countWalletItemDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select count(o) from WalletItemData o where o.userId=:userId");
      // TODO change to JDO
      //query.setParameter("userId", uid);
      //return ((Number) query.getSingleResult()).longValue();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return 0l;
  }

  public static List<WalletItemData> findWalletItemDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select o from WalletItemData o");
      query.setFilter("userId=\"" + uid + "\"");
      List<WalletItemData> list = (List<WalletItemData>) query.execute();
      list.size();
      return list;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }



  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private Long version;

  /**
   * the entity owner - the person who's logged in. Will set this on the client side. 
   * I'm not to concerned b/c its a child of a parent. Keep for reference in debugging.
   */
  @Persistent
  private Long userId;

  @Persistent
  private String name;

  @Persistent
  private String contact;


  public void setId(Key parentKey, String id) {
    if (parentKey == null) {
      return;
    }
    if (id != null) {
      setId(id);
    } else {
      key = KeyFactory.createKey(parentKey, WalletItemData.class.getName(), null);
    }
  }
  public void setId(String id) {
    key = KeyFactory.stringToKey(id);
  }
  public String getId() {
    String id = null;
    if (key != null) {
      id = KeyFactory.keyToString(key);
    }
    return id;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  public Long getUserId() {
    return userId;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
  public Long getVersion() {
    if (version == null) {
      return 0l;
    }
    return version;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }
  public String getContact() {
    return contact;
  }


  public void persist() {

    // set the owner of this entity
    Long uid = UserData.getLoggedInUserId();
    setUserId(uid);

    // JPA does this automatically, but JDO won't. Not sure why.
    if (version == null) {
      version = 0l;
    }
    version++;
    
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.makePersistent(this);
      tx.commit();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
  }

  /**
   * can't use this 
   *    - errors this -> Transient instances cant be deleted.
   */
  private void remove() {
    // for checking owner
    Long uid = UserData.getLoggedInUserId();
    if (userId != null && uid != userId) {
      log.severe("WalletItemData.remove() Error: Something weird going on in setting UID. userId=" + userId + " uid=" + uid);
      return;
    }
    
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      WalletItemData e = pm.detachCopy(this); // this won't fix it
      pm.deletePersistent(e);
      tx.commit();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
  }
  
  public static Boolean deleteWalletItemData(String id) {
    
    if (id == null) {
      return false;
    }
    Long uid = UserData.getLoggedInUserId();
    
    Key key = KeyFactory.stringToKey(id);
    
    Boolean success = false;
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      
      WalletItemData e = pm.getObjectById(WalletItemData.class, key);
      if (e.getUserId() != uid) { // check owner
        log.severe("WalletItemData.remove() Error: Something weird going on in setting UID. e.getUserId()=" + e.getUserId() + " uid=" + uid);
        success = false;
        
      } else {
        tx.begin();
        pm.deletePersistent(e);
        tx.commit();
        success = true;
      }
      
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error:", e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
        success = false;
      }
      pm.close();
    }
    
    return success;
  }
  
}
