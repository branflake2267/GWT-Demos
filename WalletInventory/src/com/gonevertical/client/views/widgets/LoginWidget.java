package com.gonevertical.client.views.widgets;

import org.gonevertical.core.client.images.UiImages;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import org.gonevertical.core.client.loading.LoadingWidget;

public class LoginWidget extends Composite {

  private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);
  @UiField HTML htmlNick;
  @UiField HTML htmlUrl;
  @UiField Image imgHelp;
  @UiField HTML htmlPlusOne;
  @UiField LoadingWidget wLoading;

  private UiImages uiImages = GWT.create(UiImages.class);
  
  private ClientFactory clientFactory;

  private boolean alreadyInit;
  
  private HelpDialog helpDialog;

  interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
  }

  public LoginWidget() {
    initWidget(uiBinder.createAndBindUi(this));

    imgHelp.setResource(uiImages.help());
    imgHelp.setSize("16px", "16px");
    
    addStyleName("app-loginwidget");
    
    drawPlusOne();
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    
    createUser();
  }
  
  /**
   * this will create/lookup a user in the datastore according to the Google Login
   *  (if logged in or has logged in)
   */
  private void createUser() {
    wLoading.showLoading(true, "Checking credentials...&nbsp;");
    Request<UserDataProxy> req = clientFactory.getRequestFactory().getUserDataRequest().createUserData();
    req.fire(new Receiver<UserDataProxy>() {
      public void onSuccess(UserDataProxy data) {
        wLoading.showLoading(false);
        process(data);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError("Oops, I couldn't process credentials...Try reloading this page...&nbsp;");
        super.onFailure(error);
      }
    });
  }

  private void process(UserDataProxy userData) {
    if (userData != null && 
        userData.getId() != null && 
        userData.getId().matches(".*([0-9]+).*") == true) {
      setLoggedIn(userData);
    } else {
      setLoggedOut(userData);
    }
    
    // tell the rest of the app a login event has happened
    clientFactory.setUserData(userData);
  }

  /**
   * lets use the url to show where to login at
   * @param userData
   */
  private void setLoggedOut(UserDataProxy userData) {
    if (userData == null) {
      // this shouldn't happen, b/c we need the urls
      return;
    }

    String url = userData.getLoginUrl();
    String qs = Window.Location.getQueryString();
    String token = History.getToken();
    if (qs != null) {
      url += URL.encode(qs);
    }
    if (token != null && token.length() > 0) {
      url += URL.encode("#" + token);
    }
    
    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Sign In")
    .appendHtmlConstant("</a>");
    htmlUrl.setHTML(builder.toSafeHtml());
  }

  /**
   * logged in, lets go to the wallet list
   */
  private void setLoggedIn(UserDataProxy userData) {
    if (userData == null) {
      return;
    }

    setNick(userData);

    String url = userData.getLogoutUrl();
    String qs = Window.Location.getQueryString();
    if (qs != null) {
      url += URL.encode(qs);
    }

    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Sign Out")
    .appendHtmlConstant("</a>");
    htmlUrl.setHTML(builder.toSafeHtml());
  }

  private void setNick(UserDataProxy userData) {
    if (userData == null) {
      return;
    }

    String nick = userData.getGoogleNickname();

    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendEscaped(nick);

    htmlNick.setHTML(builder.toSafeHtml());
  }

  private void drawHelp() {
    if (helpDialog == null) {
      helpDialog = new HelpDialog();
      helpDialog.setHelpText(getHelpText());
    }
    helpDialog.draw(imgHelp);
  }
  
  private void drawPlusOne() {
    String s = "<g:plusone href=\"https://mywalletinventory.appspot.com\"></g:plusone>";
    htmlPlusOne.setHTML(s);
  }

  /**
   * This is html (table) overkill... made with rich text editor, then copied by inspect element... I copied from another project...
   * TODO come back to this and fix it later
   * @return
   */
  private String getHelpText() {
    String s = "<div><table cellspacing=\"0\" cellpadding=\"0\" style=\"width: 400px; \"><tbody><tr><td align=\"left\" style=\"vertical-align: top; \"><div class=\"gwt-HTML\" style=\"white-space: normal; \">Login with your Google account here:<div><br></div><div></div><div>Don't have one yet, create one here:</div><div>1. <a href=\"https://accounts.google.com/NewAccount?service=mail\">Create a Google gmail account (<b>recommended</b>)</a></div><div>2. <a href=\"http://www.google.com/+/demo/\">Create a Google+ account</a></div><div>3. <a href=\"http://www.google.com/apps/intl/en/business/\">Create a Google business account</a></div><div>4. <a href=\"https://accounts.google.com/NewAccount\">Create a Google account with any email</a></div><div><br></div><div><br></div></div></td></tr><tr><td align=\"left\" style=\"vertical-align: top; \"><table style=\"display: none; \"><colgroup><col></colgroup><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" class=\"gwt-RichTextToolbar\" style=\"width: 100%; \"><tbody><tr><td align=\"left\" style=\"vertical-align: top; \"><table cellspacing=\"0\" cellpadding=\"0\" style=\"width: 100%; \"><tbody><tr><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Bold\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAJElEQVR42mNgGAVDBvwngCk2FJ/YMDVwNAxJN5Am6XAUDBcAAEbIOsY2U3mTAAAAAElFTkSuQmCC) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Italic\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAHt7e4SEhP///yH5BAEAAAMALAAAAAAUABQAAAIgnI+py+0Po0Sg1jmqwOHiGnDeBIoAlgGhZFnoC8fyDBUAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Underline\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAHt7e////////yH5BAEAAAIALAAAAAAUABQAAAIplI+py+0PF5hA0Prm0ZBbnIGe441NCZJiegKBEJgtFdXKhdv6zve+UAAAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Subscript\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAInjI+py+0NwInP0HArllLP7IFaJD5UyVyoAqpranlIJ2O1Rd76zjsFADs=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Superscript\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAInjI+py+2/gIQIGEtr1m1eK3mKJ0ITtoXbtwYhSp2wQ85MWbb6zj8FADs=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Left Justify\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIejI+py+0PE5i01hitxTzoD3QOOIkZeZkLqrbuCxsFADs=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Center\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIdjI+py+0PE5i01oiDtTnuD3QQOIkPyZkNqrbuCxcAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Right Justify\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAH0lEQVR42mNgGAXUBv/JxIPfxaNhOBqGIysMRwHpAADGwEe5v4tWjAAAAABJRU5ErkJggg==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-ToggleButton gwt-ToggleButton-up\" role=\"button\" title=\"Toggle Strikethrough\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAABAP///yH5BAEAAAEALAAAAAAUABQAAAInjI+py+0PHZggVDhPxtd0uVmRFYLUSY1p1K3PVzZhzFTniOf6zjMFADs=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Indent Right\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAP///////wAAACH5BAEAAAIALAAAAAAUABQAAAIjlI+py+0Po5wHVIBzVphqa3zbpUkfeSYdJYCtOLLyTNf2LRQAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Indent Left\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAJEAAAAAAP///////wAAACH5BAEAAAIALAAAAAAUABQAAAIjlI+py+0Po5wIGICzVpgKbX1gd4wTeI1htVKhmnnyTNf2fRQAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Insert Horizontal Rule\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIajI+py+0Po5y0OoCz1mr73H2eRZbmiabqUwAAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Insert Ordered List\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAIAAAAAAAP///yH5BAEAAAEALAAAAAAUABQAAAIjjI+py+0MwIMn2rvkTHZ7132VcZWZSEbommqiW2Lnq7L2vRUAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Insert Unordered List\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAIklEQVR42mNgGPHgPxKmmYH/icD0c+FopIxGymikjALaAAC2dzXLcl5hjgAAAABJRU5ErkJggg==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Insert Image\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAMQAAAAAAP///8rFytPP7uXn7qzG/8/Z75qx4NHg/9zm+01soo2l0qGy0Ojw/yZIgW+HsHuVwcDU9e/2/47B/ZGnvr7H0L/f/a3J4PT5/P3//PDy7+rPz////wAAAAAAAAAAACH5BAEAABwALAAAAAAUABQAAAWfICeOZGmeaHoShKCOxDYIW0ysMc1qjYWxJoGAoJFIEIUJZILQbEyEBsIyUVarloyrRDBcv4lL1gAlfC0WQoRxwdRMhkYCHbksKAsI61YSYCYLdwyBChARGlskAhp3EAsHDBAPhYhldhAQeJMKDw1PJTUFm5IKpQ4XQCQsBqWmnAsWEqkwBAmtBwkIBgYsQkEEbBUGAgY1s317L8rLzCIhADs=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Create Link\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/gif;base64,R0lGODlhFAAUAKIAAAAAzGFs5X9/5f///////////////////yH5BAEAAAQALAAAAAAUABQAAAM7SLrc/jDKSSsNIGelc2jYIIrZSH4LQHYsyagAoZpDDL8rq7lLOAoZgQmA6rE4naJlWdE5NcyodEqlJAAAOw==) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Remove Link\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAVklEQVR42mNgGAWDGyTmPP3PwHAGDaMDVHmIHjyGoQNUQ7HL4zD0DJohhF2I3VI0A3FI4gBnCBtINRdiC8P6+qcYYQgSIzIMqRzLQwGc+U8ZHgWDFgAAXRvmgdN64GsAAAAASUVORK5CYII=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" title=\"Remove Formatting\" aria-pressed=\"false\"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><img onload=\"this.__gwtLastUnhandledEvent=&quot;load&quot;;\" src=\"https://studentlearningplan.appspot.com/gonevertical/clear.cache.gif\" style=\"width: 20px; height: 20px; background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAABB0lEQVR42tWUsQ2DMBBFyQSICShCTwEdlijooECiCR0jMAIjMAIjMAILINHSMQIj/OTAJpAQTEApctKXsDk9f9+drCh/G03ToKoqqXYDVVWl5E3xHHnwk5GmKbquQxiGE4T+kXtd18W+PAiUJMk8+c1RnucoigK76ye+y7Kc3M0d9X0P0tfNIbcCuNvRVlCtBJDqeQrGAYNM0zzvjgovgPzq5+J1XE7BeAdXBxjj/nNtGPLDsiybgK7rYg4TEjBYFuA4+NgIDlhoCb0AmoYRZq/D2rZFXde7tQmjYIxJHwMhGFfApmsywPMAPzjucHBmP5yx0RkCH4gi4BYfnwJytljHMX72MN8BS744G4gNMaUAAAAASUVORK5CYII=) no-repeat 0px 0px;\" border=\"0\" class=\"gwt-Image\"></div></td></tr></tbody></table></td></tr><tr><td align=\"left\" style=\"vertical-align: top; \"><table cellspacing=\"0\" cellpadding=\"0\" style=\"width: 100%; \"><tbody><tr><td align=\"left\" style=\"vertical-align: top; \"><select class=\"gwt-ListBox\" size=\"1\"><option value=\"Background\">Background</option><option value=\"white\">White</option><option value=\"black\">Black</option><option value=\"red\">Red</option><option value=\"green\">Green</option><option value=\"yellow\">Yellow</option><option value=\"blue\">Blue</option></select></td><td align=\"left\" style=\"vertical-align: top; \"><select class=\"gwt-ListBox\" size=\"1\"><option value=\"Foreground\">Foreground</option><option value=\"white\">White</option><option value=\"black\">Black</option><option value=\"red\">Red</option><option value=\"green\">Green</option><option value=\"yellow\">Yellow</option><option value=\"blue\">Blue</option></select></td><td align=\"left\" style=\"vertical-align: top; \"><select class=\"gwt-ListBox\" size=\"1\"><option value=\"\">Font</option><option value=\"\">Normal</option><option value=\"Times New Roman\">Times New Roman</option><option value=\"Arial\">Arial</option><option value=\"Courier New\">Courier New</option><option value=\"Georgia\">Georgia</option><option value=\"Trebuchet\">Trebuchet</option><option value=\"Verdana\">Verdana</option><option value=\"Ubuntu\">Ubuntu</option></select></td><td align=\"left\" style=\"vertical-align: top; \"><select class=\"gwt-ListBox\" size=\"1\"><option value=\"Size\">Size</option><option value=\"XX-Small\">XX-Small</option><option value=\"X-Small\">X-Small</option><option value=\"Small\">Small</option><option value=\"Medium\">Medium</option><option value=\"Large\">Large</option><option value=\"X-Large\">X-Large</option><option value=\"XX-Large\">XX-Large</option></select></td></tr></tbody></table></td></tr></tbody></table></td></tr><tr><td><iframe class=\"gwt-RichTextArea hasRichTextToolbar\" style=\"width: 100%; height: 14em; \"></iframe></td></tr><tr><td>&nbsp;</td></tr></tbody></table></td></tr><tr><td align=\"left\" style=\"vertical-align: top; \"><table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" aria-pressed=\"false\" style=\"display: none; \"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><div class=\"html-face\">Close</div></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" aria-pressed=\"false\" style=\"display: none; \"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><div class=\"html-face\">Edit</div></div></td><td align=\"left\" style=\"vertical-align: top; \"><div tabindex=\"0\" class=\"gwt-PushButton gwt-PushButton-up\" role=\"button\" aria-pressed=\"false\" style=\"display: none; \"><input type=\"text\" tabindex=\"-1\" style=\"opacity: 0; height: 1px; width: 1px; z-index: -1; overflow-x: hidden; overflow-y: hidden; position: absolute; \"><div class=\"html-face\">Save</div></div></td></tr></tbody></table></td></tr></tbody></table></div>";    
    return s;
  }

  @UiHandler("imgHelp")
  void onImgHelpClick(ClickEvent event) {
    drawHelp();
  }
  @UiHandler("imgHelp")
  void onImgHelpTouchStart(TouchStartEvent event) {
    drawHelp();
  }
  @UiHandler("imgHelp")
  void onImgHelpMouseOver(MouseOverEvent event) {
    drawHelp();
  }
  
  
  
  
  
  
  
  
  
  
  

 
  
  
  
  
}
