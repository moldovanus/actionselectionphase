package uk.ac.man.cs.mig.coode.protege.wizard.component.tree;

import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protege.util.TransferableCollection;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Nov-2004
 */
public class DNDSelectableTree extends SelectableTree
        implements DragGestureListener, DragSourceListener {

  public static final boolean debug = false;

  TreeSelectionEvent myCachedSelectionEvent;

  public DNDSelectableTree(Action action, LazyTreeRoot lazyTreeRoot) {
    super(action, lazyTreeRoot);

    setShowsRootHandles(true);

    DragSource dragSource = DragSource.getDefaultDragSource();

    // creating the recognizer is all that's necessary - it
    // does not need to be manipulated after creation

    dragSource.createDefaultDragGestureRecognizer(this, // component where drag originates
                                                  DnDConstants.ACTION_COPY, // actions
                                                  this); // drag gesture listener
    addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        if (myCachedSelectionEvent == null)
          return;
        DNDSelectableTree.super.fireValueChanged(myCachedSelectionEvent);

        myCachedSelectionEvent = null;
      }
    });
  }

  public void dragGestureRecognized(DragGestureEvent dge) {
    if (debug) System.out.println("DNDSelectableTree.dragGestureRecognized");
    Transferable t = new TransferableCollection(getSelection());
    dge.startDrag(null, t);
  }

  public void dragEnter(DragSourceDragEvent dsde) {
    if (debug) System.out.println("DNDSelectableTree.dragEnter");
  }

  public void dragOver(DragSourceDragEvent dsde) {
    if (debug) System.out.println("DNDSelectableTree.dragOver");
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
    if (debug) System.out.println("DNDSelectableTree.dropActionChanged");
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
    if (debug) System.out.println("DNDSelectableTree.dragDropEnd");
  }

  public void dragExit(DragSourceEvent dse) {
    if (debug) System.out.println("DNDSelectableTree.dragExit");
  }
}
