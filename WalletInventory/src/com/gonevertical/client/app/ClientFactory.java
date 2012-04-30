package com.gonevertical.client.app;

import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.ApplicationRequestFactory;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.SimplePanel;


public interface ClientFactory {
  
	EventBus getEventBus();
	
	ApplicationRequestFactory getRequestFactory();
	
	PlaceController getPlaceController();

  void setUserData(UserDataProxy data);

  UserDataProxy getUserData();

  ActivityManager getActivityManager();
  
  Boolean getIsLoggedIn();
  
  
  /**
   * used by the historyMapper
   */
  SignInPlace.Tokenizer getSignInTokenizer();
  /**
   * used by the historyMapper
   */
  WalletListPlace.Tokenizer getWalletListTokenizer();
  /**
   * used by the historyMapper
   */
  WalletEditPlace.Tokenizer getWalletEditTokenizer();

  
  
}
