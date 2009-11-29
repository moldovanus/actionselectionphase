package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         13-Feb-2005
 */
public class ImportWizard extends AbstractOWLWizard {

  public static final String fileProp = "fileProp";

  public ImportWizard(String wizardName, String wizardDescription) {
    //super(wizardName, wizardDescription, "Wizards");
  }

  public ImportWizard() {
    //super("Import local ontology", "Import an ontology from a local file into this one", "Wizards");
  }

  protected void initialise() throws NullPointerException, WizardInitException {
//    addProperty(fileProp, null);
//
//    addPage(new ArbitraryComponentPage("Local File", fileProp, new FileSelectorComponent("owl"), "Select a local ontology file to import"));
  }

  protected boolean handleFinished(Console output) // could return errors
  {

    return false;  // @@TODO change default
  }
}
