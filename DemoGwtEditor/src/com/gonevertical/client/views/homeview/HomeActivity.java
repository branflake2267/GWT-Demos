package com.gonevertical.client.views.homeview;

import com.gonevertical.client.app.ClientFactory;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class HomeActivity extends AbstractActivity implements HomeView.Presenter {

  private HomeView view;
  
  private ClientFactory clientFactory;

  private boolean running;
  
  public HomeActivity(HomePlace place, ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  /**
   * Invoked by the ActivityManager to start a new Activity
   */
  @Override
  public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
    if (view == null) {
      view = new HomeViewImpl();
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
