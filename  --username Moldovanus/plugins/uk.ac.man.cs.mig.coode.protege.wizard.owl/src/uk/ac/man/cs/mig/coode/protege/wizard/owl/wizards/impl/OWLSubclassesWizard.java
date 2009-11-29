package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.EnterSubclassesWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2004
 */
public class OWLSubclassesWizard extends EnterSubclassesWizard {

  protected static final String disjProp = "disjProp";

  public boolean isSuitable(KnowledgeBase knowledgeBase) {
    return knowledgeBase instanceof OWLModel;
  }

  protected void initialise() throws NullPointerException, WizardInitException {
    super.initialise();

    addProperty(disjProp, Boolean.TRUE);

    addPage(new OptionPage("2", disjProp));

    OwlKbHelper helper = OwlKbHelper.getHelper((OWLModel)getKnowledgeBase());
    getClassesEntryPage().setPrefixes(helper.getNamespacePrefixes(true));
  }

  protected boolean handleFinished(Console output) {

    // create all the classes (using the parent)
    boolean result = super.handleFinished(output);

    // now add disjoints if appropriate
    if (((Boolean) getProperty(disjProp)).booleanValue()) {
      OWLNamedClass superclass = (OWLNamedClass) getProperty(superclassProp);

      // ensure all direct subclasses are disjoint (also with their existing siblings)
      OWLUtil.ensureSubclassesDisjoint(superclass);

      // then, only sort out disjoints for the new class trees
      try{
        Object names = getProperty(namesProp);
        if (names instanceof DefaultMutableTreeNode) {
          Collection subsubclasses = getHelper().getChildrenClasses(superclass, (DefaultMutableTreeNode) names);
          if (subsubclasses != null) {
            OwlKbHelper.ensureAllSubclassesDisjoint(subsubclasses);
          }
        }
      }
      catch(Exception e){
        e.printStackTrace();
        result = false;
      }
    }

    return result;
  }
}
