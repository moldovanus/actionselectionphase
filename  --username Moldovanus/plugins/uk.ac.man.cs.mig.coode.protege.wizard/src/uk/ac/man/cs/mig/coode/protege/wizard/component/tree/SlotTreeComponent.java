package uk.ac.man.cs.mig.coode.protege.wizard.component.tree;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.SlotSubslotRoot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;

import javax.swing.tree.TreePath;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jul-2004
 */
public class SlotTreeComponent extends AbstractTreeComponent {

  public SlotTreeComponent(Slot rootSlot) {
    this(rootSlot, false);
  }

  public SlotTreeComponent(Slot rootSlot, boolean multiSelect) {
    super(multiSelect);

    //System.err.println("WARNING: MANY FEATURES OF THE SLOT TREE ARE NOT YET IMPLEMENTED");

    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    SlotSubslotRoot rootNode = null;
    Collection rootSlots;

    if (rootSlot == null) {
      rootSlots = kb.getRootSlots();
    }
    else {
      rootSlots = CollectionUtilities.createCollection(rootSlot);
    }

    rootNode = new SlotSubslotRoot(kb, rootSlots);

    initialise(rootNode);
  }

  public SlotTreeComponent(Collection rootSlots, boolean multiSelect) {
    super(multiSelect);

    //System.err.println("WARNING: MANY FEATURES OF THE SLOT TREE ARE NOT YET IMPLEMENTED");

    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    SlotSubslotRoot rootNode = null;

    if (rootSlots == null) {
      rootSlots = kb.getRootSlots();
    }

    rootNode = new SlotSubslotRoot(kb, rootSlots);

    initialise(rootNode);
  }

  protected TreePath getTreePath(Object value) {
    return null;  // @@TODO change default
  }

  public boolean isSuitable(Object value) {
    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    if (debug) System.out.println("suitability: value slot = " + value.getClass());
    return (super.isSuitable(value) && (kb.containsFrame(((Slot) value).getName())));
  }

  public void setRoot(Slot newRoot) {
    setRoots(CollectionUtilities.createCollection(newRoot));
  }

  public void setRoots(Collection roots) {
    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    super.setRoot(new SlotSubslotRoot(kb, roots));
  }
}
