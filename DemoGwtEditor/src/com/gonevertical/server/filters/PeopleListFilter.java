package com.gonevertical.server.filters;

import java.util.List;

public class PeopleListFilter {

  private List<String> search;
  
  private long start;
  
  private long end;
  
  public void setSearch(List<String> search) {
    this.search = search;
  }
  public List<String> getSearch() {
    return search;
  }
  
  public void setStart(long start) {
    this.start = start;
  }
  public long getStart() {
    return start;
  }
  
  public void setEnd(long end) {
    this.end = end;
  }
  public long getEnd() {
    return end;
  }
  
}
