package com.gonevertical.client.app.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface ApplicationRequestFactory extends RequestFactory {

  UserDataRequest getUserDataRequest();
  
  PeopleDataRequest getPeopleDataRequest();
  
  TodoDataRequest getTodoDataRequest();
}
