package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractBulkClassOperationWizard;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Mar-2005
 */
public abstract class AbstractBulkOWLClassOpWizard extends AbstractBulkClassOperationWizard{

  public boolean isSuitable(KnowledgeBase kb) {
    return kb instanceof OWLModel;
  }

  public OWLModel getModel() {
    return (OWLModel) getKnowledgeBase();
  }
}
