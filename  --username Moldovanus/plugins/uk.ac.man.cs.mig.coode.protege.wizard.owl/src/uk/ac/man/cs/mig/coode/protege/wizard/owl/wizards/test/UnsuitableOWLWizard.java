package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.test;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import javax.swing.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class UnsuitableOWLWizard extends AbstractWizard {

  public UnsuitableOWLWizard() {
    this("OWL Wizard forgetting suitability test", "This should be visible for all Knowledgebases");
  }

  public UnsuitableOWLWizard(String s, String s1) {
    //super(s, s1, "Wizards");
  }

  public void initialise() throws NullPointerException {
    JOptionPane.showConfirmDialog(null, "This should be an OWL only wizard");
  }

  protected boolean handleFinished(Console output) {
    return true;
  }
}
