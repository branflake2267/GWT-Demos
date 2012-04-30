package com.gonevertical.client.views.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gonevertical.core.client.input.WiseTextBox;
import org.gonevertical.core.client.loading.LoadingWidget;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.WalletDataRequest;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.app.requestfactory.dto.WalletItemDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.widgets.WalletEditItemWidget;
import com.gonevertical.client.views.widgets.WalletListItemWidget.State;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class WalletEditViewImpl extends Composite implements WalletEditView {

  public static enum State {
    VIEW, EDIT;
  }
  private State stateIs;
  
  private Presenter presenter;

  private ClientFactory clientFactory;

  private static WalletEditViewUiBinder uiBinder = GWT.create(WalletEditViewUiBinder.class);
  @UiField VerticalPanel pList;
  @UiField WiseTextBox tbName;
  @UiField PushButton bFinished;
  @UiField PushButton bAdd;
  @UiField FocusPanel pFocusName;
  @UiField LoadingWidget wLoading;
  @UiField VerticalPanel pLoggedIn;
  @UiField VerticalPanel pLoggedOut;

  private WalletDataProxy walletData;

  private boolean savingInProgress;

  private boolean scheduleSave;

  private boolean timerRunning;

  private boolean alreadyInit;

  interface WalletEditViewUiBinder extends UiBinder<Widget, WalletEditViewImpl> {
  }

  public WalletEditViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
        
    tbName.setEditHover(false);
    
    bFinished.setTitle("You can go back to your wallet list by clicking this.");
    tbName.setTitle("Give your wallet a name in this textbox.");
    bAdd.setTitle("Add another item to this wallet.");
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    
    // this is overkill in here, but here for example
    if (alreadyInit == false) {
      //System.out.println("SignViewImpl.setClientFactory(): init");
      clientFactory.getEventBus().addHandler(AuthEvent.TYPE, new AuthEventHandler() {
        public void onAuthEvent(AuthEvent event) {
          Auth e = event.getAuthEvent();
          if (e == Auth.LOGGEDIN) {
            setLoggedIn();
          } else if (e == Auth.LOGGEDOUT) {
            setLoggedOut();
          }
        }
      });
    }
    alreadyInit = true;
  }

  public void setData(WalletDataProxy walletData) {
    this.walletData = walletData;
  }

  public void draw() {
    if (clientFactory.getIsLoggedIn() == null) {
      // wait for login event b/c hasn't happened yet
      
    } else if (clientFactory.getIsLoggedIn() == true) {
      setLoggedIn();
      
    } else if (clientFactory.getIsLoggedIn() == false) { 
      setLoggedOut();
    }
  }
  
  private void setLoggedIn() {
    pLoggedIn.setVisible(true);
    pLoggedOut.setVisible(false);
    
    drawName();
    drawItems();
  }

  /**
   * allow the person to sign in directly from here, so they could load the wallet by id
   */
  private void setLoggedOut() {
    pLoggedIn.setVisible(false);
    pLoggedOut.setVisible(true);
    
    // TODO announce they must sign in
  }
  
  /**
   * when loading data form url
   */
  public void draw(EntityProxyId<WalletDataProxy> walletDataId) {
    wLoading.showLoading(true);
    presenter.setRunning(true);
    Request<WalletDataProxy> request = clientFactory.getRequestFactory().getWalletDataRequest().find(walletDataId).with("items");
    request.fire(new Receiver<WalletDataProxy>() {
      public void onSuccess(WalletDataProxy walletData) {
        wLoading.showLoading(false);
        setData(walletData);
        draw();
        presenter.setRunning(false);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError();
        presenter.setRunning(false);
        super.onFailure(error);
      }
    });
  }

  private void drawItems() {
    pList.clear();
    if (walletData == null || 
        walletData.getItems() == null || 
        walletData.getItems().size() == 0) {
      return;
    }
    
    int i=0;
    Iterator<WalletItemDataProxy> itr = walletData.getItems().iterator();
    while(itr.hasNext()) {
      WalletItemDataProxy d = itr.next();
      add(i, d);
      i++;
    }
  }
  
  private void add() {
    int i = pList.getWidgetCount();
    add(i, null);
  }

  private WalletEditItemWidget add(int i, WalletItemDataProxy itemData) {
    WalletEditItemWidget wItem = new WalletEditItemWidget();
    pList.add(wItem);
    wItem.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        save();
      }
    });
    wItem.setPresenter(presenter);
    wItem.setClientFactory(clientFactory);
    wItem.setLoading(wLoading);
    wItem.setData(i, itemData);
    wItem.draw();
    return wItem;
  }

  private List<WalletItemDataProxy> getItems(WalletDataRequest request) {
    if (pList.getWidgetCount() == 0) {
      return null;
    }
    List<WalletItemDataProxy> items = new ArrayList<WalletItemDataProxy>();
    for (int i=0; i < pList.getWidgetCount(); i++) {
      WalletEditItemWidget wItem = (WalletEditItemWidget) pList.getWidget(i);
      items.add(wItem.getData(request));
    }
    return items;
  }

  private void drawName() {
    if (walletData == null || 
        walletData.getName() == null || 
        walletData.getName().trim().length() == 0) {
      String s = "My Wallet Name";
      tbName.setDefaultText(s);
      return;
    }
    
    String s = walletData.getName();
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    tbName.setText(sh.asString());
  }
  
  private void setName() {
    String s = tbName.getText().trim();
    if (s != null && s.length() == 0) {
      s = "My Wallet";
    }
    if (walletData != null && s.equals(walletData.getName()) == false) {
      save();
    }
  }

  private String getName() {
    String s = tbName.getText().trim();
    if (s.length() == 0) {
      s = null;
    }
    return s;
  }

  private void save() {
    
    // lets make sure one trip at a time happens, otherwise some contention might happen in the datastore
    if (savingInProgress == true) {
      scheduleSave = true;
      return;
    }
    savingInProgress = true;
    wLoading.showLoading(true, "Saving...");
    presenter.setRunning(true);
    
    // get the requestContext
    WalletDataRequest request = clientFactory.getRequestFactory().getWalletDataRequest();
    
    // is it create or edit
    WalletDataProxy data = null;
    if (walletData == null) { // insert|create
      data = request.create(WalletDataProxy.class);
      
    } else { // update|edit
      data = request.edit(walletData);
    }
   
    // persist these
    data.setName(getName());
    data.setItems(getItems(request));
    
    request.persist().using(data).fire(new Receiver<WalletDataProxy>() {
      public void onSuccess(WalletDataProxy walletData) {
        wLoading.showLoading(false);
        process(walletData);
        presenter.setRunning(false);
        
        savingInProgress = false;
        if (scheduleSave == true) {
          scheduleSave = false;
          save();
        }
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError();
        savingInProgress = false;
        scheduleSave = false;
        presenter.setRunning(false);
        super.onFailure(error);
      }
    });
  }
  
  private void process(WalletDataProxy walletData) {
    this.walletData = walletData;
  }
  
  /**
   * on edit, lets wait a moment before moving back to view
   * 
   * @param state
   */
  private void setState(State state) {
    stateIs = state;
    if (timerRunning == true) {
      return;
    }
    if (state == State.VIEW) {
      setStateView();
    } else {
      setStateEdit();
      timerRunning = true;
      Timer t = new Timer() {
        public void run() {
          timerRunning = false;
          if (stateIs == State.EDIT) {
            setState(State.EDIT);
          } else {
            setStateView();
          }
        }
      };
      t.schedule(3000);
    }
  }

  private void setStateView() {
    tbName.setEdit(false);
  }

  private void setStateEdit() {
    tbName.setEdit(true);
  }
  
  @UiHandler("tbName")
  void onTbNameChange(ChangeEvent event) {
    save();
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameTouchStart(TouchStartEvent event) {
    if (stateIs == State.VIEW) {
      setState(State.EDIT);
    } else if (stateIs == State.EDIT) {
      setState(State.VIEW);
    }
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameMouseOut(MouseOutEvent event) {
    setName();
    setState(State.VIEW); 
  }
  
  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    add();
  }

  @UiHandler("bFinished")
  void onBFinishedClick(ClickEvent event) {
    presenter.goTo(new WalletListPlace());
  }

 
}
