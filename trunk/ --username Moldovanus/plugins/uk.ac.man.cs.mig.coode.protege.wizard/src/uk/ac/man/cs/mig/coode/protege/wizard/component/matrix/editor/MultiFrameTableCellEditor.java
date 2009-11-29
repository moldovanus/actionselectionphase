package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import edu.stanford.smi.protege.model.Instance;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect.MultiSelectComponent;

import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Oct-2004
 */
class MultiFrameTableCellEditor extends AbstractDialogCellEditor {

  private Instance root = null;
  private AbstractWizardEditor multisel = null;

  public MultiFrameTableCellEditor(Frame parentFrame, Instance root, String title) {
    super(parentFrame, title);
    this.root = root;
  }

  protected AbstractWizardEditor createComponent() {
    if (multisel == null) {
      multisel = new MultiSelectComponent(root, true);
    }
    return multisel;
  }
}
