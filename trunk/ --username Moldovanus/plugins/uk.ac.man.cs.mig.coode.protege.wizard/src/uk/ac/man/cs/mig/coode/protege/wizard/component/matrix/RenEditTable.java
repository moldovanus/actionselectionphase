package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix;

import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.TableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.TableCellRendererFactory;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public class RenEditTable extends VariableHeightTable {

  public static final boolean debug = false;

  protected TableCellEditorFactory editorFactory = null;
  protected TableCellRendererFactory rendererFactory = null;

  public RenEditTable(AbstractMatrixModel dm) {
    super(dm);
  }

  public RenEditTable(AbstractMatrixModel dm,
                      TableCellEditorFactory ef,
                      TableCellRendererFactory rf) {
    super(dm);
    setEditorFactory(ef);
    setRendererFactory(rf);
  }

  public void setEditorFactory(TableCellEditorFactory ef) {
    this.editorFactory = ef;
    refreshEditors();
  }

  public void refreshEditors() {
    if (editorFactory != null) {
      AbstractMatrixModel model = (AbstractMatrixModel) getModel();
      for (int i = 1; i < model.getColumnCount(); i++) { // start from "1st" column
        Class expectedCellClass = model.getColumnClass(i);
        for (int j = 0; j < model.getRowCount(); j++) {
          //TableColumn col = getColumnModel().getColumn(i);
          Collection allowedValues = model.getAllowedValues(i, j);
          TableColumn tc = getColumnModel().getColumn(i);
          tc.setCellEditor(editorFactory.getEditorForClassAndAllowedValues(expectedCellClass, allowedValues));
        }
      }
    }
  }

  public void setRendererFactory(TableCellRendererFactory rf) {
    if (debug) System.out.println("Setting renderer factory");
    this.rendererFactory = rf;
    refreshRenderers();
  }

  public void refreshRenderers() {
    if (debug) System.out.println("Attempting to refresh renderers");
    if (rendererFactory != null) {
      if (debug) System.out.println("REFRESHING RENDERERS");
      TableModel model = getModel();
      for (int i = 0; i < model.getColumnCount(); i++) {
        TableColumn col = getColumnModel().getColumn(i);
        Class expectedCellClass = model.getColumnClass(i);
        TableCellRenderer tcr = rendererFactory.getRendererForClass(expectedCellClass);
        if (tcr != null) {
          col.setCellRenderer(tcr);
        }
        else {
          if (debug) System.err.println(">>>> NULL renderer applied to class " + expectedCellClass);
        }
      }
      refreshAllRowHeights();
    }
  }

  private void refresh() {
    refreshEditors();
    refreshRenderers();
  }

  public void setModel(TableModel dataModel) {
    if (debug) System.err.println("SETTING MODEL");
    super.setModel(dataModel);
    refresh();
  }

  public void tableChanged(TableModelEvent e) {
    if (debug) System.err.println("TABLE CHANGED");
    super.tableChanged(e);
    refresh();
  }
}