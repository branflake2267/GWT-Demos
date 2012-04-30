package com.gonevertical.client.app;

import com.gonevertical.client.app.requestfactory.ApplicationRequestFactory;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.views.homeview.HomePlace;
import com.gonevertical.client.views.peopleedit.PeopleEditPlace;
import com.gonevertical.client.views.peoplelist.PeopleListPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;


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
  HomePlace.Tokenizer getHomePlaceTokenizer();

  /**
   * used by the historyMapper
   */
  PeopleListPlace.Tokenizer getPeopleListTokenizer();
  
  /**
   * used by the historyMapper
   */
  PeopleEditPlace.Tokenizer getPeopleEditTokenizer();
}
