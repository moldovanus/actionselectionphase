package uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import javax.swing.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class StandardProtegeTestWizard extends AbstractWizard {

//  public StandardProtegeTestWizard() {
//    super("Simple Basic Protege Wizard", "This has no problem with any normal KnowledgeBase", "Wizards");
//  }
//
//  public StandardProtegeTestWizard(String s, String s1, String s2) {
//    super(s, s1, s2);
//  }

  protected void initialise() throws NullPointerException {
    JOptionPane.showConfirmDialog(null, "this is a basic normal protege wizard");
  }

  protected boolean handleFinished(Console output) {
    return true;
  }
}
