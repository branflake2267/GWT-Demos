package com.gonevertical.server.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import com.gonevertical.client.app.requestfactory.dto.PeopleListFilterProxy;
import com.gonevertical.server.Filter;
import com.gonevertical.server.RequestFactoryUtilsJdo;
import com.gonevertical.server.RequestFactoryUtilsJpa;
import com.gonevertical.server.filters.PeopleListFilter;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;

@PersistenceCapable // @Entity
public class PeopleData {

  private static final Logger log = Logger.getLogger(PeopleData.class.getName());

  /**
   * id is base64 string of Key
   * @param id
   * @return
   */
  public static PeopleData findPeopleData(String id) {
    PeopleData d = RequestFactoryUtilsJdo.find(PeopleData.class, id);
    return d;
  }

  public static List<PeopleData> findPeopleData(PeopleListFilter filter) {
    ArrayList<Filter> tfilter = null;
    
    if (filter.getSearch() != null) {
      tfilter = new ArrayList<Filter>();
      List<String> searchList = filter.getSearch();
      Iterator<String> itr = searchList.iterator();
      while(itr.hasNext()) {
        String s = itr.next();
        if (s != null) {
          s = s.toLowerCase().trim();
          Filter f = new Filter("search", FilterOperator.EQUAL, s);
          tfilter.add(f);
        }
      }
    } 
    
    List<PeopleData> list = RequestFactoryUtilsJdo.findList(PeopleData.class, tfilter, filter.getStart(), filter.getEnd());
    return list;
  }

  public static Long findCount(PeopleListFilter filter) {
    ArrayList<Filter> tfilter = null;

    if (filter.getSearch() != null) {
      tfilter = new ArrayList<Filter>();
      List<String> searchList = filter.getSearch();
      Iterator<String> itr = searchList.iterator();
      while(itr.hasNext()) {
        String s = itr.next();
        if (s != null) {
          s = s.toLowerCase().trim();
          Filter f = new Filter("search", FilterOperator.EQUAL, s);
          tfilter.add(f);
        }
      }
    } 

    Long count = RequestFactoryUtilsJdo.findCount(PeopleData.class, tfilter);
    return count;
  }


  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) // @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  @Persistent
  private Long version;

  @Persistent
  private Date dateCreated;

  @Persistent
  private Boolean active;

  @Persistent
  private String nameFirst;

  @Persistent
  private String nameLast;

  @Persistent
  private Integer gender;

  @Persistent
  private Text note;

  @Persistent // @OneToMany(cascade = CascadeType.ALL)
  private HashSet<String> search;

  /**
   * owned collection 
   * @Persistent(defaultFetchGroup = "true", dependentElement = "true") - for JDO
   */
  @Persistent(defaultFetchGroup = "true", dependentElement = "true") // @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, targetEntity = TodoData.class)
  private List<TodoData> todos;



  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("key=" + key + ",");
    sb.append("version=" + version + ",");
    // TODO
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

  public Key getKey() {
    return key;
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
  private void incrementVersion() {
    if (version == null) {
      version = 0l;
    } else {
      version++;
    }
  }

  public void setDateCreated() {
    if (dateCreated == null) {
      dateCreated = new Date();
    }
  }
  public Date getDateCreated() {
    return dateCreated;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
  public Boolean getActive() {
    return active;
  }

  public void setNameFirst(String nameFirst) {
    this.nameFirst = nameFirst;
    setSearch();
  }
  public String getNameFirst() {
    return nameFirst;
  }

  public void setNameLast(String nameLast) {
    this.nameLast = nameLast;
    setSearch();
  }
  public String getNameLast() {
    return nameLast;
  }

  public void setGender(Integer gender) {
    this.gender = gender;
  }
  public Integer getGender() {
    return gender;
  }

  public void setNote(String note) {
    if (note == null) {
      this.note = null;
    } else {
      this.note = new Text(note);
    }
  }
  public String getNote() {
    if (note == null) {
      return null;
    } else {
      return note.getValue();
    }
  }

  public void setTodos(List<TodoData> todos) {
    this.todos = todos;
  }
  public List<TodoData> getTodos() {
    return todos;
  }

  public PeopleData persist() {
    incrementVersion();
    setDateCreated();   
    PeopleData r = RequestFactoryUtilsJdo.persist(this);
    return r;
  }


  public boolean remove() {
    return RequestFactoryUtilsJdo.removeByAdminOnly(this);
  }

  /**
   * this is a bit redundant when both names are set. Good for now, and good for example. No matter, its fast
   */
  private void setSearch() {
    HashSet<String> hs = new HashSet<String>();

    if (nameLast != null) {
      String ln = nameLast;
      if (ln != null && ln.trim().length() > 0) {
        hs.add(ln.toLowerCase());

        if (ln.length() > 1) {

          for (int i=1; i < ln.length(); i++) {
            String s = ln.substring(0, i);
            hs.add(s.toLowerCase());
          }
        }
      }
    }

    if (nameFirst != null) {
      String fn = nameFirst;
      if (fn != null && fn.trim().length() > 0) {
        hs.add(fn.toLowerCase());

        if (fn.length() > 1) {
          for (int i=1; i < fn.length(); i++) {
            String s = fn.substring(0, i);
            hs.add(s.toLowerCase());
          }
        }
      }
    }
    search = hs;
  }
}
