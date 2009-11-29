package uk.ac.man.cs.mig.coode.protege.wizard.page;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardPage;

import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Jun-2005
 */
public interface ProtegeWizardPage {

  void setProtegeWizard(ProtegeWizard wizard);
  Object getPropValue();
  String getName();

  void addKeyListener(KeyListener l);
  void removeKeyListener(KeyListener l);

  WizardPage getWizardPage();
}
