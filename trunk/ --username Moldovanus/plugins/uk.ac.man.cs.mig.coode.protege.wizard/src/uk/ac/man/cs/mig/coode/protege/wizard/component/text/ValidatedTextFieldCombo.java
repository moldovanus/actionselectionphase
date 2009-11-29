package uk.ac.man.cs.mig.coode.protege.wizard.component.text;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.Fixable;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Mar-2005
 */
public class ValidatedTextFieldCombo extends AbstractWizardEditor
 implements Fixable{

  private JPanel thePanel;

  private JComboBox combo;
  private ValidatedTextField editor;
  private DefaultComboBoxModel model;

  public ValidatedTextFieldCombo(StringValidator validator) {
    super();

    thePanel = new JPanel(new BorderLayout(6, 6));

    model = new DefaultComboBoxModel();
    model.addListDataListener(new ListDataListener(){
      public void contentsChanged(ListDataEvent e) {
        notifyValueChanged();
      }
      public void intervalAdded(ListDataEvent e) { }
      public void intervalRemoved(ListDataEvent e) { }
    });
    combo = new JComboBox(model);
    combo.setEditable(true);
    editor = new ValidatedTextField(validator);
    combo.setEditor(editor);

    thePanel.add(combo, BorderLayout.NORTH);
  }

  public Object getValue(){
    return combo.getSelectedItem();
  }

  public JComponent getComponent() {
    return thePanel;
  }

  public void setValues(Collection prefixes){
      model.removeAllElements();
      for (Iterator i=prefixes.iterator(); i.hasNext();){
        model.addElement(i.next());
      }
      model.setSelectedItem(null);
    }

  public boolean isSuitable(Object value) {
    return value instanceof String;
  }

  public boolean setValue(Object newValue) {
    model.setSelectedItem(newValue);
    return true;
  }

  public void fix() {
    editor.fix();
  }

  public boolean addValueChangeListener(ValueChangeListener valueChangeListener) {
    editor.addValueChangeListener(valueChangeListener);
    return super.addValueChangeListener(valueChangeListener);
  }

  public boolean removeValueChangeListener(ValueChangeListener valueChangeListener) {
    editor.removeValueChangeListener(valueChangeListener);
    return super.removeValueChangeListener(valueChangeListener);
  }

  public synchronized void addKeyListener(KeyListener l) {
    combo.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    combo.removeKeyListener(l);
  }

  public void giveFocus() {
    combo.requestFocus();
  }

  public boolean currentValueIsValid() {
    return editor.currentValueIsValid();
  }
}
