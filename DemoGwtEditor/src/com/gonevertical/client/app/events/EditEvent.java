package com.gonevertical.client.app.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

/**
 * authorization event, in response to logging into Google system
 * 
 * @author branflake2267
 *
 */
public class EditEvent<T extends EntityProxy> extends GwtEvent<EditEventHandler> {

  public static Type<EditEventHandler> TYPE = new Type<EditEventHandler>();

  /**
   * Type of Edit Event
   */
  public static enum Edit {
    
    /**
     * on errors
     */
    ERRORS,
    
    /**
     * when finished editing
     */
    FINISHED,
    
    /**
     * start of the saving of data
     */
    SAVING,
    
    /**
     * end of saving of the data
     */
    SAVED,
    
    /**
     * constraint violation
     */
    VIOLATION, 
    
    /**
     * tell parent to save it
     */
    SAVE; 
  }

  private Edit edit;

  /**
   * data transport (proxy) object
   */
  private T data;
  
  /**
   * for saving and errors
   * @param edit
   */
  public EditEvent(Edit edit) {
    this.edit = edit;
  }

  /**
   * for saved
   * @param edit
   * @param data
   */
  public EditEvent(Edit edit, T data) {
    this.edit = edit;
    this.data = data;
  }
  

  @Override
  public Type<EditEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditEventHandler handler) {
    handler.onEditEvent(this);
  }

  /**
   * get event type
   * @return
   */
  public Edit getEvent() {
    return edit;
  }
  
  /**
   * get dto
   * @return
   */
  public T getData() {
    return data;
  }

}
