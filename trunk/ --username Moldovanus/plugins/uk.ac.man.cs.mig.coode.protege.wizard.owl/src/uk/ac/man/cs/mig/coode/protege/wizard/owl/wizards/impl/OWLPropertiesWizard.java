package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionsPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.SlotTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2004
 */
public class OWLPropertiesWizard extends AbstractOWLWizard {

  protected static final String namesProp = "namesProp";
  protected static final String superPropertyProp = "superProp";
  protected static final String rootSelectionProp = "rootSelectionProp";
  protected static final String typeProp = "typeProp";

  private static final String OBJECTPROP = "Object Property";
  private static final String DATATYPEPROP = "Datatype Property";
  private static final String ANNOTPROP = "Annotation Property";

  private ListEntryPage propertiesEntryPage;
  private Collection rootSlots;

  public boolean isSuitable(KnowledgeBase knowledgeBase) {
    return knowledgeBase instanceof OWLModel;
  }

  protected void initialise() throws NullPointerException, WizardInitException {

    rootSlots = getHelper().getRootSlots();
    if (rootSlots != null) {
      addProperty(rootSelectionProp, rootSlots);
      addProperty(superPropertyProp, null);
      addPage(new SlotTreePage("0", superPropertyProp, rootSelectionProp));
    }

    addUniqueProperty(namesProp, null);

    StringValidator validator = getDefaultValidator();
    propertiesEntryPage = new ListEntryPage("1", namesProp, validator, true);
    addPage(propertiesEntryPage);

    List types = new ArrayList(3);
    types.add(OBJECTPROP);
    types.add(DATATYPEPROP);
    types.add(ANNOTPROP);
    addProperty(typeProp, OBJECTPROP);
    addPage(new OptionsPage("2", typeProp, types));
  }

  protected boolean handleFinished(Console output) {
    boolean result = true;

    OWLProperty superProp = null;

    if (rootSlots != null) {
      superProp = (OWLProperty) getProperty(superPropertyProp);
    }

    String typeOpt = (String) getProperty(typeProp);
    int type = OwlKbHelper.PROP_TYPE_OBJECT;
    if (typeOpt.equals(DATATYPEPROP)) {
      type = OwlKbHelper.PROP_TYPE_DATATYPE;
    }
    else if (typeOpt.equals(ANNOTPROP)) {
      type = OwlKbHelper.PROP_TYPE_ANNOTATION;
    }

    getHelper().setIdGenerator(WizardAPI.getWizardManager().getDefaultIdGenerator());

    Object propertyNames = getProperty(namesProp);

    if (propertyNames instanceof Collection) {
      Collection newProperties = (Collection) propertyNames;
      for (Iterator i = newProperties.iterator(); i.hasNext();) {
        String newPropName = (String) i.next();
        getOwlHelper().createProperty(newPropName, superProp, type);
      }
    }
    else if (propertyNames instanceof MutableTreeNode) {
      getOwlHelper().createSubproperties((MutableTreeNode) propertyNames, superProp, type);
    }
    else {
      result = false;
    }

    return result;
  }
}
