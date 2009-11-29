package uk.ac.man.cs.mig.coode.protege.wizard.component.tree;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.util.LazyTreeNode;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;

import javax.swing.tree.TreePath;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jul-2004
 */
public class ClassTreeComponent extends AbstractTreeComponent {

  public ClassTreeComponent(Cls rootClass) {
    this(rootClass, false);
  }

  public ClassTreeComponent(Cls rootClass, boolean multiSelect) {
    super(multiSelect);
    try {
      if (rootClass == null) {
        rootClass = getHelper().getRootCls();
      }
      initialise(new ParentChildRoot(rootClass));
    }
    catch (Exception e) {
      System.err.println("Invalid root supplied for Class Tree Component");
      e.printStackTrace();
    }
  }

  public ClassTreeComponent(Collection rootClasses) {
    this(rootClasses, false);
  }

  public ClassTreeComponent(Collection rootClasses, boolean multiSelect) {
    super(multiSelect);
    try {
      if ((rootClasses == null) || (rootClasses.size() == 0)) {
        Cls rootClass = getHelper().getRootCls();
        initialise(new ParentChildRoot(rootClass));
      }
      else {
        initialise(new ParentChildRoot(rootClasses));
      }
    }
    catch (Exception e) {
      System.err.println("Invalid root supplied for Class Tree Component");
      e.printStackTrace();
    }
  }

  protected TreePath getTreePath(Object value) {
    List pathList = ModelUtilities.getPathToRoot((Cls) value);

    LazyTreeNode currentNode = null;
    Object[] pathArray = new Object[pathList.size() + 1];
    pathArray[0] = (LazyTreeNode) theTree.getModel().getRoot();
    int pathIndex = 1;

    for (Iterator i=pathList.iterator(); i.hasNext();) {
      currentNode = getNodeFromUserObject(currentNode, i.next());
      pathArray[pathIndex] = currentNode;
      pathIndex++;
    }

    return new TreePath(pathArray);
  }

  public boolean isSuitable(Object value) {
    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    boolean result = super.isSuitable(value);
    if (!isMultiSelect()) {
      // also make sure the class exists
      result = result && (kb.containsFrame(((Cls) value).getName()));
    }
    return result;
  }

  public void setRoot(Cls newRoot) {
    super.setRoot(new ParentChildRoot(newRoot));
  }
}
