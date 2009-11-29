package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Oct-2004
 */
public abstract class AbstractOWLWizard extends AbstractWizard {

  protected AbstractOWLWizard() {
    super();
  }

  protected AbstractOWLWizard(String propertiesFileName) {
    super(propertiesFileName);
  }

  public boolean isSuitable(KnowledgeBase kb) {
    return kb instanceof OWLModel;
  }

  protected final OwlKbHelper getOwlHelper(){
    return OwlKbHelper.getHelper(getModel());
  }

  public OWLModel getModel() {
    return (OWLModel) getKnowledgeBase();
  }
}
