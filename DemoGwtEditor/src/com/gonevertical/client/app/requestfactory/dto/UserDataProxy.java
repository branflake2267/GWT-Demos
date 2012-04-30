package com.gonevertical.client.app.requestfactory.dto;

import com.gonevertical.server.data.UserData;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(UserData.class)
public interface UserDataProxy extends EntityProxy {

  String getLoginUrl();
  
  String getLogoutUrl();
  
  String getId();

  String getGoogleNickname();
  
}
