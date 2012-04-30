package com.gonevertical.client.app.requestfactory.dto;

import java.util.Date;
import java.util.List;

import com.gonevertical.server.data.PeopleData;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(PeopleData.class)
public interface PeopleDataProxy extends EntityProxy {

  void setId(String id);

  String getId();

  Date getDateCreated();

  void setActive(boolean active);

  Boolean getActive();

  void setNameFirst(String nameFirst);

  String getNameFirst();

  void setNameLast(String nameLast);

  String getNameLast();

  void setGender(Integer gender);

  Integer getGender();

  void setNote(String note);

  String getNote();

  void setTodos(List<TodoDataProxy> todos);

  List<TodoDataProxy> getTodos();

}