package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.AbstractOWLWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Oct-2004
 */
public class ReifyRelationWizard extends AbstractOWLWizard {

  private static final String naryPropertyProp = "naryPropertyProp";
  private static final String domainClsProp = "domainClsProp";
  private static final String classRootProp = "classRootProp";
  private static final String propRootProp = "propRootProp";
  private static final String attributePropertyNamesProp = "attributePropertyNamesProp";
  private static final String propRangeProp = "propRangeProp";
  private static final String propertyModelProp = "propertyModelProp";
  private static final String superclassProp = "superPropertyProp";

  private ListEntryPage attributesPage;

  public ReifyRelationWizard() {
    //super("Reify existing Relation", "Turn a property into a class, to allow further things to be said about a relation", "Wizards");
  }

  protected void initialise() throws NullPointerException, WizardInitException {
//    addUniqueProperty(naryPropertyProp, null);
//    addUniqueProperty(domainClsProp, null);
//    addUniqueProperty(propRootProp, getKnowledgeBase().getRootSlots());
//    addUniqueProperty(classRootProp, getKnowledgeBase().getRootCls());
//    addUniqueProperty(attributePropertyNamesProp, CollectionUtilities.createCollection("value"));
//    addUniqueProperty(propRangeProp, CollectionUtilities.createCollection("range"));
//    addUniqueProperty(propertyModelProp, new PropRangeMatrixModel(this, propRangeProp, attributePropertyNamesProp));
//
//    addPropertyChangeListener(naryPropertyProp, new PropertyChangeListener() {
//      public void propertyChange(PropertyChangeEvent evt) {
//        attributesPage.setPrefixText(((OWLClass) evt.getNewValue()).getBrowserText() + "_");
//      }
//    });
//
//    StringValidator v = getDefaultValidator();
//    //addPage(new ValidatedStringEntryPage("property name", naryPropertyProp, v, "Please enter the name of the property you would like to create (eg has_temperature)"));
//    addPage(new SlotTreePage("select property", naryPropertyProp, "Please select a property to reify", propRootProp, false));
//    addPage(new ClassTreePage("select domain", domainClsProp, "Select a domain for the new property (if required)", classRootProp, false));
//    attributesPage = new ListEntryPage("property attributes", attributePropertyNamesProp, v, "Please enter the attributes you would like to associate with the relationshiop (eg has_value)", false, OwlKbUtils.getNamespacePrefixes(true));
//    addPage(attributesPage);
//
//    addPage(new MatrixPage("select ranges", propertyModelProp,
//                           new FrameTableCellRendererFactory(),
//                           new FrameTableCellEditorFactory("select a range"),
//                           "Please enter a range for each attribute if required (Select an existing class or create a new one)"));
//
//    Cls enumRoot = OwlPatternUtils.getDefaultPatternRoot(OwlPatternUtils.PATTERN_NARY_RELATION);
//    if (enumRoot != null) {
//      addProperty(superPropertyProp, enumRoot);
//      addPage(new ClassTreePage("select superclass", superPropertyProp, "Where would you like to put your new class?", classRootProp));
//    }
//    else {
//      addProperty(superPropertyProp, OwlPatternUtils.getDefaultPatternRootName(OwlPatternUtils.PATTERN_NARY_RELATION));
//      addPage(new ValidatedStringEntryPage("create root", superPropertyProp, v, "What would you like to name the root class of all nary relations?"));
//    }
  }

  protected boolean handleFinished(Console output) // could return errors
  {
    OWLNamedClass naryRootCls = null;
    Object o = getProperty(superclassProp);
    if ((o == null) || (o instanceof OWLNamedClass)) {
      naryRootCls = (OWLNamedClass) o;
    }
    else {
      naryRootCls = (OWLNamedClass) getHelper().createClassAtRoot((String) o);
    }
    return false;
//    return OwlPatternUtils.createNaryRelation1(naryRootCls,
//                                               (String) getUserOption(naryPropertyProp),
//                                               (OWLNamedClass) getUserOption(domainClsProp),
//                                               (Collection) getUserOption(attributePropertyNamesProp));
  }
}
