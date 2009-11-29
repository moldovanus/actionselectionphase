package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.test;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import javax.swing.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class BasicOWLWizard extends AbstractWizard {

  public BasicOWLWizard() {
    //super("Basic OWL only wizard", "This should only be visible for OWL Knowledgebases", "Wizards");
  }

  public BasicOWLWizard(String s, String s1) {
    //super(s, s1, "Wizards");
  }

  public void initialise() throws NullPointerException {
    JOptionPane.showConfirmDialog(null, "This is an OWL only wizard");
  }

  protected boolean handleFinished(Console output) {
    return true;
  }

  public boolean isSuitable(KnowledgeBase knowledgeBase) {
    return (knowledgeBase instanceof OWLModel);
  }
}
