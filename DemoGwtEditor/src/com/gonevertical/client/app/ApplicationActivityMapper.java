package com.gonevertical.client.app;

import com.gonevertical.client.views.homeview.HomeActivity;
import com.gonevertical.client.views.homeview.HomePlace;
import com.gonevertical.client.views.peopleedit.PeopleEditActivity;
import com.gonevertical.client.views.peopleedit.PeopleEditPlace;
import com.gonevertical.client.views.peoplelist.PeopleListActivity;
import com.gonevertical.client.views.peoplelist.PeopleListPlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class ApplicationActivityMapper implements ActivityMapper {
  
  private ClientFactory clientFactory;

  /**
   * AppActivityMapper associates each Place with its corresponding
   * {@link Activity}
   * 
   * @param clientFactory Factory to be passed to activities
   * @param walleteditview 
   * @param walletlistview 
   * @param signinview 
   */
  public ApplicationActivityMapper(ClientFactory clientFactory) {
    super();
    this.clientFactory = clientFactory;
  }
  
  /**
   * Map each Place to its corresponding Activity. 
   */
  @Override
  public Activity getActivity(Place place) {

    if (place instanceof HomePlace) {
      return new HomeActivity((HomePlace) place, clientFactory);
      
    } else if (place instanceof PeopleListPlace) {
      return new PeopleListActivity((PeopleListPlace) place, clientFactory);
      
    } else if (place instanceof PeopleEditPlace) {
      return new PeopleEditActivity((PeopleEditPlace) place, clientFactory);
      
    } else {
      return null;
    }
  }

}
