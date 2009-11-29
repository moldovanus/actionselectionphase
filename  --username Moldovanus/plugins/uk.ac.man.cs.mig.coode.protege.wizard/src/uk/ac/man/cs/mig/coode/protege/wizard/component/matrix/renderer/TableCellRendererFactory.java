package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer;

import javax.swing.table.TableCellRenderer;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public interface TableCellRendererFactory {

  public TableCellRenderer getRendererForClass(Class expectedType);
}
