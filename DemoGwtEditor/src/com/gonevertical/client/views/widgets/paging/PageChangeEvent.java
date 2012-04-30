package com.gonevertical.client.views.widgets.paging;

import com.google.gwt.event.shared.GwtEvent;

/**
 * authorization event, in response to logging into Google system
 * 
 * @author branflake2267
 *
 */
public class PageChangeEvent extends GwtEvent<PageChangeEventHandler> {

  public static Type<PageChangeEventHandler> TYPE = new Type<PageChangeEventHandler>();
  
  /**
   * range start
   */
  private long start;
  
  /**
   * range end
   */
  private long end;

  /**
   * page change event
   * @param start
   * @param end
   */
  public PageChangeEvent(long start, long end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Type<PageChangeEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PageChangeEventHandler handler) {
    handler.onEditEvent(this);
  }

  public long getStart() {
    return start;
  }
  
  public long getEnd() {
    return end;
  }
  
}
