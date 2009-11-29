package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.console.TextAreaConsole;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Aug-2004
 */
public class BasicListEntryComponent extends AbstractWizardEditor {

  private static final boolean debug = Trace.Component.listEntry;

  protected UniqueEntryListEditor theEditor;
  private LineNumberComponent numberPanel;

  private StringValidator validator;
  protected JPanel mainPanel;

  public StringValidator getValidator() {
    return validator;
  }

  public boolean isSuitable(Object value) {
    return value instanceof Collection;
  }

  public BasicListEntryComponent(StringValidator validator) {

    mainPanel = new JPanel(new BorderLayout(6, 6));

    this.validator = validator;
    TextAreaConsole errorsComponent = null;
    if (validator != null) {
      errorsComponent = new TextAreaConsole();
      errorsComponent.getTextPane().setRows(3);
    }
    theEditor = createEditor(validator, errorsComponent);
    theEditor.setColumns(60);
    theEditor.setRows(15);

    numberPanel = new LineNumberComponent(theEditor);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(numberPanel, BorderLayout.WEST);
    panel.add(theEditor, BorderLayout.CENTER);

    mainPanel.add(new JScrollPane(panel), BorderLayout.CENTER);

      if (errorsComponent != null){
        mainPanel.add(errorsComponent, BorderLayout.SOUTH);
//      errorLabel.setText("start inputting");
    }
  }

  protected UniqueEntryListEditor createEditor(StringValidator validator, Console errorsConsole) {
    return new UniqueEntryListEditor(validator, errorsConsole);
  }

  public void setPropertyName(String propertyName) {
    super.setPropertyName(propertyName);
    theEditor.setPropertyName(propertyName);
  }

  public boolean setValue(Object newValue) {
    boolean result = false;
    if (newValue != null) {
      if (newValue instanceof Collection) {
        StringBuffer listStringB = new StringBuffer();
        Iterator i = ((Collection) newValue).iterator();
        while (i.hasNext()) {
          listStringB.append((String) i.next());
          listStringB.append('\n');
        }
        theEditor.setText(listStringB.toString());
      }
      else if (newValue instanceof String) {
        theEditor.setText((String) newValue);
        result = true;
      }
      else {
        theEditor.clearAllLines();
      }
    }
    else {
      theEditor.clearAllLines();
    }
    return result;
  }

  public Object getValue() {
    return theEditor.getValue();
  }

  public Object getParent(Object value) {
    return null;
  }

  public boolean currentValueIsValid() {
    return ((theEditor.getValidEntries().size() > 0) &&
            (theEditor.getErrors().size() <= 0));
  }

  public JComponent getComponent() {
    return mainPanel;
  }

  public boolean addValueChangeListener(ValueChangeListener valueChangeListener) {
    super.addValueChangeListener(valueChangeListener);
    return theEditor.addValueChangeListener(valueChangeListener);
  }

  public boolean removeValueChangeListener(ValueChangeListener valueChangeListener) {
    super.removeValueChangeListener(valueChangeListener);
    return theEditor.removeValueChangeListener(valueChangeListener);
  }

  public void giveFocus() {
    theEditor.requestFocus();
  }

  public synchronized void addKeyListener(KeyListener l) {
    theEditor.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    theEditor.removeKeyListener(l);
  }
}
