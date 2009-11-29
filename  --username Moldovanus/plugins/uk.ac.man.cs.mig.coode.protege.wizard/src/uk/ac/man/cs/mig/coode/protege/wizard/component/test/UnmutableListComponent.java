package uk.ac.man.cs.mig.coode.protege.wizard.component.test;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class UnmutableListComponent extends AbstractWizardEditor {

  private DefaultListModel myModel;
  private JList myList;

  public boolean isSuitable(Object value) {
    return true; // as it creates a list with a single item if not a Collection
  }

  public UnmutableListComponent() {
    super();

    myModel = new DefaultListModel();

    myList = new JList(myModel);
  }

  public boolean setValue(Object newValue) {
    myModel.removeAllElements();

    if (newValue != null) {
      if (newValue instanceof Collection) {
        Iterator i = ((Collection) newValue).iterator();
        int j = 0;
        while (i.hasNext()) {
          myModel.add(j++, i.next());
        }
      }
      else {
        myModel.add(0, newValue);
      }
    }

    myList.repaint();

    return true;
  }

  public Object getValue() {
    Object result = null;
    if (myModel.getSize() > 0) {
      result = new ArrayList(myModel.getSize());
      Enumeration e = myModel.elements();
      while (e.hasMoreElements()) {
        ((Collection) result).add(e.nextElement());
      }
    }
    return result;
  }

  public JComponent getComponent() {
    return myList;
  }
}