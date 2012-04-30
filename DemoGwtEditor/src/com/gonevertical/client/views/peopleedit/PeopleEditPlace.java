package com.gonevertical.client.views.peopleedit;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class PeopleEditPlace extends Place {
  
  @Prefix("peopleedit")
  public static class Tokenizer implements PlaceTokenizer<PeopleEditPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - app activated
     */
    @Override
    public String getToken(PeopleEditPlace place) {
      String s = "";
      if (place.getId() != null) {
        s = requestFactory.getHistoryToken(place.getId());
        
      } else {
        s = "new"; 
      }
      return s;
    }

    /**
     * from - url activated
     */
    @Override
    public PeopleEditPlace getPlace(String token) {
      EntityProxyId<PeopleDataProxy> id = null;
      if (token == null || token.trim().length() == 0 || token.equals("new") == true) {
        // new
      } else {
        id = requestFactory.<PeopleDataProxy> getProxyId(token);
      }
      return new PeopleEditPlace(id);
    }

  }
  
  private String token;
  
  // from url
  private EntityProxyId<PeopleDataProxy> proxyId; 

  
  // from url
  public PeopleEditPlace(EntityProxyId<PeopleDataProxy> proxyId) {
    this.proxyId = proxyId;
  }

  public String getToken() {
    return token;
  }
  
  public EntityProxyId<PeopleDataProxy> getId() {
    return proxyId;
  }
 
}
