package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix;

import uk.ac.man.cs.mig.coode.protege.wizard.renderer.MultiFrameTableCellRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Nov-2004
 */
public class VariableHeightTable extends JTable {

  private static final boolean debug = Trace.Component.matrix;

  private static final int DEFAULT_FRAME_HEIGHT = 16;
  private static final Color STRIPE_COLOUR = new Color(230, 230, 255);
  private static final Color BACKGROUND_COLOUR = Color.white;

  private boolean done = false;

  public VariableHeightTable(AbstractMatrixModel dm) {
    super(dm);

    setBackground(BACKGROUND_COLOUR);
    setShowGrid(true);
    //setGridColor(STRIPE_COLOUR);

    done = true;
  }

  public void setModel(TableModel dataModel) {
    super.setModel(dataModel);
    refreshAllRowHeights();
  }

  public void editingStopped(ChangeEvent e) {
    int row = getEditingRow();
    super.editingStopped(e);
    refreshRowHeight(row);
  }

  private int calcRowHeight(int row) {
    int maxheight = DEFAULT_FRAME_HEIGHT;
    int height = 0;
    if (done) {
      if (debug) System.out.println("getColumnCount() = " + getColumnCount());
      for (int i = 1; i < getColumnCount(); i++) {
        Object o = getValueAt(row, i);
        if (debug) System.out.println("row = " + row + ", column = " + i);
        TableCellRenderer tcr = getCellRenderer(row, i);
        if (tcr instanceof MultiFrameTableCellRenderer) {
          height = ((MultiFrameTableCellRenderer) tcr).calcHeight(o);
          if (height > maxheight) {
            maxheight = height;
          }
        }
      }
    }
    return maxheight;
  }

  public void refreshRowHeight(int row) {
    setRowHeight(row, calcRowHeight(row));
  }

  public void refreshAllRowHeights() {
    for (int i = getRowCount() - 1; i >= 0; i--) {
      refreshRowHeight(i);
    }
  }

  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    Component c = super.prepareRenderer(renderer, row, column);

    if (c != null) {
      if ((row % 2) == 0) {
        if (debug) System.out.println("setting bgrnd colour for " + column + ", " + row);
        c.setBackground(STRIPE_COLOUR);
      }
      else {
        c.setBackground(getBackground());
      }
    }
    return c;
  }
}
