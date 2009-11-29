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
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class MultiSelectComponent extends AbstractWizardEditor {

  public static final boolean debug = Trace.Component.multiSelect;

  private static final boolean MULTI_SELECT_ON = true;

  protected AbstractTreeComponent theTree = null;
  protected SelectableListComponent theList = null;

  protected boolean duplicateValuesAllowed = false;
  private JPanel mainPanel;

  public MultiSelectComponent(Collection rootFrames, boolean canBeEmpty) {
    super();
    Frame firstRoot = (Frame) CollectionUtilities.getFirstItem(rootFrames);
    if (firstRoot instanceof Cls) {
      theTree = new SearchableClassTree(rootFrames, MULTI_SELECT_ON);
    }
    else if (firstRoot instanceof Slot) {
      theTree = new SlotTreeComponent(rootFrames, MULTI_SELECT_ON);
    }
    else if (firstRoot != null) {
      System.err.println("CANNOT CREATE MULTI_SELECT_ON FROM COLLECTION- WRONG ROOT TYPE: " + firstRoot.getClass());
    }
    else {
      System.err.println("NO ROOT SUPPLIED FOR MULTISELECT");
    }
    createGUI(canBeEmpty);
  }

  /**
   * @param root       the root of the selection tree. Cannot be null
   * @param canBeEmpty true if the component is allowed to have a null selection,
   *                   false if the component must have a selection made to be valid
   */
  public MultiSelectComponent(Frame root, boolean canBeEmpty) {
    super();
    if (root != null) {
      if (debug) System.out.println("root = " + root);
      if (root instanceof Cls) {
        theTree = new SearchableClassTree((Cls) root, MULTI_SELECT_ON);
      }
      else if (root instanceof Slot) {
        theTree = new SlotTreeComponent((Slot) root, MULTI_SELECT_ON);
      }
      else {
        System.err.println("CANNOT CREATE MULTI_SELECT_ON - WRONG ROOT TYPE: " + root.getClass());
      }
      createGUI(canBeEmpty);
    }
    else {
      throw new NullPointerException("MultiSelect Component cannot be created with a null root");
    }
  }

  private void createGUI(boolean canBeEmpty) {

    theTree.addKeyListener(new KeyAdapter(){

      public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()){
          case KeyEvent.VK_ENTER:
            if (!e.isControlDown()){
              updateSelection();
            }
            break;
        }
      }
    });

    mainPanel = new JPanel(new GridLayout(1, 2, 6, 6));

    theList = new SelectableListComponent(canBeEmpty);

//    JPanel buttonPanel = new JPanel(new BorderLayout());
//    buttonPanel.setBackground(Color.YELLOW);
//
//    buttonPanel.add(new JButton(new AbstractAction(">>"){
//      public void actionPerformed(ActionEvent e) {
//        updateSelection();
//      }
//    }), BorderLayout.NORTH);
//
//    buttonPanel.add(new JButton(new AbstractAction("<<"){
//      public void actionPerformed(ActionEvent e) {
//        theList.removeSelection();
//      }
//    }), BorderLayout.SOUTH);

    mainPanel.add(theTree.getComponent());
//    add(buttonPanel, BorderLayout.CENTER);
    mainPanel.add(theList.getComponent());

    // @@TODO need to implement dnd behaviour correctly within components
    DropTargetListener dt = new DropTargetAdapter() {
      public void drop(DropTargetDropEvent dtde) {
//        try {
//          Object o = dtde.getTransferable().getTransferData(new DataFlavor(TransferableCollection.class, "Collection"));
//          System.out.println("o.getClass() = " + o.getClass());
//        } catch (UnsupportedFlavorException e) {
//          e.printStackTrace();  //@@TODO replace default stack trace
//        } catch (IOException e) {
//          e.printStackTrace();  //@@TODO replace default stack trace
//        }

        //DropTargetContext.TransferableProxy p = (DropTargetContext.TransferableProxy)tr;
        //Collection selected = (Collection) tr.getCollectionTransferData();
        updateSelection();
      }
    };

    theList.getListComponent().setDropTarget(new DropTarget(theList.getListComponent(), dt));

    SelectableTree test = theTree.getSelectable();
    if (test != null) {
      test.setDragEnabled(true);
    }
    else {
      System.err.println("cannot get the tree - not yet initialised");
    }

    //theTree.getSelectable().setSelectionRow(0);
  }

  private void updateSelection() {
    Collection selected = (Collection) theTree.getValue();

    if (selected != null) {
      for (Iterator i = selected.iterator(); i.hasNext();) {
        Frame fr = (Frame) i.next();
        if ((duplicateValuesAllowed) || (!theList.contains(fr))) {
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

  public void setDuplicateValuesAllowed(boolean duplicateValuesAllowed) {
    this.duplicateValuesAllowed = duplicateValuesAllowed;
  }

  public boolean isDuplicateValuesAllowed() {
    return duplicateValuesAllowed;
  }

  public synchronized void addKeyListener(KeyListener l) {
    theTree.addKeyListener(l);
    theList.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    theTree.removeKeyListener(l);
    theList.removeKeyListener(l);
  }
}
