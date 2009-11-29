package uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleListModel;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Oct-2004
 */
public class SelectableListComponent extends AbstractWizardEditor {

  private LabeledComponent labeledComponent;

  private SimpleListModel theModel;
  private SelectableList theList;

  private boolean canBeEmpty = false;

  public SelectableListComponent(boolean canBeEmpty) {
    super();

    this.canBeEmpty = canBeEmpty;

    theModel = new SimpleListModel();

    theList = new SelectableList();
    theList.setModel(theModel);
    theList.setCellRenderer(FrameRenderer.createInstance());

    labeledComponent = new LabeledComponent("Selected", new JScrollPane(theList));
    addButtons(labeledComponent);

    // @@TODO need to implement dnd behaviour internally
  }

  private void addButtons(LabeledComponent lc) {

    lc.addHeaderButton(new AbstractAction("move down", Icons.getDownIcon()) {
      public void actionPerformed(ActionEvent e) {
        moveDown();
      }
    });

    lc.addHeaderButton(new AbstractAction("move up", Icons.getUpIcon()) {
      public void actionPerformed(ActionEvent e) {
        moveUp();
      }
    });

    lc.addHeaderButton(new AbstractAction("remove all", Icons.getDeleteIcon()) {
      public void actionPerformed(ActionEvent e) {
        theModel.clear();
        notifyValueChanged();
      }
    });

    lc.addHeaderButton(new AllowableAction("remove selected", Icons.getRemoveIcon(), theList) {
      public void actionPerformed(ActionEvent e) {
        removeSelection();
      }
    });
  }

  public void removeSelection() {
    int[] sel = theList.getSelectedIndices();
    if (sel.length > 0) {
      // get the entry directly before the current selection
      int newIndex = theList.getSelectedIndex() - 1;

      // remove all in the current selection
      List values = new ArrayList(theModel.getValues());
      for (int i = sel.length - 1; i >= 0; i--) {
        values.remove(sel[i]);
      }
      theModel.setValues(values);

      // update the selection (so its easy to keep deleting etc)
      if (newIndex >= 0) {
        // select the previous item
        theList.addSelectionInterval(newIndex, newIndex);
      }
      else {
        // if there are items left, select the first
        if (theModel.getValues().size() > 0) {
          theList.addSelectionInterval(0, 0);
        }
      }
      // let the parent know the values have changed
      notifyValueChanged();
    }
  }

  public void moveDown() {
    int[] sel = theList.getSelectedIndices();
    if ((sel.length > 0) && // only move if something selected
        (sel[sel.length - 1] < theModel.getSize() - 1)) { // and if the selection not at bottom

      // move all in the current selection
      List values = new ArrayList(theModel.getValues());
      for (int i = sel.length - 1; i >= 0; i--) {
        Object lastValueInSelectionNotYetMoved = values.get(sel[i]);
        Object nextValue = values.get(sel[i] + 1);
        // switch them
        values.set(sel[i], nextValue);
        values.set(sel[i] + 1, lastValueInSelectionNotYetMoved);
      }
      theModel.setValues(values);

      for (int i = 0; i < sel.length; i++) {
        theList.addSelectionInterval(sel[i] + 1, sel[i] + 1);
      }
      // let the parent know the values have changed
      notifyValueChanged();
    }
  }

  public void moveUp() {
    int[] sel = theList.getSelectedIndices();
    if ((sel.length > 0) && // only move if something selected
        (sel[0] > 0)) { // and if the selection not at top

      // move all in the current selection
      List values = new ArrayList(theModel.getValues());
      for (int i = 0; i < sel.length; i++) {
        Object firstValueInSelectionNotYetMoved = values.get(sel[i]);
        Object nextValue = values.get(sel[i] - 1);
        // switch them
        values.set(sel[i], nextValue);
        values.set(sel[i] - 1, firstValueInSelectionNotYetMoved);
      }
      theModel.setValues(values);

      for (int i = 0; i < sel.length; i++) {
        theList.addSelectionInterval(sel[i] - 1, sel[i] - 1);
      }
      // let the parent know the values have changed
      notifyValueChanged();
    }
  }

  public boolean setValue(Object newValue) {
    theModel.clear();
    if ((newValue != null) && (((Collection) newValue).size() > 0)) {
      theModel.addValues((Collection) newValue);
    }
    return true;
  }

  public Object getValue() {
    AbstractList result = null;
    Collection values = theModel.getValues();
    if ((values != null) && (values.size() > 0)) {
      result = new ArrayList(values);
    }
    return result;
  }

  public int addValues(Collection newValues) {
    int result = theModel.addValues(newValues);
    int lastElementIndex = theModel.getSize() - 1;
    theList.addSelectionInterval(lastElementIndex - newValues.size() - 1, lastElementIndex);
    notifyValueChanged();
    return result;
  }

  public int addValue(Object newValue) {
    int result = theModel.addValue(newValue);
    int lastElementIndex = theModel.getSize() - 1;
    theList.addSelectionInterval(lastElementIndex, lastElementIndex);
    notifyValueChanged();
    return result;
  }

  public boolean contains(Object o) {
    return theModel.contains(o);
  }

  public SelectableList getListComponent() {
    return theList;
  }

  public boolean isSuitable(Object value) {
    return value instanceof Collection;
  }

  public boolean currentValueIsValid() {
    return (canBeEmpty) || (theModel.getSize() > 0);
  }

  public JComponent getComponent() {
    return labeledComponent;
  }
}
