package uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards;

import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Nov-2004
 */
public class SeperateIdWizard extends AbstractWizard {

  private static final String propNameProp = "propNameProp";

  public SeperateIdWizard() {
//    this("Seperate ID",
//         "Moves the name of each class into a specified slot and renames the class with an auto-generated ID",
//         "Wizards");
  }

  protected void initialise() throws NullPointerException, WizardInitException {
//    setIntroHtml(SeperateIdWizard.class.getWizardProperty("html" + File.separator + "sepId.html"));
//
//    addProperty(propNameProp, "conceptName");
//
//    addPage(new ValidatedStringEntryPage("Create Property", propNameProp, getDefaultValidator(), "Create a new property to store the human-readable name for the class"));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
//    String propName = (String) getUserOption(propNameProp);
//    Slot newProp = KbUtils.createProperty(propName);
//
//    IdGenerator idGen = NumericConceptIdGenerator.getInstance("id_", 6);
//
//    KnowledgeBase kb = getKnowledgeBase();
//    Collection allClasses = KbUtils.getUserClasses(kb.getSubclasses(kb.getRootCls()));
//    for (Iterator i = allClasses.iterator(); i.hasNext();) {
//      Cls current = (Cls) i.next();
//      String currentName = current.getWizardLabel();
//      output.out("transforming class: " + currentName);
//      current.setDirectOwnSlotValue(newProp, currentName);
//      current.setName(idGen.getNextId());
//    }
//
//    GuiUtils.setBrowserSlot(newProp);
//
    return true;
  }
}
