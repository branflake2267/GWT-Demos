package com.gonevertical.client.views.peopleedit.editor;

import java.io.IOException;
import java.util.List;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.events.EditEvent;
import com.gonevertical.client.app.events.EditEventHandler;
import com.gonevertical.client.app.requestfactory.PeopleDataRequest;
import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.gonevertical.client.views.peopleedit.editor.todos.TodoListEditor;
import com.gonevertical.client.views.widgets.richtextarea.RichTextAreaEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonEditor extends Composite implements Editor<PeopleDataProxy> {

  private ClientFactory clientFactory;
  
  private static PersonEditorUiBinder uiBinder = GWT.create(PersonEditorUiBinder.class);

  @UiField 
  ValueBoxEditorDecorator<String> nameFirst;

  @UiField 
  ValueBoxEditorDecorator<String> nameLast;

  @UiField 
  SimpleCheckBox active;

  @UiField(provided = true) 
  ValueListBox<Integer> gender = new ValueListBox<Integer>(new Renderer<Integer>() {
    public String render(Integer object) {
      return Gender.getGenderName(object);
    }

    @Override
    public void render(Integer object, Appendable appendable) throws IOException {
      String s = Gender.getGenderName(object); // TODO what is this?
      appendable.append(s);
    }
  });

  @UiField 
  RichTextAreaEditor note;

  /**
   * Must annotate server
   *      @Persistent(defaultFetchGroup = "true", dependentElement = "true")
   *      and use .with("todos"); with the request factory context finding
   */
  @UiField 
  TodoListEditor todos;

  interface PersonEditorUiBinder extends UiBinder<Widget, PersonEditor> {}

  public PersonEditor(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    gender.setAcceptableValues(Gender.getValues());

    initWidget(uiBinder.createAndBindUi(this));
    
    todos.setClientFactory(clientFactory);
  }
  
  public final HandlerRegistration addEditHandler(EditEventHandler<PeopleDataProxy> handler) {
    return addHandler(handler, EditEvent.TYPE);
  }
}
