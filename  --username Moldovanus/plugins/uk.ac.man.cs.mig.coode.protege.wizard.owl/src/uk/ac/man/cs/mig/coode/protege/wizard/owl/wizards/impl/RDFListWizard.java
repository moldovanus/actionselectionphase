package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.validation.ResourceNSValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ValidatedStringEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         30-Nov-2004
 */
public class RDFListWizard extends AbstractOWLWizard {

  private static final boolean debug = false;

  private static final String rootClsProp = "rootClsProp";
  private static final String elementClsNameProp = "elementClsNameProp";
  private static final String elementClsProp = "elementClsProp";
  private static final String appliedClsProp = "appliedClsProp";
  private static final String propNameProp = "propNameProp";

  private static final boolean functionalDefault = true;
  public static final String propertyPrefix = "has";

  protected void initialise() throws NullPointerException, WizardInitException {

    Cls owlThing = getHelper().getRootCls();
    addProperty(rootClsProp, owlThing);
    addProperty(elementClsNameProp, null);
    addProperty(elementClsProp, owlThing);
    addProperty(propNameProp, null);
    addProperty(appliedClsProp, owlThing);

    PropertyChangeListener elementClsPropChangeListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        String elementClsNameValue = (String) getProperty(elementClsNameProp);
        Cls elementClsValue = (Cls) getProperty(elementClsProp);

        String clsName = null;
        if (elementClsNameValue != null) {
          clsName = elementClsNameValue;
        }
        else {
          clsName = elementClsValue.getBrowserText();
        }
        String propName = ResourceNSValidator.prependTextToName(clsName, propertyPrefix) + "s";
        setProperty(propNameProp, propName);
      }
    };
    addPropertyChangeListener(elementClsNameProp, elementClsPropChangeListener);
    addPropertyChangeListener(elementClsProp, elementClsPropChangeListener);

    addPage(new ValidatedStringEntryPage("0", elementClsNameProp, getDefaultValidator()));
    addPage(new ClassTreePage("1", elementClsProp, rootClsProp));
    addPage(new ValidatedStringEntryPage("2", propNameProp, getDefaultValidator()));
    addPage(new ClassTreePage("3", appliedClsProp, rootClsProp));
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    boolean result = false;

    String elementClsName = (String) getProperty(elementClsNameProp);
    OWLNamedClass elementCls = (OWLNamedClass) getProperty(elementClsProp);
    OWLNamedClass appliedCls = (OWLNamedClass) getProperty(appliedClsProp);
    String elementPropName = (String) getProperty(propNameProp);

    if (debug) System.out.println("elementClsName = " + elementClsName);

    if ((elementClsName != null) && !(elementClsName.equals(""))) {
      OWLNamedClass elementSuperclass = elementCls;
      if (debug) System.out.println("Creating element class");
      elementCls = (OWLNamedClass) getHelper().createClass(elementClsName, elementSuperclass);
    }

    if (elementCls != null) {

      OwlKbHelper helper = getOwlHelper();

      OWLModel okb = (OWLModel) getKnowledgeBase();

      String listClsName = elementCls.getBrowserText() + "List";
      RDFSNamedClass rdfListRootCls = (RDFSNamedClass) okb.getRDFListClass();
      OWLNamedClass newListCls = (OWLNamedClass) helper.createClass(listClsName, rdfListRootCls);

      if (newListCls != null) {

        // create a property for using the list
        OWLObjectProperty hasListProp = helper.createNewObjectProperty(elementPropName, appliedCls, newListCls, functionalDefault);

        if (appliedCls != helper.getRootCls()) {
          // add a restriction associating this list with the applied class
          helper.addSomeValuesFromRestriction(appliedCls, hasListProp, newListCls);
        }

        // create a restriction on our new list to state that it can only take
        // elements of the correct type
        RDFProperty rdfFirstProp = okb.getRDFFirstProperty();
        result = helper.addAllValuesFromRestriction(newListCls, rdfFirstProp, elementCls);

        // and that the rest of the list can only be of this subclass of rdf:list
        Collection classes = new ArrayList();
        classes.add(newListCls);

        Instance rdfNil = okb.getRDFNil();
        //Instance) CollectionUtilities.getFirstItem(owlModel.getInstances(rdfListRootCls));
        //Instance rdfNil = owlModel.createInstance(null, rdfListRootCls);
        //RDFModel.Instance.NIL;

        Collection clses = CollectionUtilities.createCollection(rdfNil);
        OWLEnumeratedClass nillNominal = okb.createOWLEnumeratedClass(clses);
        classes.add(nillNominal);
        OWLUnionClass newListOrNilClass = okb.createOWLUnionClass(classes);

        result = helper.addAllValuesFromRestriction(newListCls, okb.getRDFRestProperty(), newListOrNilClass);
      }

      if (result) {
        // make rdf:list visible
        rdfListRootCls.setVisible(true);
        WizardAPI.getWizardManager().updateUI();
      }
    }

    return result;
  }
}