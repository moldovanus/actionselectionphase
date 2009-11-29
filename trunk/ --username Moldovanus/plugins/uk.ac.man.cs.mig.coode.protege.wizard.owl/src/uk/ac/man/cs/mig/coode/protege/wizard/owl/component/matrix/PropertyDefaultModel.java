package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         20-Feb-2005
 */
public class PropertyDefaultModel extends PropRangeMatrixModel {

  public PropertyDefaultModel(PropertiesCollection pc, String colPropName, String rowPropName) {
    super(pc, colPropName, rowPropName);
  }

  public Collection getAllowedValues(Object columnObject, Object rowObject) {
    Collection av = ((Slot) rowObject).getAllowedClses();
    if (av.size() < 1) {
      KbHelper helper = KbHelper.getHelper(WizardAPI.getWizardManager().getKnowledgeBase());
      av = CollectionUtilities.createCollection(helper.getRootCls());
    }
    return av;
  }

  public HashMap getMap() {
    HashMap result = new HashMap();
    for (int i = 0; i < getRowCount(); i++) {
      result.put(getValueAt(i, 0), Collections.singletonList(getValueAt(i, 1)));
    }
    return result;
  }
}
