package uk.ac.man.cs.mig.coode.protege.wizard.component.tree;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.ClsTreeFinder;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;

import java.awt.*;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jul-2004
 */
public class SearchableClassTree extends ClassTreeComponent {

  public SearchableClassTree(Cls rootClass) {
    this(rootClass, false);
  }

  public SearchableClassTree(Cls rootClass, boolean multiSelect) {
    super(rootClass, multiSelect);
    createGUI();
  }

  public SearchableClassTree(Collection rootClasses) {
    this(rootClasses, false);
  }

  public SearchableClassTree(Collection rootClasses, boolean multiSelect) {
    super(rootClasses, multiSelect);
    createGUI();
  }

  private void createGUI() {
    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    Component searchComponent = new ClsTreeFinder(kb, theTree, "Find");
    getComponent().add(searchComponent, BorderLayout.SOUTH);
  }
}
