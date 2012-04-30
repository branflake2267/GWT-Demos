package com.gonevertical.client.app.activity.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class WalletListPlace extends Place {
  
  /**
   *  I'm not really using the tokenizer here, but its good for the example
   */
  @Prefix("MyWallets")
  public static class Tokenizer implements PlaceTokenizer<WalletListPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - in app activated
     */
    @Override 
    public String getToken(WalletListPlace place) {
      String token = place.getToken();
      return "";
    }

    /**
     * from - url activated
     */
    @Override
    public WalletListPlace getPlace(String token) {
      return new WalletListPlace();
    }

  }
  
  

  private String token;

  public WalletListPlace() {
    //this.token = token;
  }

  public String getToken() {
    return token;
  }

  

}
