package uk.ac.man.cs.mig.coode.protege.wizard.owl.action;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import uk.ac.man.cs.mig.coode.protege.wizard.icon.WizardIcons;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.AbstractLaunchWizardFrameAction;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl.OWLIndividualsWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2004
 */
public class CreateIndividualsAction extends AbstractLaunchWizardFrameAction {

  public CreateIndividualsAction() {
    super("Create individuals...", WizardIcons.getHatIcon());
  }

  public boolean isSuitable(Component component, RDFResource resource) {
    return (resource instanceof OWLClass);
  }

  protected ProtegeWizard createWizard() {
    return new OWLIndividualsWizard();
  }
}
