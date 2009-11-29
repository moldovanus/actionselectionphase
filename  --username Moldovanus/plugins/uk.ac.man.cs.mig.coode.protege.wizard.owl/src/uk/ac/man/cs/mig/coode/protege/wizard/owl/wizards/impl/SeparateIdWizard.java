package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.GuiUtils;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;
import uk.ac.man.cs.mig.coode.protege.wizard.util.NumericConceptIdGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Feb-2005
 */
public class SeparateIdWizard extends AbstractOWLWizard {

  private static final String propNameProp = "propNameProp";
  private static final String prefixProp = "prefixProp";
  private static final String paddedProp = "paddedProp";
  private static final String langProp = "langProp";

  public SeparateIdWizard() {
    this("Separate ID", "Moves the name of each class into a specified slot and renames the class with an auto-generated ID");
  }

  public SeparateIdWizard(String wizardName, String wizardDescription) {
//    super(wizardName, wizardDescription, "Wizards");
  }

  public List canBeLaunched() {
    List errors = null;
    Cls meta = getModel().getOWLNamedClassClass();
    Slot browserSlot = meta.getBrowserSlotPattern().getFirstSlot();
    if (browserSlot != null && (!browserSlot.getName().equals(Model.Slot.NAME))) {
      errors = new ArrayList();
      errors.add(getError("brwsrslotset"));
    }
    return errors;
  }

  protected void initialise() throws NullPointerException, WizardInitException {
//    // create a new annot prop to contain the name, and generate ids
//    setIntroHtml(this.getClass().getWizardProperty("html/separateId.html"));
//
//    IdGenerator idGen = WizardAPI.getDefaultIdGenerator();
//    addProperty(propNameProp, "conceptName");
//    addProperty(prefixProp, idGen.getPrefix());
//    addProperty(paddedProp, new Integer(idGen.getPadding()));
//    addProperty(langProp, getModel().getDefaultLanguage());
//
//    addPage(new ValidatedStringEntryPage("Label Property", propNameProp, getDefaultValidator(), "Create a new property to store the human-readable label for the class"));
//    addPage(new StringEntryPage("Prefix", prefixProp, "Please enter a prefix for your numeric IDs"));
//    addPage(new StringEntryPage("Language", langProp, "Please enter a 2 character language tag if required"));
//    addPage(new IntegerPage("Padding?", paddedProp, "If you would like padding, please enter the length of the number string, or 0 for no padding"));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    OWLModel model = (OWLModel) getKnowledgeBase();
    String propName = (String) getProperty(propNameProp);
    String prefix = (String) getProperty(prefixProp);
    String lang = (String) getProperty(langProp);
    int paddedLen = ((Integer) getProperty(paddedProp)).intValue();
    OWLDatatypeProperty newProp = model.createAnnotationOWLDatatypeProperty(propName);

    // save this back into the default for future wizards to use
    IdGenerator idGen = new NumericConceptIdGenerator(prefix, 0, paddedLen, getKnowledgeBase());
    WizardAPI.getWizardManager().setDefaultIdGenerator(idGen);

    // switch name with ID
    Collection allClasses = model.getUserDefinedOWLNamedClasses();
    for (Iterator i = allClasses.iterator(); i.hasNext();) {
      OWLClass current = (OWLClass) i.next();
      String currentName = current.getName();
      output.out("transforming class: " + currentName);
      current.setPropertyValue(newProp, model.createRDFSLiteral(currentName, lang));
      current.rename(idGen.getNextId());
    }

    GuiUtils.setBrowserSlot(newProp);

    return true;
  }
}
