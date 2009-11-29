package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect.SwitchableMultiSelectComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         31-Mar-2005
 */
public class MoveRestrictionToAncestorClassWizard extends AbstractOWLWizard{

  protected String promptText = "Enter subclasses below";
  protected static final String classProp = "classProp";
  protected static final String rootSelectionProp = "rootSelectionProp";
  protected static final String restrsProp = "restrsProp";
  private SwitchableMultiSelectComponent restrSelector;

  public MoveRestrictionToAncestorClassWizard() {
    //super("Move Restriction", "Move a Restriction to an ancestor class", "Refactor");
  }

  public MoveRestrictionToAncestorClassWizard(String wizardName, String wizardDescription, String wizardMenu) {
    //super(wizardName, wizardDescription, wizardMenu);
  }

  protected void initialise() throws NullPointerException, WizardInitException {
//
//    addProperty(rootSelectionProp, KbUtils.getRootCls());
//    addProperty(restrsProp, null);
//
//    Frame currentSelectedFrame = getKbEntity(); // in case we've been started from a frameaction
//
//    if (currentSelectedFrame != null){
//      addProperty(classProp, currentSelectedFrame);
//    }
//    else{
//      // only select a superclass if none already selected
//      addProperty(classProp, KbUtils.getRootCls());
//      addPage(new ClassTreePage("select class", classProp, "select a class to move restrictions from", rootSelectionProp));
//    }
//
//    addPropertyChangeListener(classProp, new PropertyChangeListener(){
//      public void propertyChange(PropertyChangeEvent evt) {
//        Collection supers = ((OWLClass)evt.getNewValue()).getSuperclasses(false);
//        System.out.println("supers = " + supers);
//        restrSelector.setRoot(supers);
//        //To change body of implemented methods use File | Settings | File Templates.
//      }
//    });
//
//    restrSelector = new SwitchableMultiSelectComponent((Collection)null, false);
//    addPage(new ArbitraryComponentPage("select restrictions", restrsProp, restrSelector, "Select the restrictions you would like to move"));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
