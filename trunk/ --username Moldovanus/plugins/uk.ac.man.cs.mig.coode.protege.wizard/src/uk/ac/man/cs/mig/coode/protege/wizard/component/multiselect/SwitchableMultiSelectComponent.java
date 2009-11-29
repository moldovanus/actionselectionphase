package uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.SelectableTree;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.AbstractTreeComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.SearchableClassTree;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.SlotTreeComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class SwitchableMultiSelectComponent extends AbstractWizardEditor {

  public static final boolean debug = Trace.Component.multiSelect;

  private static final boolean MULTI_SELECT_ON = true;

  private JComponent mainPanel;

  protected AbstractTreeComponent theTree = null;
  protected SelectableListComponent theList = null;

  private boolean emptyAllowed;

  /**
   * @param root       the root of the selection tree. Cannot be null
   * @param canBeEmpty true if the component is allowed to have a null selection,
   *                   false if the component must have a selection made to be valid
   */
  public SwitchableMultiSelectComponent(Frame root, boolean canBeEmpty) {
    super();
    mainPanel = new JPanel(new GridLayout(1, 2, 6, 6));
    emptyAllowed = canBeEmpty;
    if (root != null) {
      setRoot(root);
    }
  }

  public SwitchableMultiSelectComponent(Collection rootFrames, boolean canBeEmpty) {
    super();
    mainPanel = new JPanel(new GridLayout(1, 2, 6, 6));
    emptyAllowed = canBeEmpty;
    if (rootFrames != null){
      setRoot(rootFrames);
    }
  }

  public void setRoot(Collection rootFrames) {
    AbstractTreeComponent tree = null;
    Frame firstRoot = (Frame) CollectionUtilities.getFirstItem(rootFrames);
    if (firstRoot instanceof Cls) {
      tree = new SearchableClassTree(rootFrames, MULTI_SELECT_ON);
    }
    else if (firstRoot instanceof Slot) {
      tree = new SlotTreeComponent(rootFrames, MULTI_SELECT_ON);
    }
    else {
      System.err.println("CANNOT CREATE MULTI_SELECT_ON FROM COLLECTION- WRONG ROOT TYPE: " + firstRoot.getClass());
    }

    createGUI(tree);
  }

  public void setRoot(Frame root) {

    AbstractTreeComponent tree = null;
    if (root instanceof Cls) {
      tree = new SearchableClassTree((Cls) root, MULTI_SELECT_ON);
    }
    else if (root instanceof Slot) {
      tree = new SlotTreeComponent((Slot) root, MULTI_SELECT_ON);
    }
    else {
      System.err.println("CANNOT CREATE MULTI_SELECT_ON - WRONG ROOT TYPE: " + root.getClass());
    }
    createGUI(tree);
  }

  private void createGUI(AbstractTreeComponent tree) {
    if (debug) System.out.println("SwitchableMultiSelectComponent.createGUI");
    if (theTree != null){
      mainPanel.removeAll();
    }
    theTree = tree;

    SelectableTree selectableTree = theTree.getSelectable();
    if (selectableTree != null) {
      selectableTree.setDragEnabled(true);
    }
    else {
      System.err.println("cannot get the tree - not yet initialised");
    }

    theTree.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_ENTER:
            if (!e.isControlDown()) {
              updateSelection();
            }
        }
      }
    });

    mainPanel.add(theTree.getComponent());
    setList();
  }

  private void setList(){
    if (theList == null){

      theList = new SelectableListComponent(emptyAllowed);

      // @@TODO need to implement dnd behaviour correctly within components
      DropTargetListener dt = new DropTargetAdapter() {
        public void drop(DropTargetDropEvent dtde) {
          updateSelection();
        }
      };

      theList.getListComponent().setDropTarget(new DropTarget(theList.getListComponent(), dt));
    }
    mainPanel.add(theList.getComponent());
  }

  private void updateSelection() {
    Collection selected = (Collection) theTree.getValue();

    if (selected != null) {
      for (Iterator i = selected.iterator(); i.hasNext();) {
        Frame fr = (Frame) i.next();
        if (!theList.contains(fr)) {
          theList.addValue(fr);
        }
      }
    }
  }

  public boolean isSuitable(Object value) {
    return theList.isSuitable(value);
  }

  public boolean setValue(Object newValue) {
    return theList.setValue(newValue);
  }

  public Object getValue() {
    return theList.getValue();
  }

  public boolean addValueChangeListener(ValueChangeListener valueChangeListener) {
    return theList.addValueChangeListener(valueChangeListener);
  }

  public boolean removeValueChangeListener(ValueChangeListener valueChangeListener) {
    return theList.removeValueChangeListener(valueChangeListener);
  }

  public boolean currentValueIsValid() {
    return theList.currentValueIsValid();
  }

  public void giveFocus() {
    theTree.giveFocus();
  }

  public JComponent getComponent() {
    return mainPanel;
  }
}
