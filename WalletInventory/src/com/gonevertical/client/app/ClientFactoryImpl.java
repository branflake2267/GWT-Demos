package com.gonevertical.client.app;

import com.gonevertical.client.app.activity.ApplicationActivityMapper;
import com.gonevertical.client.app.activity.ApplicationPlaceHistoryMapper;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.ApplicationRequestFactory;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.layout.Layout;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ClientFactoryImpl implements ClientFactory {

  /**
   * shell for the app - includes LoginWidget - processes the credentials
   */
  private Layout layout = new Layout();
  
  /**
   * default start point in application - if nothing is in url
   */
  private Place defaultPlace = new SignInPlace();
  
  /**
   * for app's global events
   */
  private static final EventBus eventBus = new SimpleEventBus();
  
  /**
   * for datastore persistence calls
   */
  private final ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
  
  /**
   * place's directory
   */
  private final ActivityMapper activityMapper = new ApplicationActivityMapper(this);

  /**
   * place's director (internal flagger|flagman for places)
   */
  private final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);

  /**
   * URL tokenizer directory
   */
  private final ApplicationPlaceHistoryMapper historyMapper = GWT.create(ApplicationPlaceHistoryMapper.class);
  
  /**
   * URL tokenizer's director (URL event flagger|flagman)
   */
  private final PlaceHistoryHandler placeHistoryHandler;
  
  /**
   * app place's director (internal flagger|flagman) (not deprecated if your seeing it)
   */
  private final PlaceController placeController = new PlaceController(eventBus);
  
  /**
   * logged in user (or null userid with the login url)
   */
  private UserDataProxy userData;

  /**
   * is the user loggedin or loggedout, null hasn't happened yet
   */
  private Boolean isLoggedIn;
  
  
  /**
   * init
   */
  public ClientFactoryImpl() {
    
    RootPanel.get().add(layout);
    
    activityManager.setDisplay(layout.getContentPanel());
    
    requestFactory.initialize(eventBus);
        
    // tell the historyMapper there are tokenizers (below) to use in here. 
    historyMapper.setFactory(this);
    
    
    placeHistoryHandler = new PlaceHistoryHandler(historyMapper);
    
    // This is not deprecated if you see it so!
    placeHistoryHandler.register(placeController, eventBus, defaultPlace);
    
    // the loginWidget needs all the stuff setup to fetch userData - inits credentials process
    layout.setClientFactory(this);
    
    // Goes to the place represented on URL else default place
    placeHistoryHandler.handleCurrentHistory();
  }


  @Override
  public EventBus getEventBus() {
    return eventBus;
  }
  
  @Override
  public ApplicationRequestFactory getRequestFactory() {
    return requestFactory;
  }
  
  @Override
  public PlaceController getPlaceController() {
    return placeController;
  }

  @Override
  public void setUserData(UserDataProxy userData) {
    this.userData = userData;
    
    Auth auth = null;
    if (userData != null && 
        userData.getId() != null && 
        userData.getId().matches(".*([0-9]+).*") == true) {
      auth = Auth.LOGGEDIN;
      isLoggedIn = true;
    } else {
      auth = Auth.LOGGEDOUT;
      isLoggedIn = false;
    }
    eventBus.fireEvent(new AuthEvent(auth, userData));
  }
  
  @Override 
  public UserDataProxy getUserData() {
    return userData;
  }

  @Override
  public ActivityManager getActivityManager() {
    return activityManager;
  }
  
  @Override
  public Boolean getIsLoggedIn() {
    return isLoggedIn;
  }
  
  
  
  
  
  /***
   * Used by the HistoryMapper { @link ApplicationPlaceHistoryMapper }
   * Picked up by the historyMapper when needed.
   * this way, I can send in objects like the requestFactory into them and use it
   *  otherwise, the HistoryMapper runs decoupled with this here we can push objects into it.
   */
  public SignInPlace.Tokenizer getSignInTokenizer() {
    return new SignInPlace.Tokenizer(requestFactory);
  }
  /**
   * Used by the HistoryMapper { @link ApplicationPlaceHistoryMapper }
   */
  public WalletListPlace.Tokenizer getWalletListTokenizer() {
    return new WalletListPlace.Tokenizer(requestFactory);
  }
  /**
   * Used by the HistoryMapper { @link ApplicationPlaceHistoryMapper }
   */
  public WalletEditPlace.Tokenizer getWalletEditTokenizer() {
    return new WalletEditPlace.Tokenizer(requestFactory);
  }

}
