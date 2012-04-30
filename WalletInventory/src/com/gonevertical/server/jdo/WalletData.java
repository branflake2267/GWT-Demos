package com.gonevertical.server.jdo;

import java.util.ArrayList;
import java.util.Iterator;
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
@Version(strategy=VersionStrategy.VERSION_NUMBER, column="version", extensions={@Extension(vendorName="datanucleus", key="key", value="version")})
public class WalletData {

  @NotPersistent
  private static final Logger log = Logger.getLogger(WalletData.class.getName());

  public static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }

  public static WalletData findWalletData(String id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    Key key = KeyFactory.stringToKey(id);
    PersistenceManager pm = getPersistenceManager();
    try {
      WalletData e = pm.getObjectById(WalletData.class, key);
      if (e.getUserId() != uid) { // does the uid really own this wallet
        e = null;
      }
      return e;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.findWalletData(id): id=" + id, e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }

  public static Long countAll() {
    PersistenceManager pm = getPersistenceManager();
    try {
      // TODO change to JDO
      //return ((Number) em.createQuery("select count(o) from WalletData o").getSingleResult()).longValue();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.countAll():", e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return 0l;
  }

  public static Long countWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      // TODO
      //javax.jdo.Query query = pm.newQuery("select count(o) from WalletData o  where o.userId==\"" + uid + "\"");
      //Long r = (Long) query.execute();
      return 0l;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.countWalletDataByUser():", e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }

  /**
   * get list of wallets for user
   * Note: use .with("childEntity") to get children
   * 
   * @return
   */
  public static List<WalletData> findWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();

    // only pull up the owner's wallets
    String qfilter = "userId==" + uid + "";

    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select from " + WalletData.class.getName());
      query.setFilter(qfilter);
      List<WalletData> list = (List<WalletData>) query.execute();

      // TODO i'm getting Object Manager has been closed here, something weird going on (lazy loader?)
      List<WalletData> r = (List<WalletData>) pm.detachCopyAll(list);

      return r;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.findWalletDataByUser(): qfilter=" + qfilter, e);
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }



  /**
   * primary id
   * transposed to web safe id (base64) for transport
   */
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  /**
   * mandatory parameter used with request factory
   * @Version in JPA annotation
   */
  @Persistent
  private Long version;

  /**
   * entity owner - the person who's logged in
   */
  @Persistent
  private Long userId; 

  /**
   * name of the wallet
   */
  @Persistent
  private String name;

  /**
   * items in the wallet
   * owned collection entities
   */
  @Persistent(defaultFetchGroup = "true", dependentElement = "true")  
  private List<WalletItemData> items;

  
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("key=" + key + ",");
    sb.append("version=" + version + ",");
    sb.append("userId=" + userId + ",");
    sb.append("name=" + name);
    sb.append("items={ " + items + " } ");
    return sb.toString();
  }
  
  public void setId(String id) {
    if (id == null) {
      return;
    }
    key = KeyFactory.stringToKey(id);
  }
  public String getId() {
    String id = null;
    if (key != null) {
      id = KeyFactory.keyToString(key);
    }
    return id;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
  public Long getVersion() {
    if (version == null) {
      version = 0l;
    }
    return version;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  public Long getUserId() {
    return userId;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public void setItems(List<WalletItemData> items) {
    if (items == null) {
      this.items = null;
      return;
    }

    // set owner, so the owner can remove later
    Long uid = UserData.getLoggedInUserId();

    Iterator<WalletItemData> itr = items.iterator();
    if (itr == null) {
      this.items = null;
      return;
    }
    ArrayList<WalletItemData> a = new ArrayList<WalletItemData>();
    while(itr.hasNext()) {
      WalletItemData d = itr.next();
      if (d != null && d.getId() != null) {
        d.setId(key, d.getId());
      }
      if (d != null) {
        d.setUserId(uid);
        a.add(d);
      }
    }
    this.items = items;
  }
  public List<WalletItemData> getItems() {
    return items;
  }

  public WalletData persist() {

    // set the owner of this entity
    Long uid = UserData.getLoggedInUserId();

    // JPA @Version does this automatically, but JDO @Version is not working like that. Not sure why.
    if (version == null) {
      version = 0l;
    }
    version++;

    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {

      // make sure the owner is making the modification
      if (key != null) { 
        WalletData e = pm.getObjectById(WalletData.class, key);
        if (e != null && e.getUserId() != uid) {
          log.severe("WalletData.persist() Error: Something weird going on in setting UID. e.getUserId=" + e.getUserId() + " uid=" + uid);
          return null;
        }
      }

      setUserId(uid);
      tx.begin();
      pm.makePersistent(this);
      tx.commit();

    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.persist(): this=" + this, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    return this;
  }

  /**
   * this won't work - check walletItemData for more notes
   */
  private void remove() {

    // for checking owner
    Long uid = UserData.getLoggedInUserId();
    if (userId != null && uid != userId) {
      log.severe("WalletData.remove() Error: Something weird going on in setting UID. userId=" + userId + " uid=" + uid);
      return;
    }

    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.deletePersistent(this);
      tx.commit();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: WalletData.remove(): this=" + this, e);
      e.printStackTrace();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
  }

  public static Boolean deleteWalletData(String id) {

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
      log.log(Level.SEVERE, "Error: WalletData.deleteWalletData(id): id=" + id, e);
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
