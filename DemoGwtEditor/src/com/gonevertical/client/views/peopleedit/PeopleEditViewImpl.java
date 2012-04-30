package com.gonevertical.client.views.peopleedit;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.core.LoadingWidget;
import com.gonevertical.client.app.events.EditEvent;
import com.gonevertical.client.app.events.EditEvent.Edit;
import com.gonevertical.client.app.events.EditEventHandler;
import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.views.peopleedit.editor.EditPersonWorkFlow;
import com.gonevertical.client.views.peoplelist.PeopleListPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class PeopleEditViewImpl extends Composite implements PeopleEditView {

  private static EditViewImplUiBinder uiBinder = GWT.create(EditViewImplUiBinder.class);
  @UiField LoadingWidget wLoading;
  @UiField HTML htmlSignIn;
  @UiField FlowPanel pEdit;

  interface EditViewImplUiBinder extends UiBinder<Widget, PeopleEditViewImpl> {}
  
  private Presenter presenter;

  private ClientFactory clientFactory;
  
  private boolean alreadyInit;
  
  private EditPersonWorkFlow editorFlow;
  
  private PeopleDataProxy peopleDataProxy;
  
  private EntityProxyId<PeopleDataProxy> id;

  public PeopleEditViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
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
          setState(e, event.getUserData());
        }
      });
    }
    alreadyInit = true;
  }

  /**
   * presenter starts this
   *    this probably could be put in the presenter
   */
  public void start(EntityProxyId<PeopleDataProxy> id) {
    this.id = id;
    peopleDataProxy = null;
    
    start();
  }
  
  private void start() {
    wLoading.showLoading(true);
    
    if (clientFactory.getIsLoggedIn() == null) {
      // wait for login event b/c hasn't happened yet
      
    } else if (clientFactory.getIsLoggedIn() == true) {
      setLoggedIn();
      
    } else if (clientFactory.getIsLoggedIn() == false) { 
      setLoggedOut(clientFactory.getUserData());
    }
  }

  private void setState(Auth auth, UserDataProxy userData) {
    if (auth == Auth.LOGGEDIN) {
      setLoggedIn();
    } else if (auth == Auth.LOGGEDOUT) {
      setLoggedOut(userData);
    }
  }
  
  private void setLoggedIn() {
    wLoading.showLoading(false);
    
    if (id == null) {
      drawWorkFlowEditor();
    } else {
      findId(id);
    }
  }
  
  /**
   * load the data, and children todos. 
   *   note todos must be annotated on the server side. 
   * @param id
   */
  private void findId(EntityProxyId<PeopleDataProxy> id) {
    Request<PeopleDataProxy> req = clientFactory.getRequestFactory().getPeopleDataRequest().find(id).with("todos");
    req.fire(new Receiver<PeopleDataProxy>() {
      public void onSuccess(PeopleDataProxy response) {
        PeopleEditViewImpl.this.peopleDataProxy = response; // as in this.peopleDataProxy
        drawWorkFlowEditor();
      }
    });
  }

  /**
   * setup the editor
   */
  private void drawWorkFlowEditor() {
    if (editorFlow == null) {
      editorFlow = new EditPersonWorkFlow(clientFactory, presenter);
      pEdit.add(editorFlow);
      editorFlow.addEditHandler(new EditEventHandler<PeopleDataProxy>() {
        public void onEditEvent(EditEvent<PeopleDataProxy> event) {
          setEditEvent(event);
        }
      });
    }
    editorFlow.edit(peopleDataProxy);
  }

  private void setEditEvent(EditEvent<PeopleDataProxy> event) {
    if (event.getEvent() == Edit.SAVING) {
      presenter.setRunning(true);
      
    } else if (event.getEvent() == Edit.SAVED) {
      presenter.setRunning(false);
      
    } else if (event.getEvent() == Edit.FINISHED) {
      presenter.goTo(new PeopleListPlace());
    }
  }

  /**
   * lets use the url to show where to login at
   * @param userData
   */
  private void setLoggedOut(UserDataProxy userData) {
    peopleDataProxy = null;
    id = null;
    
    wLoading.showLoading(false);
    if (userData == null) {
      // this shouldn't happen, b/c we need the urls
      return;
    }
   
    String url = userData.getLoginUrl();
    String qs = Window.Location.getQueryString();
    if (qs != null) {
      url += URL.encode(qs);
    }
    
    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Please Sign In")
    .appendHtmlConstant("</a>");
    htmlSignIn.setHTML(builder.toSafeHtml());
  }

}
