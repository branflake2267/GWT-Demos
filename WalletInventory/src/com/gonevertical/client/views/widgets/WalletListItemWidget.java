package com.gonevertical.client.views.widgets;

import org.gonevertical.core.client.dialog.bool.BooleanDialog;
import org.gonevertical.core.client.dialog.bool.BooleanEvent;
import org.gonevertical.core.client.dialog.bool.BooleanEvent.Selected;
import org.gonevertical.core.client.dialog.bool.BooleanEventHandler;
import org.gonevertical.core.client.input.WiseTextBox;
import org.gonevertical.core.client.loading.LoadingWidget;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.requestfactory.WalletDataRequest;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.views.WalletListView.Presenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class WalletListItemWidget extends Composite {

  /**
   * widget state View | Edit
   */
  public static enum State {
    VIEW, EDIT;
  }
  private State stateIs;

  private Presenter presenter;

  private ClientFactory clientFactory;

  private static WalletListItemWidgetUiBinder uiBinder = GWT.create(WalletListItemWidgetUiBinder.class);
  @UiField WiseTextBox tbName;
  @UiField FocusPanel pFocus;
  @UiField PushButton bDelete;
  @UiField PushButton bView;

  private WalletDataProxy walletData;

  private BooleanDialog wconfirm;

  private int index;

  private LoadingWidget wLoading;
  
  private int width;

  private boolean timerRunning;

  interface WalletListItemWidgetUiBinder extends UiBinder<Widget, WalletListItemWidget> {
  }

  public WalletListItemWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    
    setState(State.VIEW);
    
    tbName.setEditHover(false);
    
    setTitle("Enter a name for your wallet, purse or handbag.");
    bDelete.setTitle("Delete this wallet and its contents forever.");
    bView.setTitle("See and edit what you have in your wallet.");
  }

  public void setData(int index, WalletDataProxy walletDataProxy) {
    this.index = index;
    // TODO set style depending on i
    this.walletData = walletDataProxy; 
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }
  
  public void setLoadingWidget(LoadingWidget wLoading) {
    this.wLoading = wLoading;
  }

  public void draw() {

    setState(State.VIEW);

    drawName();
  }

  private void drawName() {
    if (walletData == null || 
        walletData.getName() == null || 
        walletData.getName().trim().length() == 0) {
      String s = index + " My Wallet";
      tbName.setText(s);
      return;
    }
    
    String s = walletData.getName();
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    tbName.setText(sh.asString());
  }

  public void edit() {

    // goto to edit view
    presenter.goTo(new WalletEditPlace(walletData));
  }

  private String getName() {
    String s = tbName.getText().trim();
    if (s.length() == 0) {
      s = null;
    }
    return s;
  }

  private void save() {
    
    // only update in this view
    if (walletData == null) {
      return;
    }
    
    WalletDataRequest request = clientFactory.getRequestFactory().getWalletDataRequest();
    walletData = request.edit(walletData);
    
    walletData.setName(getName());
    
    presenter.setRunning(true);
    wLoading.showLoading(true);
    Request<WalletDataProxy> req = request.persist().using(walletData);
    req.fire(new Receiver<WalletDataProxy>() {
      public void onSuccess(WalletDataProxy walletData) {
        wLoading.showLoading(false);
        process(walletData);
        presenter.setRunning(false);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError();
        presenter.setRunning(false);
        super.onFailure(error);
      }
    });
  }
  
  private void process(WalletDataProxy walletData) {
    this.walletData = walletData;
  }

  private void delete() {
    if (wconfirm == null) {
      wconfirm = new BooleanDialog("Are you sure you want to delete this?");
      wconfirm.addSelectionHandler(new BooleanEventHandler() {
        public void onBooleanEvent(BooleanEvent event) {
          if (event.getBooleanEvent() == Selected.TRUE) {
            deleteIt();
          } else if (event.getBooleanEvent() == Selected.FALSE) {
            // do nothing
          }
        }
      });
    }
    wconfirm.center();
  }

  private void deleteIt() {
    if (walletData == null || walletData.getId() == null) {
      removeFromParent();
      return;
    }
    wLoading.showLoading(true);
    presenter.setRunning(true);
    Request<Boolean> req = clientFactory.getRequestFactory().getWalletDataRequest().deleteWalletData(walletData.getId());
    req.fire(new Receiver<Boolean>() {
      public void onSuccess(Boolean data) {
        wLoading.showLoading(false);
        if (data != null && data.booleanValue() == true) {
          removeFromParent();
        } else {
          wLoading.showError("Oops, I couldn't delete that for some reason.");
        }
        presenter.setRunning(false);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError();
        presenter.setRunning(false);
        super.onFailure(error);
      }
    });
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
    bDelete.setVisible(false);
  }

  private void setStateEdit() {
    tbName.setEdit(true);
    bDelete.setVisible(true);
    
    // b/c delete hides, it shrinks, set it
    width = pFocus.getOffsetWidth();
    pFocus.setWidth(width + "px");
  }

  @UiHandler("tbName")
  public void onTbNameTouchStart(TouchStartEvent event) {
    if (stateIs == State.VIEW) {
      setState(State.EDIT);
    } else if (stateIs == State.EDIT) {
      setState(State.VIEW);
    }
  }

  @UiHandler("tbName")
  public void onTbNameTouchEnd(TouchEndEvent event) {
  }

  @UiHandler("tbName")
  public void onTbNameMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }

  @UiHandler("tbName")
  public void onTbNameMouseOut(MouseOutEvent event) {
    setState(State.VIEW);
  }

  @UiHandler("tbName")
  void onTbNameChange(ChangeEvent event) {
    save();
  }

  @UiHandler("bDelete")
  public void onBDeleteClick(ClickEvent event) {
    delete();
  }

  @UiHandler("bView")
  public void onBViewClick(ClickEvent event) {
    edit();
  }

  @UiHandler("bView")
  void onBViewMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }
  @UiHandler("bView")
  void onBViewMouseOut(MouseOutEvent event) {
    setState(State.VIEW);
  }
}
