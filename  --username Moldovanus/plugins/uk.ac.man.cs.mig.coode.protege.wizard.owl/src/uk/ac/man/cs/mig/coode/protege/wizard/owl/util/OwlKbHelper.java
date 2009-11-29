package uk.ac.man.cs.mig.coode.protege.wizard.owl.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.GuiUtils;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.PromptPanel;
import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class OwlKbHelper extends KbHelper {

  public static final int PROP_TYPE_OBJECT = 0;
  public static final int PROP_TYPE_DATATYPE = 1;
  public static final int PROP_TYPE_ANNOTATION = 2;

  private static final boolean debug = false;
  protected OWLModel owlModel;

  public static OwlKbHelper getHelper(OWLModel okb) {
    // if not yet created or subclass, create (or upgrade)
    if (helper == null || helper.getClass() == KbHelper.class) {
      helper = new OwlKbHelper();
    }
    ((OwlKbHelper) helper).setModel(okb);
    return (OwlKbHelper) helper;
  }

  protected void setModel(OWLModel okb) {
    this.owlModel = okb;
    super.setKnowledgeBase(okb);
  }

  public String getPrefixForImport(URI importURI) {
    String namespace = importURI.toString();
    if (!namespace.endsWith("#") && !namespace.endsWith("/")) {
      namespace += "#";
    }
    if (debug) {
		System.out.println("m.getDefaultOWLOntology().getName() = " + owlModel.getDefaultOWLOntology().getName());
	}
    return owlModel.getNamespaceManager().getPrefix(namespace);
  }

  public boolean isImported(URI importURI) {
    String namespace = importURI.toString();
    if (!namespace.endsWith("#") && !namespace.endsWith("/")) {
      namespace += "#";
    }
    //OWLOntology owlOntology = owlModel.getDefaultOWLOntology();
    return owlModel.getAllImports().contains(importURI.toString());
  }

  public OWLProperty createProperty(String childName, OWLProperty parent, int type) {
    OWLProperty property = null;
    switch (type) {
      case PROP_TYPE_OBJECT:
        property = owlModel.createOWLObjectProperty(childName);
        break;
      case PROP_TYPE_DATATYPE:
        property = owlModel.createOWLDatatypeProperty(childName);
        break;
      case PROP_TYPE_ANNOTATION:
        property = owlModel.createAnnotationOWLObjectProperty(childName);
    }
    if (parent != null) {
      property.addSuperproperty(parent);
    }
    return property;
  }

  @Override
  public Instance createInstance(String newInstanceName, Cls type) {
	    if (type == null) {
	        type = owlModel.getRootCls();
	      }

	      GuiUtils.getConsole().out("Creating individual of " + type.getBrowserText() + ": " + newInstanceName);

	      Instance inst = null;
	      if (type instanceof RDFSNamedClass) {
	    	  inst = ((RDFSNamedClass)type).createRDFIndividual(newInstanceName);
	      } else {
	    	  //we're in trouble if this is the case
	    	  inst = type.createDirectInstance(newInstanceName);
	      }

	      return inst;
  }

  /**
   * Recursively create a tree of new properties starting from topParent
   *
   * @param directParent root of tree where all user objects are names for new properties
   *                     these should already be verified as reasonable (and unique) frame names
   * @param topParent    the topmost parent (as the directParent does not need to know this)
   */
  public Collection createSubproperties(MutableTreeNode directParent,
                                        OWLProperty topParent,
                                        int type) {

    int numChildren = directParent.getChildCount();
    Collection addedClasses = new ArrayList(numChildren);

    for (int i = 0; i < numChildren; i++) {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) directParent.getChildAt(i);
      String childName = (String) currentNode.getUserObject();
      OWLProperty property = createProperty(childName, topParent, type);
      addedClasses.add(property);
      addedClasses.addAll(createSubproperties(currentNode, property, type));
    }

    return addedClasses;
  }

  public boolean ensureOntologyImported(URI uri, String prefix, File file) {

    String namespace = uri.toString();
    if (!namespace.endsWith("#") && !namespace.endsWith("/")) {
      namespace += "#";
    }

    if (file == null) {
      if (JOptionPane.showConfirmDialog(Application.getMainWindow(),
                                        "Note: This will import the ontology from\n" + uri,
                                        "Adding Import...", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
        return false;
      }
    }
    else {
      if (JOptionPane.showConfirmDialog(Application.getMainWindow(),
                                        "Note: This will add an alias for the URI\n" + uri + "\nto point to the local file\n" + file,
                                        "Adding Import...", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
        return false;
      }

      //Protege 3.2 specific code
      if (owlModel.getRepositoryManager().getRepository(uri) == null) {
        owlModel.getRepositoryManager().addGlobalRepository(new LocalFileRepository(file));
      }
    }
    owlModel.getNamespaceManager().setPrefix(namespace, prefix);

    try {
    	ImportHelper importHelper = new ImportHelper(owlModel);
    	importHelper.addImport(uri);
    	importHelper.importOntologies(false); //TT - check if needs reload of UI
    }
    catch (Exception e) {
      Log.getLogger().log(Level.WARNING, "Errors at importing " + uri, e);
      return false;
    }
    return true;
  }

  public AbstractList getClassAnnotationProperties() {
    AbstractList result = new ArrayList();
    Collection slots = owlModel.getOWLAnnotationProperties();
    for (Iterator i = slots.iterator(); i.hasNext();) {
      RDFProperty annotationSlot = (RDFProperty) i.next();
      if (debug) {
		System.out.println("annotationSlot.isEditable() = " + annotationSlot.isEditable());
	}
      if (!NamespaceUtil.getPrefixedName(owlModel, annotationSlot.getName()).startsWith("owl:")) {
        result.add(annotationSlot);
      }
    }
    return result;
  }

  /**
   * Add an annotation to an individual. Functional annotation properties which are duplicated
   * or in existance will be overwritten with the last value
   */
  public void addAnnotation(RDFResource resource, OWLProperty annotProp, Object value, String lang) {
    try {

      GuiUtils.getConsole().out("Adding annotation on " + resource.getBrowserText() +
                                ": " + annotProp.getBrowserText() + "=" + value);

      // create a literal and add the language string to the value if set
      if (value instanceof String) {
        if (lang != null) {
          value = owlModel.createRDFSLiteral((String) value, lang);
        }
        else {
          value = owlModel.createRDFSLiteral(value);
        }
      }

      if (annotProp.isFunctional()) {
        resource.setPropertyValue(annotProp, value);
      }
      else {
        Collection allValues = resource.getPropertyValues(annotProp);

        // must create a new collection as above is unmodifiable
        Collection newValues = new ArrayList(allValues);
        newValues.add(value);

        resource.setPropertyValues(annotProp, newValues);
      }
    }
    catch (NullPointerException e) {
      System.err.println("Failed to add annotation-");
      System.err.println("resource: " + resource);
      System.err.println("annotProp: " + annotProp);
    }
  }

  public void addClassAnnotation(OWLClass cls, OWLProperty annotProp, Object value, String lang) {
    addAnnotation(cls, annotProp, value, lang);
  }

  public void addPropertyAnnotation(OWLProperty property, OWLProperty annotProp, Object value, String lang) {
    addAnnotation(property, annotProp, value, lang);
  }


//////////////////////////  find restrictions  /////////////////////////////////

  public static Collection getRestrsOnProp(OWLNamedClass cls, OWLProperty prop) {
    Collection results = null;
    Collection restrs = cls.getRestrictions();
    if (restrs != null) {
      results = new ArrayList();
      for (Iterator i = restrs.iterator(); i.hasNext();) {
        OWLRestriction current = (OWLRestriction) i.next();
        if (current instanceof AbstractOWLQuantifierRestriction) {
          if (((AbstractOWLQuantifierRestriction) current).getOnProperty() == prop) {
            results.add(current);
          }
        }
      }
      if (results.size() < 1) {
        results = null;
      }
    }
    return results;
  }

  public static Collection getAllValuesFromRestrsOnProp(OWLNamedClass target, OWLProperty prop) {
    Collection restrs = getRestrsOnProp(target, prop);
    if (restrs != null) {
      for (Iterator i = restrs.iterator(); i.hasNext();) {
        Object current = i.next();
        if (!(current instanceof OWLAllValuesFrom)) {
          i.remove();
        }
      }
      if (restrs.size() < 1) {
        restrs = null;
      }
    }
    return restrs;
  }

  public static Collection getSomeValuesFromRestrsOnProp(OWLNamedClass target, OWLProperty prop) {
    Collection restrs = getRestrsOnProp(target, prop);
    if (restrs != null) {
      for (Iterator i = restrs.iterator(); i.hasNext();) {
        Object current = i.next();
        if (!(current instanceof OWLSomeValuesFrom)) {
          i.remove();
        }
      }
      if (restrs.size() < 1) {
        restrs = null;
      }
    }
    return restrs;
  }

  public static OWLAllValuesFrom getClosureRestriction(OWLNamedClass cls, DefaultOWLObjectProperty prop) {
    OWLAllValuesFrom closureRestr = null;

    // get all the classes that would be in the closure restriction
    Collection namedFillersForProp = getNamedSomeValuesFromFillers(cls, prop);
    if (debug) {
		System.out.println("namedFillersForProp = " + namedFillersForProp);
	}

    // as long as there are some SomeValuesFrom restrs using this prop
    // with a named class in the filler
    if (namedFillersForProp != null) {

      // get all the AllValuesFrom restrs for this property
      Collection restrs = getAllValuesFromRestrsOnProp(cls, prop);

      if (restrs != null) {
        // and try to find if one is the closure for the given prop
        for (Iterator i = restrs.iterator(); i.hasNext() && closureRestr == null;) {
          OWLAllValuesFrom current = (OWLAllValuesFrom) i.next();

          if (namedFillersForProp.size() == 1) {
            // if there is only 1 exist restr using this prop, the closure will be
            // a named class
            if (current.getFiller() == CollectionUtilities.getFirstItem(namedFillersForProp)) {
              closureRestr = current;
            }
          }
          else {
            // multiple exist restrs using this prop then
            // a union class is likely to be a closure restriction
            if (current.getFiller() instanceof OWLUnionClass) {
              OWLUnionClass filler = (OWLUnionClass) current.getFiller();

              TreeSet closureFillerClasses = new TreeSet();
              filler.getNestedNamedClasses(closureFillerClasses);

              // so check to see if it is complete, in which case we've found it
              if (closureFillerClasses.containsAll(namedFillersForProp)) {
                closureRestr = current;
              }
            }
          }
        }
      }
    }

    return closureRestr;
  }

//////////////////////////////////////////////////////////////////////////////

  /**
   * @param cls
   * @param objectProperty
   * @return null if none found
   */
  public static Collection getNamedSomeValuesFromFillers(OWLNamedClass cls, DefaultOWLObjectProperty objectProperty) {
    return getNamedSomeValuesFromFillers(getSomeValuesFromRestrsOnProp(cls, objectProperty));
  }

  /**
   * @param someRestrs a collection of some retrictions
   * @return null if none found
   */
  public static Collection getNamedSomeValuesFromFillers(Collection someRestrs) {

    Collection fillers = null;
    if (someRestrs != null) {
      Collection foundFillers = new ArrayList();
      for (Iterator i = someRestrs.iterator(); i.hasNext();) {
        DefaultOWLSomeValuesFrom restr = (DefaultOWLSomeValuesFrom) i.next();
        RDFResource filler = restr.getFiller(); // returns the class of the filler
        if (KbHelper.isUserClass(filler)) {
          foundFillers.add(filler);
        }
      }
      if (foundFillers.size() > 0) {
        fillers = foundFillers;
      }
    }
    return fillers;
  }

  public static void makeAllDisjoint(AbstractList classes) {
    //System.out.println("Creating disjoints for set of classes");
    int num_classes = classes.size();
    for (int i = 0; i < num_classes - 1; i++) {
      for (int j = i + 1; j < num_classes; j++) {
        addDisjoint((OWLNamedClass) classes.get(i), (OWLNamedClass) classes.get(j));
      }
    }
  }

  public static void addDisjoint(OWLNamedClass class1, OWLNamedClass class2) {
    if (!class1.getDisjointClasses().contains(class2)) {
      GuiUtils.getConsole().out("Adding disjoint between: " +
                                class1.getBrowserText() + "and " + class2.getBrowserText());

      class1.addDisjointClass(class2);
      class2.addDisjointClass(class1); // the reverse disjoint must be added
    }
  }

  public OWLObjectProperty createNewObjectProperty(String name, OWLNamedClass domain, OWLNamedClass range, boolean isFunctional) {
    String str = "object property: ";
    if (isFunctional) {
      str = "functional " + str;
    }
    GuiUtils.getConsole().out("Creating " + str + name);

    DefaultOWLObjectProperty newProperty = (DefaultOWLObjectProperty) owlModel.createOWLObjectProperty(name);

    newProperty.setFunctional(isFunctional);

    if (range != null) {
      newProperty.setRange(range);
    }

    if (domain != null) {
      newProperty.setDomain(domain); // @@API was domain.addDirectTemplateSlot(newProperty);
    }

    return newProperty;
  }

  public void updateSomeValuesFromRestrsForProp(OWLNamedClass cls,
                                                DefaultOWLObjectProperty prop,
                                                Collection newFillers,
                                                boolean updateClosure) {

    if (debug) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>  newFillers = " + newFillers);
	}
    Console out = GuiUtils.getConsole();
    boolean addingClosure = false;
    boolean removingClosure = false;

    Collection someRestrs = getSomeValuesFromRestrsOnProp(cls, prop);
    Collection oldFillers = getNamedSomeValuesFromFillers(someRestrs);

    if (oldFillers == null) {    // if there were no existing restrictions
      if (newFillers != null) {  // but there are some new ones
        out.out("Creating new restrictions on " + prop.getBrowserText() +
                " for class: " + cls.getBrowserText());
        for (Iterator i = newFillers.iterator(); i.hasNext();) {
          OWLNamedClass filler = (OWLNamedClass) i.next();
          addSomeValuesFromRestriction(cls, prop, filler);
        }

        // add in a new closure axiom for the property
        if (updateClosure) {
          addingClosure = true;
          addClosureRestriction(cls, prop);
        }
      }
    }
    else {

      if (newFillers == null) {
        // remove the old closure axiom for the property (if there is one)
        if (updateClosure) {
          OWLAllValuesFrom oldClosure = getClosureRestriction(cls, prop);
          if (oldClosure != null) {
            if (debug) {
				System.err.println("removing closure " + oldClosure);
			}
            removingClosure = true;
            cls.removeSuperclass(oldClosure);
          }
        }

        // remove all existing restr on this prop
        if (debug) {
			System.err.println(">>>>>>>>>>>>>>>>>    no new restrictions");
		}
        out.out("Removing old restrictions on " + prop.getBrowserText() +
                " for class: " + cls.getBrowserText());
        for (Iterator i = someRestrs.iterator(); i.hasNext();) {
          cls.removeSuperclass((OWLSomeValuesFrom) i.next());
        }
      }
      else {
        if (debug) {
			System.err.println(">>>>>>>>>>>>>> New restrs");
		}
        // replace the current restrs with new ones
        if (!CollectionUtilities.containSameItems(oldFillers, newFillers)) {
          // if the fillers have been edited (old and new do not match)

          // remove the old closure axiom for the property (if there is one)
          if (updateClosure) {
            OWLAllValuesFrom oldClosure = getClosureRestriction(cls, prop);
            if (oldClosure != null) {
              if (debug) {
				System.err.println("removing closure " + oldClosure);
			}
              removingClosure = true;
              cls.removeSuperclass(oldClosure);
            }
          }

          out.out("Updating restrictions on " + prop.getBrowserText() +
                  " for class: " + cls.getBrowserText());
          if (debug) {
			System.err.println(">>>>>>>>>>>>>>>> changes");
		}
          // go through and remove and old fillers not in the new set
          for (Iterator i = oldFillers.iterator(); i.hasNext();) {
            OWLNamedClass filler = (OWLNamedClass) i.next();
            if (!newFillers.contains(filler)) {
              OWLSomeValuesFrom restriction = findRestrWithFiller(someRestrs, filler);
              someRestrs.remove(restriction);
              cls.removeSuperclass(restriction);
            }
          }

          // then go through the new fillers and add them (if not already in old)
          for (Iterator i = newFillers.iterator(); i.hasNext();) {
            OWLNamedClass filler = (OWLNamedClass) i.next();
            if (!oldFillers.contains(filler)) {
              addSomeValuesFromRestriction(cls, prop, filler);
            }
          }

          // add in a new closure axiom for the property
          if (updateClosure) {
            addingClosure = true;
            addClosureRestriction(cls, prop);
          }
        }
      }
    }
    if (addingClosure) {
      if (removingClosure) {
        out.out("Updating closure on " + prop.getBrowserText() + " for class: " + cls.getBrowserText());
      }
      else {
        out.out("Closing " + prop.getBrowserText() + " for class: " + cls.getBrowserText());
      }
    }
    else {
      if (removingClosure) {
        out.out("Removing closure on " + prop.getBrowserText() + " for class: " + cls.getBrowserText());
      }
    }
  }

  public static OWLSomeValuesFrom findRestrWithFiller(Collection allSomeRestrs, OWLNamedClass filler) {
    OWLSomeValuesFrom result = null;
    for (Iterator i = allSomeRestrs.iterator(); i.hasNext() && result == null;) {
      OWLSomeValuesFrom restr = (OWLSomeValuesFrom) i.next();
      if (restr.getSomeValuesFrom() == filler) {
        result = restr;
      }
    }
    return result;
  }

  public boolean addSomeValuesFromRestriction(OWLNamedClass cls, RDFProperty property, OWLClass filler) {
    OWLSomeValuesFrom restr = owlModel.createOWLSomeValuesFrom(property, filler);
    cls.addSuperclass(restr);
    return restr != null;
  }

  public boolean addAllValuesFromRestriction(OWLNamedClass cls, RDFProperty property, OWLClass filler) {
    OWLAllValuesFrom restr = owlModel.createOWLAllValuesFrom(property, filler);
    cls.addSuperclass(restr);
    return restr != null;
  }

  public void addCoveringRestriction(OWLNamedClass targetClass, Collection children) {

    GuiUtils.getConsole().out("Adding covering axiom to class: " + targetClass.getBrowserText());

    OWLUnionClass union = owlModel.createOWLUnionClass(children);
    targetClass.addEquivalentClass(union);
  }

  public void addClosureRestriction(OWLNamedClass target, DefaultOWLObjectProperty prop) {
    Collection namedFillersForProp = getNamedSomeValuesFromFillers(target, prop);

    if (namedFillersForProp != null) {

      if (namedFillersForProp.size() == 1) {
        // single named class filler for the closure
        OWLNamedClass filler = (OWLNamedClass) CollectionUtilities.getFirstItem(namedFillersForProp);
        target.addSuperclass(owlModel.createOWLAllValuesFrom(prop, filler));
      }
      else {
        // multiple fillers must be added to a union for closure
        OWLUnionClass closureFiller = owlModel.createOWLUnionClass(namedFillersForProp);
        OWLAllValuesFrom closureRestr = owlModel.createOWLAllValuesFrom(prop, closureFiller);
        target.addSuperclass(closureRestr);
      }
    }
  }

  public void addEquivalentSetRestriction(OWLNamedClass parent, Collection children) {
    GuiUtils.getConsole().out("Adding equivalent enumeration: " + parent.getBrowserText());
    OWLEnumeratedClass individualSet = owlModel.createOWLEnumeratedClass(children);
    parent.addEquivalentClass(individualSet);
  }

  public void makeAllDifferent(Collection individuals) {
    GuiUtils.getConsole().out("Making individuals all different:");

    OWLAllDifferent allDiff = owlModel.createOWLAllDifferent();

    for (Iterator i = individuals.iterator(); i.hasNext();) {
      Object item = i.next();
      if (item instanceof OWLIndividual) {
        GuiUtils.getConsole().out(" " + ((OWLIndividual) item).getBrowserText());

        allDiff.addDistinctMember((OWLIndividual) item);
      }
    }
  }

  public void makeAllDifferent(OWLNamedClass instantiatingClass) {
    makeAllDifferent(instantiatingClass.getInstances(false));
  }

  public static void ensureAllSubclassesDisjoint(Collection namedSubs) {

    for (Iterator i = namedSubs.iterator(); i.hasNext();) {
      Object sub = i.next();
      if (sub instanceof OWLNamedClass) {
        OWLNamedClass current = (OWLNamedClass) sub;
        Collection subclasses = current.getSubclasses(false);
        if (subclasses != null) {
          GuiUtils.getConsole().out("Making sure all primitive subclasses of " +
                                    current.getBrowserText() + " are disjoint");
          OWLUtil.ensureSubclassesDisjoint(current);
          ensureAllSubclassesDisjoint(subclasses);
        }
      }
    }
  }

//  public static void ensureAllSubclassesDisjoint(OWLNamedClass superclass, Collection namedSubs) {
//
//    Collection subclasses = superclass.getSubclasses(false);
//
//    if (subclasses != null) {
//      GuiUtils.getConsole().out("Making sure all primitive subclasses of " +
//                                superclass.getBrowserText() + " are disjoint");
//      OWLUtil.ensureSubclassesDisjoint(superclass);
//
//      for (Iterator i = subclasses.iterator(); i.hasNext();) {
//        try {
//          OWLNamedClass cls = (OWLNamedClass)i.next();
//          if (namedSubs.contains(cls)){
//            ensureAllSubclassesDisjoint(cls, namedSubs);
//          }
//        }
//        catch (ClassCastException e) {
//          e.printStackTrace();
//        }
//      }
//    }
//  }


  public Collection getRootObjectProperties() {
    Collection rootProps = owlModel.getVisibleUserDefinedOWLProperties();
    Collection objProps = new ArrayList();

    for (Iterator i = rootProps.iterator(); i.hasNext();) {
      OWLProperty prop = (OWLProperty) i.next();
      if (prop.isObjectProperty() && prop.getSuperpropertyCount() == 0) {
        objProps.add(prop);
      }
    }
    if (objProps.size() < 1) {
      objProps = null;
    }

    return objProps;
  }

  public Collection getNamespacePrefixes(boolean withSeparator) {
    Collection nsPrefixes = owlModel.getNamespaceManager().getPrefixes();
    if (withSeparator) {
      Collection withSeperators = new ArrayList(nsPrefixes.size());
      for (Iterator i = nsPrefixes.iterator(); i.hasNext();) {
        withSeperators.add((String) i.next() + ":");
      }
      nsPrefixes = withSeperators;
    }
    return nsPrefixes;
  }

  /**
   * Gets the resources from the given namespace that match the given java class
   *
   * @param namespaceURIString the URI of the namespace
   * @param resourceClass      the java class of the resources to match
   * @return a collection of the resources (type will match that of that given) or null
   */
  public Collection getResourcesFromNamespace(String namespaceURIString, Class resourceClass) {
    Collection hiddenResources = null;
    try {
      URI metaURI = new URI(namespaceURIString);

      if (isImported(metaURI)) {
        hiddenResources = new ArrayList();
        TripleStore tStore = owlModel.getTripleStoreModel().getTripleStore(namespaceURIString);
        for (Iterator i = tStore.listHomeResources(); i.hasNext();) {
          RDFResource res = (RDFResource) i.next();
          if (resourceClass.isAssignableFrom(res.getClass())) {
            hiddenResources.add(res);
          }
        }
      }
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return hiddenResources;
  }


  public void hideResources(Collection resources) {
    for (Iterator i = resources.iterator(); i.hasNext();) {
      RDFResource res = (RDFResource) i.next();
      res.setVisible(false);
    }
    // reload the UI to make sure they are now hidden
    WizardAPI.getWizardManager().updateUI();
  }

  public void ensureSubclassesAreVisible(Collection resources, OWLNamedClass defaultParent, boolean prompt) {
    PromptPanel promptPanel = null;
    boolean doIt = true;

    for (Iterator i = resources.iterator(); i.hasNext();) {
      RDFResource res = (RDFResource) i.next();
      if (!res.isVisible()) {
        if (res instanceof OWLNamedClass) {
          OWLNamedClass cls = (OWLNamedClass) res;
          Collection subs = cls.getNamedSubclasses(false);
          for (Iterator j = subs.iterator(); j.hasNext();) {
            OWLNamedClass sub = (OWLNamedClass) j.next();
            if (sub.isVisible() && sub.getNamedSuperclasses(false).size() == 1) {
              if (prompt) {
                promptPanel = new PromptPanel("Hidden class detected: " + sub.getBrowserText() +
                                              "\nReason: it is a subclass of the hidden class " +
                                              cls.getBrowserText() + "\n\nWould you like to assert " +
                                              defaultParent.getBrowserText() +
                                              " as an additional superclass so that you can see this class?");
                int result = ProtegeUI.getModalDialogFactory().showDialog(WizardAPI.getWizardManager().getMainWindow(),
                                                                          promptPanel,
                                                                          "Hidden class detected: " + sub.getBrowserText(),
                                                                          ModalDialogFactory.MODE_YES_NO);

                doIt = result == ModalDialogFactory.OPTION_YES;
                prompt = promptPanel.getShowAgain();
              }

              if (doIt) {
                sub.addSuperclass(defaultParent);
              }
            }
          }
        }
      }
//          Collection subs = cls.getNamedSubclasses(false);
//          boolean hasVisibleParent = false;
//          for (Iterator j = subs.iterator(); i.hasNext() && hasVisibleParent;){
//            OWLNamedClass sub = (OWLNamedClass)j.next();
//            if ()
//          }
    }
    // reload the UI to make sure they are now shown
//    WizardAPI.getWizardManager().updateUI();

  }
}
