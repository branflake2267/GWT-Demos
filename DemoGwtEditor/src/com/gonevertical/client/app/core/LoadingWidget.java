package com.gonevertical.client.app.core;

import com.gonevertical.client.app.core.images.UiImages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class LoadingWidget extends Composite {
  
  private static final String ERROR_MESSAGE = "Oops, a request error occured. Try again.";
  private static final int DEFAULT_DELAY = 4000;
  
  private UiImages uiImages = GWT.create(UiImages.class);

  private static LoadingWidgetUiBinder uiBinder = GWT.create(LoadingWidgetUiBinder.class);
  @UiField Image imgLoading;
  @UiField HorizontalPanel pWidget;
  @UiField HTML htmlMessage;
  @UiField HTML htmlSpacer;

  interface LoadingWidgetUiBinder extends UiBinder<Widget, LoadingWidget> {
  }
  
  /**
   * track hide timer
   */
  private boolean isRunning;

  public LoadingWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    
    imgLoading.setResource(uiImages.loading());
    imgLoading.setSize("20px", "20px");

    setStyleName("gv-core-loadingwidget");
    
    // set default state
    showLoading(false);
  }
  
  public void showLoading(boolean show) {
    setVisible(show);
    setMessage(null);
  }
  
  public void showLoading(boolean show, String message) {
    showLoading(show);
    setMessage(message);
  }
  
  public void hideTimed(int delayMillis, String message) {
    showLoading(true, message);
    hideTimed(delayMillis);
  }
  
  public void showError(int delayMillis) { 
    showError(delayMillis, ERROR_MESSAGE);
  }
  
  public void showError() { 
    showError(DEFAULT_DELAY, ERROR_MESSAGE);
  }
  
  public void showError(String message) { 
    showError(DEFAULT_DELAY, message);
  }
  
  public void showError(int delayMillis, String message) { 
    showLoading(true, message);
    hideTimed(delayMillis);
    addStyleName("gv-core-loadingwidget-error");
  }
  
  public void hideTimed(int delayMillis) {
    if (delayMillis == 0) {
      delayMillis = DEFAULT_DELAY; // default 4 secs if 0
    }
    if (isRunning == true) { // make sure I don't have concurrent timers
      return;
    }
    isRunning = true;
    Timer t = new Timer() {
      public void run() {
        isRunning = false;
        showLoading(false);
        removeStyleName("gv-core-loadingwidget-error"); // not needed everytime...
      }
    };
    t.schedule(delayMillis);
  }
  
  private void setMessage(String s) {
    if (s == null || s.trim().length() == 0) {
      htmlMessage.setHTML("");
      htmlSpacer.setVisible(false);
      htmlMessage.setVisible(false);
      return;
    }
    htmlMessage.setHTML(SimpleHtmlSanitizer.sanitizeHtml(s));
    htmlSpacer.setVisible(true);
    htmlMessage.setVisible(true);
  }
  
  public void hide() {
    showLoading(false);
  }

}
