package com.gonevertical.client.app.requestfactory;

import com.gonevertical.server.jdo.WalletItemData;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(WalletItemData.class)
public interface WalletItemDataRequest extends RequestContext {

  Request<Boolean> deleteWalletItemData(String id);
  
}
