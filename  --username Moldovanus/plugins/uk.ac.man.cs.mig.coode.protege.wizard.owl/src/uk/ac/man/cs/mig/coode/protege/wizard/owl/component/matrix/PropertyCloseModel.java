package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         20-Feb-2005
 */
public class PropertyCloseModel extends PropRangeMatrixModel {

  public PropertyCloseModel(PropertiesCollection pc, String colPropName, String rowPropName) {
    super(pc, colPropName, rowPropName);

    // default to false @@TODO could base this on if prop functional
    for (int i = 0; i < getRowCount(); i++) {
      setValueAt(Boolean.FALSE, i, 1);
    }
  }

  public Collection getAllowedValues(Object columnObject, Object rowObject) {
    return null;
  }

  public Class getColumnClass(int columnIndex) {
    Class theCls;
    if (columnIndex > 0) {
      theCls = Boolean.class;
    }
    else {
      theCls = super.getColumnClass(columnIndex);
    }
    return theCls;
  }

  public HashMap getMap() {
    HashMap result = new HashMap();
    for (int i = 0; i < getRowCount(); i++) {
      result.put(getValueAt(i, 0), getValueAt(i, 1));
    }
    return result;
  }
}
