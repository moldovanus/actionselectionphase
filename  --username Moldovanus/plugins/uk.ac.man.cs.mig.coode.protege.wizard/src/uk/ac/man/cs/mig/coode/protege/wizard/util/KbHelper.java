package uk.ac.man.cs.mig.coode.protege.wizard.util;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class KbHelper {

  private static final boolean debug = false;
  private KnowledgeBase kb;
  private IdGenerator idGen;

  protected static KbHelper helper;

  public static KbHelper getHelper(KnowledgeBase kb) {
    if (helper == null) {
      helper = new KbHelper();
    }
    helper.setKnowledgeBase(kb);
    return helper;
  }

  public static void resetHelper() {
    helper = null;
  }

  protected KbHelper() {
  }

  protected void setKnowledgeBase(KnowledgeBase kb) {
    this.kb = kb;
  }

  public void setIdGenerator(IdGenerator idGen) {
    this.idGen = idGen;
  }

  public Object createClassAtRoot(String newClassName) {
    return createClass(newClassName, kb.getRootCls());
  }

  public Cls createClass(String newClassName, Object superClass) {
    Cls newClass = null;
    if (newClassName != null) {
      if (superClass == null) {
        if (debug) System.err.println("Superclass is null, assuming root");
        superClass = kb.getRootCls();
      }

      GuiUtils.getConsole().out("Creating subclass of " +
                                ((Cls) superClass).getBrowserText() +
                                ": " + newClassName);

      try {
        if (WizardAPI.getCreateWithBrowserSlot()) {
          newClass = createClassUsingBrowserSlot(newClassName, superClass);
        }
        else {
          newClass = kb.createCls(newClassName, CollectionUtilities.createCollection(superClass));
        }
      }
      catch (NullPointerException e) {
        System.err.println("Could not create class [" + newClassName + "] perhaps it already exists");
        newClass = null;
      }
    }
    return newClass;
  }

  public Cls createClassUsingBrowserSlot(String newClassName, Object superClass) {
    Cls newClass = null;

    String newClassId = null;
    if (idGen != null) {
      newClassId = idGen.getNextId();
    }

    newClass = kb.createCls(newClassId, CollectionUtilities.createCollection(superClass));

    // we need to get the display slot for the new class
    Slot displaySlot = getDisplaySlot(newClass);

    if ((displaySlot != kb.getNameSlot()) // if the display slot is not the name
        && (isGoodDisplaySlot(displaySlot))) // and is not a bad choice of display slot
    {
      // create a new slot value using the name
      newClass.setDirectOwnSlotValue(displaySlot, newClassName);
    }
    else {
      // otherwise just rename the class
      newClass = newClass.rename(newClassName);
    }

    return newClass;
  }

  /**
   * Recursively create a tree of new classes starting from topParent
   *
   * @param directParent root of tree where all user objects are names for new classes
   *                     these should already be verified as reasonable (and unique) frame names
   * @param topParent    the topmost parent (as the directParent does not need to know this)
   */
  public Collection createSubclasses(DefaultMutableTreeNode directParent, Cls topParent) {

    int numChildren = directParent.getChildCount();
    Collection addedClasses = new ArrayList(numChildren);

    for (int i = 0; i < numChildren; i++) {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) directParent.getChildAt(i);
      String childName = (String) currentNode.getUserObject();
      Object thisClass = createClass(childName, topParent);
      addedClasses.add(thisClass);
      addedClasses.addAll(createSubclasses(currentNode, (Cls) thisClass));
    }

    return addedClasses;
  }

  public boolean classDisplaySlotIsName(Cls theClass) {
    String classDisplaySlotName = getDisplaySlot(theClass).getName();
    String nameSlotName = kb.getNameSlot().getName();

    return (classDisplaySlotName.equals(nameSlotName));
  }

  public Slot createProperty(String propName) {
    return kb.createSlot(propName);
  }

  /**
   * Find all user defined slots in knowledgebase (in current namespace only?)
   *
   * @return colleciton of valid slots or null if none found
   */
  public Collection getRootSlots() {
    Collection rootSlots = kb.getRootSlots();
    for (Iterator i = rootSlots.iterator(); i.hasNext();) {
      if (!((Slot) i.next()).isEditable()) {
        i.remove();
      }
    }
    if (rootSlots.size() <= 0) {
      rootSlots = null;
    }

    return rootSlots;
  }

  public Cls getRootCls() {
    return kb.getRootCls();
  }

  public Instance createInstance(String newInstanceName, Cls type) {
    if (type == null) {
      type = kb.getRootCls();
    }

    GuiUtils.getConsole().out("Creating instance of " +
                              ((Cls) type).getBrowserText() + ": " + newInstanceName);

    return kb.createInstance(newInstanceName, type);
  }

  public Collection createInstances(Collection instanceNames, Cls type) {
    Collection individuals = new ArrayList(instanceNames.size());
    for (Iterator i = instanceNames.iterator(); i.hasNext();) {
      Instance instance = createInstance((String) i.next(), type);
      individuals.add(instance);
    }
    return individuals;
  }


  public boolean getDisplaySlotSet() {
    return !getDisplaySlot(getRootCls()).getName().equals(Model.Slot.NAME);
  }

  //////////////////////////////////////////////////////////////////////////////////////
  // STATIC METHODS

  public static Collection getDirectUserSubclasses(Cls parent) {
    Collection directSubclasses = parent.getDirectSubclasses();
    return getUserClasses(directSubclasses);
  }

  public static Collection getNamedSubclasses(Cls parent) {
    Collection allSubclasses = parent.getSubclasses();
    return getUserClasses(allSubclasses);
  }

  public static boolean isUserClass(Object cls) {
    return ClassIdentifierTester.isUserClass(cls);
  }

  public static Collection getUserClasses(Collection clses) {
    Collection userClasses = new ArrayList();
    for (Iterator i = clses.iterator(); i.hasNext();) {
      Cls current = (Cls) i.next();
      if (debug) System.out.println("current = " + current);
      if (isUserClass(current)) {
        if (debug) System.out.print(" ----->  REMOVING");
        userClasses.add(current);
      }
    }
    if (userClasses.size() == 0) {
      userClasses = null;
    }
    return userClasses;
  }

  public static boolean isGoodDisplaySlot(Slot slot) {
    boolean result = true;
    String slotName = slot.getName();
    if ((slotName.startsWith(":")) &&
        (!slotName.equals(":NAME")) &&
        (!slotName.equals(":DOCUMENTATION"))) {
      result = false;
    }
    return result;
  }

  public static Slot getDisplaySlot(Cls theClass) {
    Cls metaclass = theClass.getDirectType();

    BrowserSlotPattern bsp = metaclass.getBrowserSlotPattern();

    return bsp.getFirstSlot();
  }

  public Collection getChildrenClasses(Cls superclass, DefaultMutableTreeNode root) {
    Collection children = new ArrayList();
    Collection supercls = CollectionUtilities.createCollection(superclass);
    for (int i = 0; i < root.getChildCount(); i++) {
      DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root.getChildAt(i);
      String childName = (String) childNode.getUserObject();
      Cls child = (Cls) CollectionUtilities.getFirstItem(kb.getClsesWithMatchingBrowserText(childName, supercls, -1));
      children.add(child);
    }
    if (children.size() <= 0) {
      children = null;
    }
    return children;
  }
}
