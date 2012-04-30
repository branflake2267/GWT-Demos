package com.gonevertical.client.views.widgets.paging;

import com.gonevertical.client.app.core.LoadingWidget;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Paging extends Composite implements ClickHandler, ChangeHandler {

  private VerticalPanel pWidget = new VerticalPanel();

  private HorizontalPanel pPrevPages = new HorizontalPanel();
  private HorizontalPanel pNextPages = new HorizontalPanel();
  private FlowPanel pOnPage = new FlowPanel();
  private FlowPanel pTotalPages = new FlowPanel();
  private HorizontalPanel pTotal = new HorizontalPanel();
  private LoadingWidget wLoading = new LoadingWidget();

  // buttons
  private PushButton bPrev = new PushButton("<");
  private PushButton bNext = new PushButton(">");
  private PushButton bStart = new PushButton();
  private PushButton bEnd = new PushButton();

  private ListBox lbLimit = new ListBox();


  private int onShard = 0;

  /**
   * calculate shards here
   */
  private Sharding shard = new Sharding();

  /**
   * constructor - init widget
   */
  public Paging() {
    initWidget(pWidget);

    bStart.setTitle("Goto to starting page");
    bPrev.setTitle("Previous page");
    bNext.setTitle("Next page");
    bEnd.setTitle("Goto last page");

    pOnPage.setTitle("Current page");
    pTotalPages.setTitle("Total pages");
    lbLimit.setTitle("Display this many records at a time in a page");

    HorizontalPanel hp = new HorizontalPanel();
    pWidget.add(hp);

    hp.add(bStart);
    hp.add(new HTML("&nbsp;"));
    hp.add(bPrev);
    hp.add(new HTML("&nbsp;"));
    hp.add(pPrevPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(pOnPage);
    hp.add(new HTML("&nbsp;"));
    hp.add(pNextPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(bNext);
    hp.add(new HTML("&nbsp;"));
    hp.add(bEnd);
    hp.add(new HTML("&nbsp;"));
    hp.add(pTotalPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(lbLimit);
    hp.add(new HTML("&nbsp;"));
    hp.add(pTotal);
    hp.add(new HTML("&nbsp;"));
    hp.add(wLoading);

    // observe
    bPrev.addClickHandler(this);
    bNext.addClickHandler(this);
    bStart.addClickHandler(this);
    bEnd.addClickHandler(this);
    lbLimit.addChangeHandler(this);

    // Style
    pWidget.setStyleName("Page");

    bStart.addStyleName("Page-Button");
    bPrev.addStyleName("Page-Button");
    bNext.addStyleName("Page-Button");
    bEnd.addStyleName("Page-Button");
    lbLimit.addStyleName("Page-LbLimit");


    hp.setCellVerticalAlignment(lbLimit, VerticalPanel.ALIGN_MIDDLE);
    hp.setCellVerticalAlignment(pTotal, VerticalPanel.ALIGN_MIDDLE);
    hp.setCellVerticalAlignment(wLoading, VerticalPanel.ALIGN_MIDDLE);

    pWidget.setVisible(false);

    drawLimitChoices();
  }

  public long getOffset() {
    return shard.getOffset();
  }

  public long getLimit() {
    return shard.getLimit();
  }

  public long[] getRange() {
    return shard.getRange(onShard);
  }

  /**
   * set the widget counts at the bottom
   * 
   * @param total
   */
  public void setTotal(long total) {
    shard.setTotal(total);

    if (total == 0) {
      setVisible(false);
      return;
    }
    
    drawInit();
    
    setVisible(true);
  }

  /**
   * set the widget counts at the bottom
   * 
   * @param offset
   * @param limit
   */
  public void setCounts(long offset, int limit, Long finish) {
    shard.setLimit(offset, limit, finish);
  }

  /**
   * beginning init render
   */
  private void drawInit() {

    // set to the beginning
    onShard = 0;
    
    setFirstPage();

    displayPrev();
    displayNext();

    drawPrevPages();
    drawNextPages();
    
    drawOnPage();
    
    drawTotalPages();

    setLimitChoicesAt();

    drawTotal();

    if (shard.getShards() <= 1) {
      pWidget.setVisible(false);
    } else if (shard.getShards() > 1) {
      pWidget.setVisible(true);
    } else {
      pWidget.setVisible(false);
    }
  }
  
  private void drawMove() {
    displayPrev();
    displayNext();

    drawPrevPages();
    drawNextPages();
    
    drawOnPage();
  }

  private void drawTotal() {
    pTotal.clear();
    pTotal.add(new HTML("Total: " + Long.toString(shard.getTotal())));
  }

  /**
   * figure out what page 
   */
  private void divideOnPage() {
    onShard = (int) (shard.getOffset() / shard.getLimit());
  }

  private void setFirstPage() {
    bStart.setText("1");
  }

  private void displayPrev() {
    if (onShard == 0) {
      bPrev.setEnabled(false);
      bStart.setEnabled(false);
      
    } else if (onShard > 0) {
      bPrev.setEnabled(true);
      bStart.setEnabled(true);
    }
  }

  private void displayNext() {

    if (onShard == shard.getShards()-1) {
      bNext.setEnabled(false);
      bEnd.setEnabled(false);

    } else if (shard.getShards() > 0) {
      bNext.setEnabled(true);
      bEnd.setEnabled(true);

    } else {
      bNext.setEnabled(false);
      bEnd.setEnabled(false);
    }

  }

  private void drawPrevPages() {
    pPrevPages.clear();

    for(int i = onShard - 9; i < onShard; i++) {
      if (i >= 0) {
        drawPageButton(pPrevPages, i);
      }
    }
  }

  private void drawNextPages() {
    pNextPages.clear();
    for(int i = onShard + 1; i < onShard + 10; i++) {
      if (i == shard.getShards() + 1) {
        break;
      }
      if (shard.getShards() > i) {
        drawPageButton(pNextPages, i);
      }
    }
  }

  private void drawPageButton(HorizontalPanel hp, int i) {
    int p = i+1;
    PageButton b = new PageButton(p);
    hp.add(b);
    b.addStyleName("Page-Button");
    b.setTitle("Goto Page " + p);
    
    b.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        PageButton b = (PageButton) event.getSource();
        setPage(b.getPage());
      }
    });
  }

  private void drawTotalPages() {
    String p = Integer.toString(shard.getShards());
    bEnd.setText(p);
  }

  private void drawOnPage() {
    pOnPage.clear();
    String p = Integer.toString(onShard+1);
    pOnPage.add(new HTML("<b>" + p + "</b>"));
  }

  private void setPage(int onPage) {
    this.onShard = onPage;
    drawMove();
    fire();
  }

  private void drawLimitChoices() {
    lbLimit.addItem("5");
    lbLimit.addItem("10");
    lbLimit.addItem("15");
    lbLimit.addItem("25");
    lbLimit.addItem("50");
    lbLimit.addItem("75");
    lbLimit.addItem("100");
  }

  private void setLimitChoicesAt() {
    int sel = 0;
    for (int i=0; i < lbLimit.getItemCount(); i++) {
      String slimit = Long.toString(shard.getLimit());
      String value = lbLimit.getItemText(i); 
      if (value.equals(slimit)) {
        sel = i;
        break;
      }
    }
    lbLimit.setSelectedIndex(sel);
  }

  private void setLimit() {
    int sel = lbLimit.getSelectedIndex();
    int newLimit = Integer.parseInt(lbLimit.getItemText(sel));
    shard.setLimit(shard.getOffset(), newLimit, shard.getFinish());
    divideOnPage();
    fire();
  }

  public void onClick(ClickEvent event) {
    Widget sender = (Widget) event.getSource();
    
    if (sender == bPrev) {
      setPrev();
      
    } else if (sender == bNext) {
      setNext();
      
    } else if (sender == bStart) {
      setStart();
      
    } else if (sender == bEnd) {
      setEnd();
    }
    
    drawMove();

    fire();
  }

  private void setNext() {
    if (onShard < shard.getShards()) {
      onShard++;
    }
  }

  private void setPrev() {
    if (onShard > 0) {
      onShard--;
    }
  }

  private void setStart() {
    onShard = 0;
  }

  private void setEnd() {
    onShard = shard.getShards()-1;
  }

  private void fire() {
    fireEvent(new PageChangeEvent(getOffset(), getOffset() + getLimit()));
  }

  public void onChange(ChangeEvent event) {
    Widget sender = (Widget) event.getSource();
    if (sender == lbLimit) {
      setLimit();
    }
  }
  
  public void showLoading(boolean show) {
    wLoading.showLoading(show);
  }

  @Override
  public void setVisible(boolean b) {
    if (shard.getShards() <= 1 && b == true) {
      b = false;
    }
    super.setVisible(b);
  }

  public final HandlerRegistration addPageChangeHandler(PageChangeEventHandler handler) {
    return addHandler(handler, PageChangeEvent.TYPE);
  }

  public void setDisplayChangeLimit(boolean b) {
    lbLimit.setVisible(b);
  }

}
