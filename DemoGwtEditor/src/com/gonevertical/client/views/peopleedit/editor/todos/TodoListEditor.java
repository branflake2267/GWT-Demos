package com.gonevertical.client.views.peopleedit.editor.todos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.events.DeleteEvent;
import com.gonevertical.client.app.events.DeleteEventHandler;
import com.gonevertical.client.app.events.EditEvent;
import com.gonevertical.client.app.events.EditEvent.Edit;
import com.gonevertical.client.app.events.EditEventHandler;
import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.gonevertical.server.data.TodoData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.gwt.client.HasRequestContext;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;

/**
 * be sure the server side class is annotated appropriately
 *     @Persistent(defaultFetchGroup = "true", dependentElement = "true") - for JDO
 *     @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, targetEntity=TodoData.class) - for JPA
 *     and use .with("todos"); with the request factory context finding
 */
public class TodoListEditor extends Composite implements IsEditor<ListEditor<TodoDataProxy, TodoItemEditor>>, HasRequestContext<List<TodoDataProxy>> {

  private static TodoListEditorUiBinder uiBinder = GWT.create(TodoListEditorUiBinder.class);
  
  @UiField 
  FlowPanel pWidget;
  
  @UiField 
  PushButton bAdd;
  
  @UiField 
  FlowPanel plist;

  
  interface TodoListEditorUiBinder extends UiBinder<Widget, TodoListEditor> {}
  
  
  private class TodoItemEditorSource extends EditorSource<TodoItemEditor> {
    @Override
    public TodoItemEditor create(final int index) {
      TodoItemEditor subEditor = new TodoItemEditor();
      plist.insert(subEditor, index);
      subEditor.addDeleteHandler(new DeleteEventHandler() {
        public void onDeleteEvent(DeleteEvent event) {
          remove(index);
        }
      });
      return subEditor;
    }     

    @Override
    public void dispose(TodoItemEditor subEditor) {
      subEditor.removeFromParent();
      
    }
    @Override
    public void setIndex(TodoItemEditor editor, int index) {
      plist.insert(editor, index);
    }
    
  }   
  private ListEditor<TodoDataProxy, TodoItemEditor> editor = ListEditor.of(new TodoItemEditorSource());
  
  private RequestContext context;

  private ClientFactory clientFactory;

  public TodoListEditor() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    add();
  }

  private void add() {
    TodoDataProxy e = context.create(TodoDataProxy.class);
    editor.getList().add(e);
  }

  @Override
  public ListEditor<TodoDataProxy, TodoItemEditor> asEditor() {
    return editor;
  }

  /**
   * this is from HasRequestContext<List<TodoDataProxy>>
   */
  @Override
  public void setRequestContext(RequestContext context) {
    this.context = context;
  }

  /**
   * the only way to delete a child owned object is to independently delete/remove it in app engine owned collection
   *    if the object is not owned, this is not needed.
   * @param index
   */
  private void remove(final int index) {
    
    TodoDataProxy t = editor.getList().get(index);
    String id = t.getId();
    if (id == null) {
      editor.getList().remove(index);
      return;
    }
    clientFactory.getRequestFactory().getTodoDataRequest().remove(id).fire(new Receiver<Boolean>() {
      public void onSuccess(Boolean response) {
        editor.getList().remove(index);
        fireEvent(new EditEvent(Edit.SAVE));
      }
    });
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }
  
  public final HandlerRegistration addEditHandler(EditEventHandler<PeopleDataProxy> handler) {
    return addHandler(handler, EditEvent.TYPE);
  }
}
