package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.table.annotation.AnnotationEntry;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.table.annotation.AnnotationTableComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MultiSelectPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.util.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Aug-2004
 */
public class AnnotationWizard extends AbstractOWLWizard {

  private static final String annotProp = "annotationsProp";
  private static final String classesProp = "classesProp";

  private static AbstractList annotations = null;

  protected void initialise() throws NullPointerException, WizardInitException {

    Collection selectedClasses = null;
    Frame selection = getKbEntity();
    if (selection != null){
      selectedClasses = Collections.singleton(selection);
    }
    addProperty(classesProp, selectedClasses);
    addProperty(annotProp, annotations);

    addPage(new MultiSelectPage("0", classesProp, getKnowledgeBase().getRootCls()));

    AbstractWizardEditor tableEditor = new AnnotationTableComponent();
    addPage(new ArbitraryComponentPage("1", annotProp, tableEditor));
  }

  protected boolean handleFinished(Console output) {
    //printProperties();

    Collection selectedClasses = (Collection) getProperty(classesProp);
    annotations = (AbstractList) getProperty(annotProp);

    if (annotations != null) {
      output.out("Adding annotations...");
      for (Iterator i = annotations.iterator(); i.hasNext();) {
        AnnotationEntry currentEntry = (AnnotationEntry) i.next();
        output.out("  " + currentEntry.toString());
        for (Iterator j = selectedClasses.iterator(); j.hasNext();) {
          getOwlHelper().addClassAnnotation((OWLClass) j.next(),
                                        currentEntry.annotationProp,
                                        currentEntry.value,
                                        currentEntry.lang);
        }
      }
    }

    return true;
  }

  public List canBeLaunched() {
    List feedback = null;
    if (getModel().getOWLThingClass().getSubclassCount() == 0){
      feedback = CollectionUtilities.createList(getWizardProperty("error.noclasses"));
    }
    return feedback;
  }
}
