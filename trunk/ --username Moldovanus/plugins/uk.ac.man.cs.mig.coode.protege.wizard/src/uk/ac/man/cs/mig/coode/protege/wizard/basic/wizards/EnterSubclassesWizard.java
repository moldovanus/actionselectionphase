package uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Aug-2004
 */
public class EnterSubclassesWizard extends AbstractWizard {

  protected static final String namesProp = "namesProp";
  protected static final String superclassProp = "superProp";
  protected static final String rootSelectionProp = "rootSelectionProp";

  private ListEntryPage classesEntryPage;
  
  protected void initialise() throws NullPointerException, WizardInitException {

    addUniqueProperty(namesProp, null);
    addProperty(rootSelectionProp, getHelper().getRootCls());

    // try to start from a sensible class (ie the frameaction one or the first selected
    Frame currentSelectedFrame = getKbEntity();
    if (currentSelectedFrame == null){
      currentSelectedFrame = getHelper().getRootCls();
    }
    addProperty(superclassProp, currentSelectedFrame);
    addPage(new ClassTreePage("0", superclassProp, rootSelectionProp));

    StringValidator validator = getDefaultValidator();
    classesEntryPage = new ListEntryPage("1", namesProp, validator, true);
    addPage(classesEntryPage);
  }

  protected boolean handleFinished(Console output) { // could return errors
    boolean result = true;

    Cls superclass = (Cls) getProperty(superclassProp);
    Object classes = getProperty(namesProp);

    getHelper().setIdGenerator(WizardAPI.getWizardManager().getDefaultIdGenerator());

    if (classes instanceof Collection) {
      Collection newClasses = (Collection) classes;
      for (Iterator i = newClasses.iterator(); i.hasNext();) {
        String newClassName = (String) i.next();
        getHelper().createClass(newClassName, superclass);
      }
    }
    else if (classes instanceof DefaultMutableTreeNode) {
      getHelper().createSubclasses((DefaultMutableTreeNode) classes, superclass);
    }
    else {
      result = false;
    }

    return result;
  }

  public ListEntryPage getClassesEntryPage() {
    return classesEntryPage;
  }

  // testing purposes

  private List createLargeList(int size) {
    List list = new ArrayList(size);
    for (int i=0; i<size; i++){
      list.add("myClass" + i);
    }
    return list;
  }
}
