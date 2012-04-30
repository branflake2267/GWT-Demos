package com.gonevertical.client.app.activity.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class SignInPlace extends Place {
  
  /** 
   * I'm not really using the tokenizer here, but good for example
   */
  @Prefix("Entry")
  public static class Tokenizer implements PlaceTokenizer<SignInPlace> {

    private RequestFactory requestFactory;

    
    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    @Override
    public String getToken(SignInPlace place) {
      String token = place.getToken();
      return "";
    }

    @Override
    public SignInPlace getPlace(String token) {
      return new SignInPlace();
    }

  }
  
  

  private String token;

  public SignInPlace() {
    //this.token = token;
  }

  public String getToken() {
    return token;
  }

 
}
