package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import uk.ac.man.cs.mig.coode.protege.wizard.event.ProtegeWizardEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         01-Dec-2004
 */
class IllegalWizardStateException extends Exception {

  public IllegalWizardStateException(int currentState, ProtegeWizardEvent event) {
    super();
    System.err.println("Illegal Wizard State - STATE: " + WizardStateMachine.printState(currentState) + ", EVENT: " + event);
  }
}
