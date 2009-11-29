package uk.ac.man.cs.mig.coode.protege.wizard.event;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardEvent;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardEventListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jul-2004
 */
public abstract class WizardEventAdapter implements WizardEventListener {

  public void nextPressed(WizardEvent event) {
  }

  public void prevPressed(WizardEvent event) {
  }

  public void pageChanged(WizardEvent event) {
  }

  public void finishPressed(WizardEvent event) {
  }

  public void cancelPressed(WizardEvent event) {
  }
}
