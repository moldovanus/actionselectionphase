package uk.ac.man.cs.mig.coode.protege.wizard.renderer;

import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleListModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.AbstractList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Oct-2004
 */
public class MultiFrameTableCellRenderer implements TableCellRenderer {

  private static final int DEFAULT_FRAME_HEIGHT = 16;
  private Component theRenderer = null;
  private SelectableList theList = null;
  private SimpleListModel theModel = null;
  private JComponent dummyComponent = null;

  public MultiFrameTableCellRenderer(FrameRenderer listRenderer) {
    theModel = new SimpleListModel();
    theList = new SelectableList();
    theList.setModel(theModel);
    theList.setCellRenderer(listRenderer);
    dummyComponent = new JPanel();
  }

  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row, int column) {
    theRenderer = dummyComponent;

    if (value != null) {
      if (value instanceof java.util.List) {
        theModel.clear();
        theModel.addValues((Collection) value);
        theRenderer = theList;
      }
      else {
        System.err.println("mftcr RENDERER TYPE SET INCORRECTLY: rendering value of type " + value.getClass());
        theRenderer = new JLabel(value.toString());
      }
    }
    return theRenderer;
  }

  public int calcHeight(Object value) {
    //System.out.println("calculating size of" + value);
    int result = 0;
    if (value instanceof AbstractList) {
      result = ((AbstractList) value).size() * DEFAULT_FRAME_HEIGHT;
    }

    if (result < DEFAULT_FRAME_HEIGHT) {
      result = DEFAULT_FRAME_HEIGHT;
    }

    return result;
  }

}
