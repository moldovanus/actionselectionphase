package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.AbstractMatrixModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.AbstractList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Oct-2004
 */
public class ClassPropMatrixModel extends AbstractMatrixModel {

  private static final boolean debug = Trace.Component.matrix;

  public ClassPropMatrixModel(PropertiesCollection pc,
                              String colPropName,
                              String rowPropName) {
    super(pc, colPropName, rowPropName, "class");
  }

  /**
   * @param cls
   * @param slot
   * @return a single filler if functional, else a collection, or null if none
   */
  protected Object loadValue(Object cls, Object slot) {
    Object fillers = null;
    if (slot instanceof DefaultOWLObjectProperty) {
      DefaultOWLObjectProperty property = (DefaultOWLObjectProperty) slot;
      fillers = OwlKbHelper.getNamedSomeValuesFromFillers((OWLNamedClass) cls, property);
      if ((fillers != null) && (property.isFunctional())) {
        fillers = CollectionUtilities.getFirstItem((Collection) fillers);
      }
    }
    else {
      throw new IllegalArgumentException("Cannot use Datatype slots in this matrix");
    }
    return fillers;
  }

  public Collection getAllowedValues(Object columnObject, Object rowObject) {
    if (debug) System.out.println("getting allowed values for " + columnObject);
    Collection av = ((Slot) columnObject).getAllowedClses();
    if (debug) System.out.println(">>>>>>allowed values = " + av);
    if (av.size() < 1) {
      KbHelper helper = KbHelper.getHelper(WizardAPI.getWizardManager().getKnowledgeBase());
      av = CollectionUtilities.createCollection(helper.getRootCls());
    }
    if (debug) System.out.println(">>>>>>allowed values = " + av);
    return av;
  }

  public Collection getValue(Cls cls, Slot slot) {
    Collection c = null;
    Object o = getValueAt(getRowForObject(cls), getColForObject(slot));
//    if (o == null) {
//      System.err.println("NEVER CALLED?");
//      o = loadValue((Object) cls, (Object) slot);
//    }

    //System.out.println("result = " + (Collection)o);
    if (o != null) {
      if (o instanceof Collection) {
        c = (Collection) o;
      }
      else {
        c = CollectionUtilities.createCollection(o);
      }
    }
    return c;
  }

  public Class getColumnClass(int columnIndex) {
    Class colClass = super.getColumnClass(columnIndex);
    AbstractOWLProperty theProp = null;
    if (columnIndex > 0) {
      theProp = getProperty(columnIndex);
      if (theProp.isFunctional()) {
        if (theProp.isObjectProperty()) {
          colClass = Cls.class;
        }
        else {
          ValueType vt = theProp.getValueType();
          colClass = vt.getJavaType();
          //System.out.println("vt = " + vt);
        }
      }
      else {
        colClass = AbstractList.class;
      }
    }
    else {
      colClass = Cls.class;
    }
    //System.out.println("the expected values for column " + columnIndex +
    //                   "(" + theProp + ") should be " + colClass);

    return colClass;
  }

  private AbstractOWLProperty getProperty(int columnIndex) {
    return (AbstractOWLProperty) getXAxis(columnIndex);
  }
}
