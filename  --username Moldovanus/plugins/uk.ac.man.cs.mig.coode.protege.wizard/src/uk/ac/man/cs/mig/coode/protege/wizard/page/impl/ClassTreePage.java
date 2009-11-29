package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import edu.stanford.smi.protege.model.Cls;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.SearchableClassTree;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         04-Oct-2004
 */
public class ClassTreePage extends ArbitraryComponentPage {

  private SearchableClassTree theTree = null;
  private String rootProp;

  public ClassTreePage(String pageName, String selectedProp, String rootProp, boolean multiSelect) {
    super(pageName, selectedProp);
    this.rootProp = rootProp;
    theTree = new SearchableClassTree((Cls) null, multiSelect);
    setComponent(theTree);
  }

  public ClassTreePage(String pageName, String selectedProp, String rootProp) {
    this(pageName, selectedProp, rootProp, false);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    theTree.setRoot((Cls) getPropValue(rootProp));
    if (theTree.isMultiSelect()) {
      // @@TODO could also take an individual class
      theTree.setValue((Collection) getPropValue(getPropName()));
    }
    else {
      theTree.setValue((Cls) getPropValue(getPropName()));
    }
  }

  public void setClearAllowed(boolean allowed) {
    theTree.setClearAllowed(allowed);
  }
}
