package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Oct-2004
 */
public class DefaultTableCellEditorFactory implements TableCellEditorFactory {

  private static final boolean debug = Trace.Component.matrixEditor;

  public TableCellEditor getEditorForClass(Class expectedType) {
    TableCellEditor editor = null;

    if (Boolean.class.isAssignableFrom(expectedType)) {
      editor = new DefaultCellEditor(new JCheckBox());
    }
    else {
      editor = new DefaultCellEditor(new JTextField());
    }

    return editor;
  }

  public TableCellEditor getEditorForClassAndAllowedValues(Class expectedType, Collection allowedValues) {
    // just get the same default editor regardless
    return getEditorForClass(expectedType);
  }
}
