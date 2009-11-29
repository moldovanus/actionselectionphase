package uk.ac.man.cs.mig.coode.protege.wizard.component.tree;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.*;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.icon.WizardIcons;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jul-2004
 */
public abstract class AbstractTreeComponent extends AbstractWizardEditor {

  protected static final boolean debug = Trace.Component.treeSet;

  protected LabeledComponent labeledTree;

  protected SelectableTree theTree;

  protected Class suitableClass = Frame.class;
  protected Object rootObject;
  private boolean initialised = false;

  private boolean multiSelect = false;
  private String title = "Select From";

  private AllowableAction clearSelectionAction;
  private boolean clearAllowed = true;

  public AbstractTreeComponent(boolean multiSelect) {
    super();
    this.multiSelect = multiSelect;
  }

  public synchronized void addKeyListener(KeyListener l) {
    theTree.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    theTree.addKeyListener(l);
  }

  protected boolean initialise(LazyTreeRoot treeRoot) {
    rootObject = getObjectFromRoot(treeRoot);

    if (isSuitable(rootObject)) {
      // set the classes that are acceptable to be the same as the root
      suitableClass = rootObject.getClass();

      theTree = new SelectableTree(null, treeRoot);

      if (treeRoot.getChildCount() > 0) {
        theTree.setShowsRootHandles(true);
      }

      // choose whether multiselect allowed
      int selType = TreeSelectionModel.SINGLE_TREE_SELECTION;
      if (multiSelect) {
        selType = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;
      }
      theTree.getSelectionModel().setSelectionMode(selType);

      // make sure the tree expands on a new selection
      theTree.setExpandsSelectedPaths(true);

      // keep track of if the selection has changed
      theTree.addSelectionListener(new SelectionListener() {
        public void selectionChanged(SelectionEvent event) {
          notifyValueChanged();
        }
      });

      // use default frame renderer currently in use
      theTree.setCellRenderer(FrameRenderer.createInstance());

      labeledTree = new LabeledComponent(title, new JScrollPane(theTree));

      resetButtons(labeledTree);

      initialised = true;
    }
    else {
      if (debug) System.out.println("userObject.getClass() = " + rootObject.getClass());
    }

    return initialised;
  }

  public void setTitle(String title) {
    this.title = title;
    if (labeledTree != null) {
      labeledTree.setHeaderLabel(title);
    }
  }

  public void setClearAllowed(boolean allowed) {
    clearAllowed = allowed;
    resetButtons(labeledTree);
  }

  private void resetButtons(LabeledComponent lc) {

    lc.removeAllHeaderButtons();

    if (clearAllowed) {
      if (clearSelectionAction == null) {

        String labelText = WizardAPI.getProperty("ui.clearselection");
        clearSelectionAction = new AllowableAction(labelText, Icons.getDeleteIcon(), theTree) {
          public void actionPerformed(ActionEvent e) {
            theTree.clearSelection();
            notifyValueChanged();
          }
        };
      }
      lc.addHeaderButton(clearSelectionAction);
    }

    if (multiSelect) {
      lc.addHeaderButton(new AllowableAction("Select direct children", WizardIcons.getDirectSubIcon(), theTree) {
        public void actionPerformed(ActionEvent e) {
          Object o = theTree.getSelectionPath().getLastPathComponent();
          selectDirectChildren((LazyTreeNode) o);
          notifyValueChanged();
        }
      });

      lc.addHeaderButton(new AllowableAction("Select all children", WizardIcons.getAllSubIcon(), theTree) {
        public void actionPerformed(ActionEvent e) {
          Object o = theTree.getSelectionPath().getLastPathComponent();
          selectAllChildren((LazyTreeNode) o);
          notifyValueChanged();
        }
      });

      lc.addHeaderButton(new AllowableAction("Select leaf children", WizardIcons.getLeafSubIcon(), theTree) {
        public void actionPerformed(ActionEvent e) {
          Object o = theTree.getSelectionPath().getLastPathComponent();
          selectLeafNodes((LazyTreeNode) o);
          notifyValueChanged();
        }
      });
    }
  }

  private Object getObjectFromRoot(LazyTreeNode treeNode) {
    Object result = null;
    List roots = (List) treeNode.getUserObject();
    if ((roots != null) && (roots.size() > 0)) {
      result = roots.get(0);
    }
    return result;
  }

  public boolean setValue(Object newValue) {
    boolean result = false;

    if (initialised) {
      if ((newValue != null) && (isSuitable(newValue))) {
        if (newValue instanceof Collection) {
          setSelection((Collection) newValue);
        }
        else if (newValue instanceof Cls) {
          setSelection((Cls) newValue);
        }
        else {
          throw new IllegalArgumentException("cannot set value of class = " + newValue.getClass());
        }
      }
      else {
        theTree.clearSelection();
        result = true;
      }
    }
    else {
      System.err.println("Have not initialised the tree");
    }

    return result;
  }

  protected boolean setSelection(Collection newValue) {
    boolean result = false;
    try {
      if (multiSelect) {
        TreePath[] paths = new TreePath[((Collection) newValue).size()];
        int index = 0;
        for (Iterator i = ((Collection) newValue).iterator(); i.hasNext();) {
          Object o = i.next();
          if (o instanceof Cls) {
            paths[index++] = getTreePath(o);
          }
          else {
            System.err.println("WARNING: Only multi class selection implemented");
          }
        }
        theTree.setSelectionPaths(paths);
        result = true;
      }
      else {
        System.err.println("This tree does not support multiple selections");
      }
    }
    catch (NullPointerException e) {
      System.out.println("theTree = " + theTree);
//      theTree.clearSelection();
    }
    return result;
  }

  protected boolean setSelection(Cls newValue) {
    boolean result = false;
    try {
      KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
      if (rootObject == kb.getRootCls()) {
        if (debug) System.out.println("Found a Cls");
        theTree.setSelectionPath(getTreePath(newValue));
        result = true;
      }
      else {
        System.err.println("NON-ROOT: tree selection setting not handled yet");
      }
    }
    catch (NullPointerException e) {
      // do nothing
    }
    return result;
  }

  protected abstract TreePath getTreePath(Object value);

  protected LazyTreeNode getNodeFromUserObject(LazyTreeNode parentNode, Object userObject) {
    if (debug) System.out.println("getNodeFromUserObject -> parentNode = " + parentNode + ", uo = " + userObject);
    LazyTreeNode result = null;

    if (parentNode == null) {
      if (debug) System.out.println("Getting root");
      parentNode = (LazyTreeNode) theTree.getModel().getRoot();
    }

    int indexOfChild = parentNode.getUserObjectIndex(userObject);
    if (indexOfChild >= 0) {
      result = (LazyTreeNode) parentNode.getChildAt(indexOfChild);
    }

    if (debug) System.out.println("GOT NODE = " + result);
    return result;
  }

  public boolean isSuitable(Object value) {
    return (multiSelect && (value instanceof Collection)) || // @@TODO check collection contents
           (suitableClass.isAssignableFrom(value.getClass()));
  }

  public Object getValue() {
    Object selection = null;

    if (initialised) {
      TreePath[] paths = theTree.getSelectionPaths();

      if ((paths != null) && (paths.length > 0)) {
        if (multiSelect) {
          selection = new ArrayList(paths.length);
          for (int i = 0; i < paths.length; i++) {
            LazyTreeNode node = (LazyTreeNode) paths[i].getLastPathComponent();
            ((Collection) selection).add(node.getUserObject());
          }
        }
        else {
          LazyTreeNode node = (LazyTreeNode) paths[0].getLastPathComponent();
          selection = node.getUserObject();
        }
      }
    }
    else {
      System.err.println("Cannot get value, tree not initialised");
    }

    return selection;
  }

  public void setRoot(LazyTreeRoot treeRoot) {
    theTree.setRoot(treeRoot);
    rootObject = getObjectFromRoot(treeRoot);
  }

  public SelectableTree getSelectable() {
    return theTree;
  }

  public boolean isMultiSelect() {
    return multiSelect;
  }

  public void giveFocus() {
    theTree.requestFocus();
  }

  protected List getDirectChildrenNodes(TreeNode parent) {
    List children = null;
    int numChildren = parent.getChildCount();
    if (numChildren > 0) {
      children = new ArrayList(numChildren);
      for (int i = 0; i < numChildren; i++) {
        children.add(parent.getChildAt(i));
      }
    }
    return children;
  }

  protected boolean selectDirectChildren(TreeNode parent) {
    boolean success = false;
    if (isMultiSelect()) {
      List childrenNodes = getDirectChildrenNodes(parent);
      if (childrenNodes != null) {
        int numChildren = childrenNodes.size();
        System.out.println("GOT CHILDREN" + numChildren);
        Collection newSelection = new ArrayList(numChildren);
        for (Iterator i = childrenNodes.iterator(); i.hasNext();) {
          newSelection.add(((LazyTreeNode) i.next()).getUserObject());
        }
        setSelection(newSelection);
        success = true;
      }
    }
    return success;
  }

  protected void getLeafNodes(TreeNode parent, Collection leaves) {
    List childrenNodes = getDirectChildrenNodes(parent);
    if (childrenNodes == null) { // this is a leaf node
      leaves.add(parent);
    }
    else { // keep going through children
      for (Iterator i = childrenNodes.iterator(); i.hasNext();) {
        getLeafNodes((LazyTreeNode) i.next(), leaves);
      }
    }
  }

  protected boolean selectLeafNodes(TreeNode parent) {
    boolean success = false;
    if (isMultiSelect()) {
      List leafNodes = new ArrayList();
      getLeafNodes(parent, leafNodes);
      if (leafNodes.size() > 0) {
        int numChildren = leafNodes.size();
        Collection newSelection = new ArrayList(numChildren);
        for (Iterator i = leafNodes.iterator(); i.hasNext();) {
          newSelection.add(((LazyTreeNode) i.next()).getUserObject());
        }
        setSelection(newSelection);
        success = true;
      }
    }
    return success;
  }

  protected void getAllChildrenNodes(TreeNode parent, Collection accumulator) {
    List childrenNodes = getDirectChildrenNodes(parent);
    if (childrenNodes != null) {
      for (Iterator i = childrenNodes.iterator(); i.hasNext();) {
        TreeNode currentChild = (TreeNode) i.next();
        accumulator.add(currentChild);
        getAllChildrenNodes(currentChild, accumulator);
      }
    }
  }

  protected boolean selectAllChildren(TreeNode parent) {
    boolean success = false;
    if (isMultiSelect()) {
      List allChildren = new ArrayList();
      getAllChildrenNodes(parent, allChildren);
      if (allChildren.size() > 0) {
        int numChildren = allChildren.size();
        Collection newSelection = new ArrayList(numChildren);
        for (Iterator i = allChildren.iterator(); i.hasNext();) {
          newSelection.add(((LazyTreeNode) i.next()).getUserObject());
        }
        setSelection(newSelection);
        success = true;
      }
    }
    return success;
  }

  public JComponent getComponent() {
    return labeledTree;
  }
}
