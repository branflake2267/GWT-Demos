package com.gonevertical.client.app.activity.places;

import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class WalletEditPlace extends Place {
    
  @Prefix("EditWallet") // historyToken anchor
  public static class Tokenizer implements PlaceTokenizer<WalletEditPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - app activated
     */
    @Override
    public String getToken(WalletEditPlace place) {
      String s = "";
      if (place.getWalletDataId() != null) {
        s = requestFactory.getHistoryToken(place.getWalletDataId());
      } else if (place.getWalletData() != null) {
        s = requestFactory.getHistoryToken(place.getWalletData().stableId());
      } else {
        s = "new"; 
      }
      return s;
    }

    /**
     * from - url activated
     */
    @Override
    public WalletEditPlace getPlace(String token) {
      EntityProxyId<WalletDataProxy> walletDataId = null;
      if (token == null || token.trim().length() == 0 || token.equals("new") == true) {
        // new
      } else {
        walletDataId = requestFactory.<WalletDataProxy> getProxyId(token);
      }
      return new WalletEditPlace(walletDataId);
    }

  }
 
  

  /**
   * from app place move
   */
  private WalletDataProxy walletData;
  
  /**
   * from url place move
   */
  private EntityProxyId<WalletDataProxy> walletDataId;

  /**
   * constructor - from app place move
   * @param walletData
   */
  public WalletEditPlace(WalletDataProxy walletData) {
    this.walletDataId = null;
    this.walletData = walletData;
  }
  
  /**
   * constructor - from url move
   * @param walletDataId
   */
  public WalletEditPlace(EntityProxyId<WalletDataProxy> walletDataId) {
    this.walletDataId = walletDataId;
  }

 
  public WalletDataProxy getWalletData() {
    return walletData;
  }
  
  public EntityProxyId<WalletDataProxy> getWalletDataId() {
    return walletDataId;
  }
  
  
}
