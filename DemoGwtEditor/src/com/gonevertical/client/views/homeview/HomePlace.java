package com.gonevertical.client.views.homeview;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class HomePlace extends Place {
  
  @Prefix("home")
  public static class Tokenizer implements PlaceTokenizer<HomePlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - app activated
     */
    @Override
    public String getToken(HomePlace place) {
      //String token = place.getToken();
      return "";
    }

    /**
     * from - url activated
     */
    @Override
    public HomePlace getPlace(String token) {
      return new HomePlace();
    }

  }
  
  

  private String token;

  public HomePlace() {
    //this.token = token;
  }

  public String getToken() {
    return token;
  }

 
}
