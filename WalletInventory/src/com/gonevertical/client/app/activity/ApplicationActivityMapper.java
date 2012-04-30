package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
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

    if (place instanceof SignInPlace) {
      return new SignInActivity((SignInPlace) place, clientFactory);
      
    } else if (place instanceof WalletListPlace) {
      return new WalletListActivity((WalletListPlace) place, clientFactory);
      
    } else if (place instanceof WalletEditPlace) {
      return new WalletEditActivity((WalletEditPlace) place, clientFactory);
      
    } else {
      return null;
    }
  }

}
