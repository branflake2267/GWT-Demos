package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.views.SignInView;
import com.gonevertical.client.views.impl.SignInViewImpl;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SignInActivity extends AbstractActivity implements SignInView.Presenter {

  private SignInView view;
  
  private ClientFactory clientFactory;

  private boolean running;
  
  public SignInActivity(SignInPlace place, ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  /**
   * Invoked by the ActivityManager to start a new Activity
   */
  @Override
  public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
    if (view == null) {
      view = new SignInViewImpl();
    }
    view.setClientFactory(clientFactory);
    view.setPresenter(this);
    containerWidget.setWidget(view.asWidget());
    view.start();
  }

  
  @Override
  public String mayStop() {
    String s = null;
    if (running == true) {
      s = "Please hold on. This activity is stopping.";
    }
    return s;
  }
  

  /**
   * Navigate to a new Place in the browser
   */
  public void goTo(Place place) {
    clientFactory.getPlaceController().goTo(place);
  }
  
  /**
   * setting this running, and activity move, then a warning dailog will popup
   */
  public void setRunning(boolean running) {
    this.running = running;
  }
}
