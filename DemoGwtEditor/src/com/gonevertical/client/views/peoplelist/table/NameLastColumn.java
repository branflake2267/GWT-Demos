package com.gonevertical.client.views.peoplelist.table;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;


public class NameLastColumn extends Column<PeopleDataProxy, String> {
  
  public NameLastColumn() {
    super(new TextCell());
  }

  @Override
  public String getValue(PeopleDataProxy object) {
    String name = object.getNameLast();
    String s = "";
    if (name != null) {
      s = name;
    }
    return s;
  }

}
  

