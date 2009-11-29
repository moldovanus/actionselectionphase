package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import edu.stanford.smi.protege.model.Cls;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.SearchableClassTree;

import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Oct-2004
 */
public class ClassTreeTableCellEditor extends AbstractDialogCellEditor {

  private Cls root = null;
  private AbstractWizardEditor tree = null;

  public ClassTreeTableCellEditor(Frame parentFrame, Cls root, String title) {
    super(parentFrame, title);
    this.root = root;
  }

  protected AbstractWizardEditor createComponent() {
    if (tree == null) {
      tree = new SearchableClassTree(root);
    }
    return tree;
  }
}
