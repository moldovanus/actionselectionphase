package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import javax.swing.table.TableCellEditor;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public interface TableCellEditorFactory {

  public TableCellEditor getEditorForClass(Class expectedType);

  public TableCellEditor getEditorForClassAndAllowedValues(Class expectedType, Collection allowedValues);
}
