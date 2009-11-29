package uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Aug-2004
 */
public class EnterInstancesWizard extends AbstractWizard {

  protected static final String namesProp = "namesProp";
  protected static final String superclassProp = "superProp";
  protected static final String rootSelectionProp = "rootSelectionProp";

  protected void initialise() throws NullPointerException, WizardInitException {

    addUniqueProperty(namesProp, null);
    addProperty(rootSelectionProp, getHelper().getRootCls());

    Frame currentSelectedFrame = getKbEntity(); // in case we've been started from a frameaction

    if (currentSelectedFrame == null){
      currentSelectedFrame = getHelper().getRootCls();
    }

    addProperty(superclassProp, currentSelectedFrame);
    addPage(new ClassTreePage("0", superclassProp, rootSelectionProp));
    addPage(new ListEntryPage("1", namesProp, getDefaultValidator(), false));
  }

  protected boolean handleFinished(Console output) { // could return errors
    boolean result = true;

    Cls cls = (Cls) getProperty(superclassProp);
    Object instances = getProperty(namesProp);

    if (instances instanceof Collection) {
      getHelper().createInstances((Collection) instances, cls);
    }
    else if (instances instanceof DefaultMutableTreeNode) {
      System.err.println("HHHEEEEEEEELLLLLLLLLPPPPPPP");
    }
    else {
      result = false;
    }

    return result;
  }
}
