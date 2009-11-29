package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardOptions;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.AbstractList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public class FrameTableCellEditorFactory extends DefaultTableCellEditorFactory {

  private static final boolean debug = Trace.renderer;

  private String title = null;

  public FrameTableCellEditorFactory(String title) {
    this.title = title;
  }

  public TableCellEditor getEditorForClass(Class expectedType) {
    TableCellEditor editor = null;
    if (expectedType.isAssignableFrom(Slot.class)) {
      editor = getEditorForClassAndAllowedValues(expectedType, null);
    }
    else {
      editor = super.getEditorForClass(expectedType);
    }
    return editor;
  }

  public TableCellEditor getEditorForClassAndAllowedValues(Class expectedType, Collection allowedValues) {

    KnowledgeBase kb = WizardAPI.getWizardManager().getKnowledgeBase();
    TableCellEditor editor = null;

    int threshold = WizardAPI.getUserOptionAsInt(WizardOptions.DROPDOWN_THRESHOLD);

    Instance root = null;

    if (allowedValues != null) {
      if (allowedValues.size() > 1) {
        System.err.println("MATRIX: MULTIPLE RANGES NOT IMPLEMENTED YET");
      }
      root = (Instance) CollectionUtilities.getFirstItem(allowedValues);
    }
    else { // default to allowing all
      //System.err.println("WARNING: DEFAULTING TO CLS ROOT - will NOT work for Slots");
      root = kb.getRootCls();
    }

    if (expectedType.isAssignableFrom(AbstractList.class)) {
      if (debug) System.out.println("EDITOR: AbstractList");
      editor = new MultiFrameTableCellEditor(WizardAPI.getWizardManager().getMainWindow(),
                                             root,
                                             title);
    }
    else if (expectedType.isAssignableFrom(Cls.class)) {

      // only single values allowed
      if (debug) System.out.println("EDITOR: CLS");
      Collection subclasses = KbHelper.getNamedSubclasses((Cls) root);
      if (subclasses == null) {
        if (debug) System.out.println("EDITOR: Dropdown for single value functional prop");
        JComboBox dropDown = new JComboBox(new Cls[]{(Cls) root, null});
        dropDown.setRenderer(FrameRenderer.createInstance());
        editor = new DefaultCellEditor(dropDown);
      }
      else if (subclasses.size() <= threshold) {
        // for small numbers of allowed values, just use a combo dropdown list
        if (debug) System.out.println("EDITOR: Dropdown for functional prop");
        subclasses.add(null);
        JComboBox dropDown = new JComboBox(subclasses.toArray());
        dropDown.setRenderer(FrameRenderer.createInstance());
        editor = new DefaultCellEditor(dropDown);
      }
      else {
        if (debug) System.out.println("EDITOR: Single value editor for functional prop");
        editor = new ClassTreeTableCellEditor(WizardAPI.getWizardManager().getMainWindow(),
                                              (Cls) root,
                                              title);
      }
    }
    else if (expectedType.isAssignableFrom(Slot.class)) {
      System.err.println("SLOTs not currently supported in FrameTableCellEditorFactory");
      editor = super.getEditorForClassAndAllowedValues(expectedType, allowedValues);
    }
    else {
      //System.err.println("CANNOT FIND EDITOR FOR: " + expectedType + ", defaulting");
      editor = super.getEditorForClassAndAllowedValues(expectedType, allowedValues);
    }

    return editor;
  }

}
