package com.gonevertical.client.views.peoplelist;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class PeopleListPlace extends Place {
  
  @Prefix("peoplelist")
  public static class Tokenizer implements PlaceTokenizer<PeopleListPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - app activated
     */
    @Override
    public String getToken(PeopleListPlace place) {
      // String token = place.getToken();
      return "";
    }

    /**
     * from - url activated
     */
    @Override
    public PeopleListPlace getPlace(String token) {
      return new PeopleListPlace();
    }

  }
  
  private String token;

  public PeopleListPlace() {
    //this.token = token;
  }

  public String getToken() {
    return token;
  }

 
}
