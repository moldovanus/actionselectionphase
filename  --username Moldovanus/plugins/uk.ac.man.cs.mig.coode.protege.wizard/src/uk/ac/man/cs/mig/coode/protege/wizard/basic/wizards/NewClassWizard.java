package uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards;

import edu.stanford.smi.protege.model.*;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextField;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.BasicNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.ProtegeFrameNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Jul-2004
 */
public class NewClassWizard extends AbstractWizard {

  protected static final String nameProp = "newClassName";
  protected static final String superclassProp = "superProp";
  protected static final String rootSelectionProp = "rootSelectionProp";

  protected void initialise() throws NullPointerException, WizardInitException {

    addProperty(nameProp, null);
    addProperty(rootSelectionProp, getHelper().getRootCls());

    // try to start from a sensible class (ie the frameaction one or the first selected
    Frame currentSelectedFrame = getKbEntity();
    if (currentSelectedFrame == null){
      currentSelectedFrame = getHelper().getRootCls();
    }
    addProperty(superclassProp, currentSelectedFrame);
    addPage(new ClassTreePage("1", superclassProp, rootSelectionProp));

    // create the validated entry component for the class name entry
    // the classname entry validation depends on if the display slot is set
    AbstractWizardEditor nameEntryEditor = null;

    KbHelper helper = getHelper();
    KnowledgeBase kb = getKnowledgeBase();
    if ((helper.classDisplaySlotIsName(kb.getRootCls())) ||
        (!KbHelper.isGoodDisplaySlot(KbHelper.getDisplaySlot(kb.getRootCls())))) {
      // for standard frame names
      nameEntryEditor = new ValidatedTextField(new ProtegeFrameNameValidator(kb, this));
    }
    else {
      // for other "free text" slots
      nameEntryEditor = new ValidatedTextField(new BasicNameValidator(this));
    }

    addPage(new ArbitraryComponentPage("0", nameProp, nameEntryEditor));
  }

  protected boolean handleFinished(Console output) {

    String newClassName = (String) getProperty(nameProp);
    Object parent = getProperty(superclassProp);

    getHelper().setIdGenerator(WizardAPI.getWizardManager().getDefaultIdGenerator());

    Cls newClass = (Cls) getHelper().createClassUsingBrowserSlot(newClassName, parent);

    WizardAPI.getWizardManager().setActiveFrameInUI(newClass);

    return (newClass != null);
  }


  public List canBeLaunched() {
    List errors = null;
    Cls meta = getKnowledgeBase().getRootCls().getDirectType();
    Slot browserSlot = meta.getBrowserSlotPattern().getFirstSlot();
    if (browserSlot == null || (browserSlot.getName().equals(Model.Slot.NAME))) {
      errors = new ArrayList();
      errors.add(getError("nobrwsrslot"));
    }
    return errors;
  }
}
