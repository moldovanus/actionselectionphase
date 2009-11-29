package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import edu.stanford.smi.protege.model.Slot;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.SlotTreeComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         04-Oct-2004
 */
public class SlotTreePage extends ArbitraryComponentPage {

  private SlotTreeComponent theTree = null;
  private String rootProp;

  public SlotTreePage(String pageName, String selectedProp, String rootProp, boolean multiSelect) {
    super(pageName, selectedProp);
    this.rootProp = rootProp;
    theTree = new SlotTreeComponent((Slot) null, multiSelect);
    setComponent(theTree);
  }

  public SlotTreePage(String pageName, String selectedProp, String rootProp) {
    this(pageName, selectedProp, rootProp, false);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    Object roots = getPropValue(rootProp);
    if (roots instanceof Collection) {
      theTree.setRoots((Collection) roots);
    }
    else {
      theTree.setRoot((Slot) roots);
    }
    if (theTree.isMultiSelect()) {
      // @@TODO could also take an individual class
      theTree.setValue((Collection) getPropValue(getPropName()));
    }
    else {
      theTree.setValue(getPropValue(getPropName()));
    }
  }
}
