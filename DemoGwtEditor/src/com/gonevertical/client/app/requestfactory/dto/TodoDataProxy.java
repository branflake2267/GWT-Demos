package com.gonevertical.client.app.requestfactory.dto;

import java.util.Date;

import com.gonevertical.server.data.TodoData;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(TodoData.class)
public interface TodoDataProxy extends EntityProxy {

  void setId(String id);

  String getId();
  
  Date getDateCreated();

  void setTodo(String todo);

  String getTodo();

  void setNote(String note);

  String getNote();
  
}
