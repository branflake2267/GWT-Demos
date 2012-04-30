package com.gonevertical.client.app.requestfactory;

import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.server.data.UserData;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(UserData.class)
public interface UserDataRequest extends RequestContext {

  Request<UserDataProxy> findUserData(String id);
  
  Request<UserDataProxy> createUserData();
  
  // not needed here
  //InstanceRequest<UserDataProxy, UserDataProxy> persist();
  
  InstanceRequest<UserDataProxy, Boolean> remove();
  
  
}
