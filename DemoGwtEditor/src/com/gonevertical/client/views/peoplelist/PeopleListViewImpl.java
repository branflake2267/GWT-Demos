package com.gonevertical.client.views.peoplelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.core.LoadingWidget;
import com.gonevertical.client.app.requestfactory.PeopleDataRequest;
import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.PeopleListFilterProxy;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.views.peopleedit.PeopleEditPlace;
import com.gonevertical.client.views.peoplelist.table.NameFirstColumn;
import com.gonevertical.client.views.peoplelist.table.NameLastColumn;
import com.gonevertical.client.views.widgets.paging.PageChangeEvent;
import com.gonevertical.client.views.widgets.paging.PageChangeEventHandler;
import com.gonevertical.client.views.widgets.paging.Paging;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.user.client.ui.TextBox;
import com.sun.tools.javac.code.Attribute.Array;

public class PeopleListViewImpl extends Composite implements PeopleListView {

  public static final int PAGE_SIZE = 5;
  
  private static PeopleListViewImplUiBinder uiBinder = GWT.create(PeopleListViewImplUiBinder.class);
  @UiField 
  LoadingWidget wLoading;
  
  @UiField 
  HTML htmlSignIn;
  
  @UiField 
  PushButton bAdd;
  
  @UiField 
  FlowPanel pContent;

  @UiField(provided=true) 
  DataGrid<PeopleDataProxy> grid = new DataGrid<PeopleDataProxy>();
  
  @UiField 
  Paging wPage;
  
  @UiField 
  TextBox tbSearch;
  
  @UiField 
  PushButton bSearch;

  interface PeopleListViewImplUiBinder extends UiBinder<Widget, PeopleListViewImpl> {
  }

  private Presenter presenter;

  private ClientFactory clientFactory;

  private boolean alreadyInit;

  private final SingleSelectionModel<PeopleDataProxy> selectionModel = new SingleSelectionModel<PeopleDataProxy>();

  public PeopleListViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

    setupGrid();
    
    setupPaging();
  }

  private void setupPaging() {
    wPage.setDisplayChangeLimit(false);
    wPage.addPageChangeHandler(new PageChangeEventHandler() {
      public void onEditEvent(PageChangeEvent event) {
        fetch();
      }
    });
  }

  private void setupGrid() {
    Column<PeopleDataProxy, String> nameFirstColumn = new NameFirstColumn();
    grid.addColumn(nameFirstColumn, "First Name");

    Column<PeopleDataProxy, String> nameLastColumn = new NameLastColumn();
    grid.addColumn(nameLastColumn, "Last Name");
    
    grid.setSelectionModel(selectionModel);
    selectionModel.addSelectionChangeHandler(new Handler() {
      public void onSelectionChange(SelectionChangeEvent event) {
        // instead of passing the object in, lets pass the id, so then I'll look the entire object up including the owned children todos
        // this way, it is a bit simpler to deal with decoupling
        EntityProxyId<PeopleDataProxy> o = (EntityProxyId<PeopleDataProxy>) selectionModel.getSelectedObject().stableId();
        presenter.goTo(new PeopleEditPlace(o));
      }
    });
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
          setLogginState(e, event.getUserData());
        }
      });
    }
    alreadyInit = true;
  }

  public void start() {
    wLoading.showLoading(true);

    if (clientFactory.getIsLoggedIn() == null) {
      // wait for login event b/c hasn't happened yet

    } else if (clientFactory.getIsLoggedIn() == true) {
      setLoggedIn();

    } else if (clientFactory.getIsLoggedIn() == false) { 
      setLoggedOut(clientFactory.getUserData());
    }
  }

  private void setLogginState(Auth auth, UserDataProxy userData) {
    if (auth == Auth.LOGGEDIN) {
      htmlSignIn.setVisible(false);
      pContent.setVisible(true);
      setLoggedIn();

    } else if (auth == Auth.LOGGEDOUT) {
      htmlSignIn.setVisible(true);
      pContent.setVisible(false);
      setLoggedOut(userData);
    }
  }

  private void setLoggedIn() {
    wLoading.showLoading(false);

    wPage.setCounts(0, PAGE_SIZE, null);
    
    fetch();
    fetchTotalCount();
  }

  private void fetchTotalCount() {
    
    PeopleDataRequest request = clientFactory.getRequestFactory().getPeopleDataRequest();
    
    PeopleListFilterProxy filter = request.create(PeopleListFilterProxy.class);
    filter.setSearch(getSearch());
    
    Request<Long> requestLoad = request.findCount(filter);
    requestLoad.fire(new Receiver<Long>() {
      public void onSuccess(Long total) {
        setTotal(total);
      }
    });
  }

  private void setTotal(Long total) {
    if (total == null) {
      return;
    }
    wPage.setTotal(total);
  }

  private void fetch() {
    long[] range = wPage.getRange();
    if (range == null || range[1] <= 1) { // total is a slight delay, so lets deal with that. 
      range[0] = 0;
      range[1] = PAGE_SIZE;
    }
    
    System.out.println("range start=" + range[0] + " end=" + range[1]);
    
    PeopleDataRequest request = clientFactory.getRequestFactory().getPeopleDataRequest();
    
    PeopleListFilterProxy filter = request.create(PeopleListFilterProxy.class);
    filter.setStart(range[0]);
    filter.setEnd(range[1]);
    filter.setSearch(getSearch());
    
    Request<List<PeopleDataProxy>> loadRequest = request.findPeopleData(filter).with("todos");
    loadRequest.fire(new Receiver<List<PeopleDataProxy>>() {
      public void onSuccess(List<PeopleDataProxy> response) {
        process(response);
      }
      public void onFailure(ServerFailure error) {
        // TODO ...
        super.onFailure(error);
      }
    });
  }

  public void process(List<PeopleDataProxy> response) {
    grid.setRowData(response);
  }

  /**
   * lets use the url to show where to login at
   * @param userData
   */
  private void setLoggedOut(UserDataProxy userData) {
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

  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    presenter.goTo(new PeopleEditPlace(null));
  }
  
  @UiHandler("bSearch")
  void onBSearchClick(ClickEvent event) {
    fetch();
    fetchTotalCount();
  }
  
  private List<String> getSearch() {
    String s = tbSearch.getValue().toLowerCase().trim();
    if (s == null || s.trim().length() == 0) {
      return null;
    }
    if (s.contains(" ") == false) {
      String[] a = new String[1];
      a[0] = s;
      return Arrays.asList(a);
    }
    String[] a = s.split(" ");
    if (a == null || a.length == 0) {
      return null;
    }
    return Arrays.asList(a);
  }
}
