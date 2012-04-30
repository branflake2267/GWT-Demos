package com.gonevertical.client.app.requestfactory.dto;

import java.util.List;

import com.gonevertical.server.filters.PeopleListFilter;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(PeopleListFilter.class)
public interface PeopleListFilterProxy extends ValueProxy {

  public void setSearch(List<String> search);
  
  public List<String> getSearch();
  
  public void setStart(long start);
  
  public long getStart();
  
  public void setEnd(long end);
  
  public long getEnd();
  
}
