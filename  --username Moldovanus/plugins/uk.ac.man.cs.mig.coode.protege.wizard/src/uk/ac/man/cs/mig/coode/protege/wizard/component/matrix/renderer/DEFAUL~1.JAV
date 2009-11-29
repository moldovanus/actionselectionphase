package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer;

import edu.stanford.smi.protege.ui.CheckBoxRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public class DefaultTableCellRendererFactory implements TableCellRendererFactory {

  private static final boolean debug = Trace.renderer;

  public TableCellRenderer getRendererForClass(Class expectedType) {
    TableCellRenderer ren = null;

    if (Boolean.class.isAssignableFrom(expectedType)) {
      ren = new CheckBoxRenderer();
    }
    else {
      ren = new DefaultTableCellRenderer();
    }

    return ren;
  }
}
