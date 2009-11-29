package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.AbstractMatrixModel;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Nov-2004
 */
public class PropRangeMatrixModel extends AbstractMatrixModel {

  public PropRangeMatrixModel(PropertiesCollection pc, String colPropName, String rowPropName) {
    super(pc, colPropName, rowPropName, "property");
  }

  protected Object loadValue(Object rangeCls, Object property) {
    return null;
  }

  public Collection getAllowedValues(Object columnObject, Object rowObject) {
    KbHelper helper = KbHelper.getHelper(WizardAPI.getWizardManager().getKnowledgeBase());
    return CollectionUtilities.createCollection(helper.getRootCls());
  }

  public Class getColumnClass(int columnIndex) {
    Class result = Object.class;
    if (columnIndex == 0) {
      result = Slot.class;
    }
    else if (columnIndex == 1) {
      result = Cls.class;
    }
    return result;
  }

  public HashMap getMap() {
    HashMap result = new HashMap();
    for (int i = 0; i < getRowCount(); i++) {
      result.put(getValueAt(i, 0), getValueAt(i, 1));
    }
    return result;
  }
}
