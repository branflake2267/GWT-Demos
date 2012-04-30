package com.gonevertical.client.views.peoplelist.table;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;


public class NameFirstColumn extends Column<PeopleDataProxy, String> {
  
  public NameFirstColumn() {
    super(new TextCell());
  }

  @Override
  public String getValue(PeopleDataProxy object) {
    String name = object.getNameFirst();
    String s = "";
    if (name != null) {
      s = name;
    }
    return s;
  }

}
  

