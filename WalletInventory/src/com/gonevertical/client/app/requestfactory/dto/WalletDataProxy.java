package com.gonevertical.client.app.requestfactory.dto;

import java.util.List;

import com.gonevertical.server.jdo.WalletData;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(WalletData.class)
public interface WalletDataProxy extends EntityProxy {
  
  String getId();
  
  Long getUserId();
  
  void setName(String name);
  
  String getName();
  
  void setItems(List<WalletItemDataProxy> items);
  
  List<WalletItemDataProxy> getItems();
  
}
