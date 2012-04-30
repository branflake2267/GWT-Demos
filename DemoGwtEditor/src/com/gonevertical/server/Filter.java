package com.gonevertical.server;

import com.google.appengine.api.datastore.Query.FilterOperator;

public class Filter {

  private String propertyName;
  
  private FilterOperator operator;
  
  private Object value;
  
  public Filter(String propertyName, FilterOperator operator, Object value) {
    setFilter(propertyName, operator, value);
  }
  
  public void setFilter(String propertyName, FilterOperator operator, Object value) {
    this.propertyName = propertyName;
    this.operator = operator;
    this.value = value;
  }
  
  public String getPropertyName() {
    return propertyName;
  }
  
  public FilterOperator getOperator() {
    return operator;
  }
  
  public Object getValue() {
    return value;
  }
}
