package uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public interface WizardContainer {

  public boolean addWizard(ProtegeWizard theWizard);

  public boolean removeWizard(ProtegeWizard theWizard);
}
