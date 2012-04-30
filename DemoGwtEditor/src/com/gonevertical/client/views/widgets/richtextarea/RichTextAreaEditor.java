package com.gonevertical.client.views.widgets.richtextarea;

import java.text.ParseException;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RichTextArea;

public class RichTextAreaEditor extends RichTextArea implements 
    HasValueChangeHandlers<String>, HasValue<String>, TakesValue<String>, LeafValueEditor<String> {

  private boolean valueChangeHandlerInitialized;
  
  @Override
  public String getValue() {
    return getHTML();
  }

  @Override
  public void setValue(String value) {
    if (value == null) {
      return;
    }
    SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
    setHTML(html);
  }

  @Override
  public void setValue(String value, boolean fireEvents) {
    SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
    setHTML(html);
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, getHTML(), value);
    }
  }
  
  @Override
  public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
    // Initialization code
    if (!valueChangeHandlerInitialized) {
      valueChangeHandlerInitialized = true;
      addChangeHandler(new ChangeHandler() {
        public void onChange(ChangeEvent event) {
          ValueChangeEvent.fire(RichTextAreaEditor.this, getValue());
        }
      });
    }
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }

  public String getValueOrThrow() throws ParseException {
    String text = getHTML();
    if ("".equals(text)) {
      return null;
    }
    return text;
  }
  
}
