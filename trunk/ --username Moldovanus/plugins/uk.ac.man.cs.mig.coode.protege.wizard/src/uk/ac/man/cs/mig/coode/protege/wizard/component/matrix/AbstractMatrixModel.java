package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix;

import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import javax.swing.table.DefaultTableModel;
import java.util.AbstractList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public abstract class AbstractMatrixModel extends DefaultTableModel {

  public static final boolean debug = false;

  private PropertiesCollection pc;
  private String colPropName;
  private String rowPropName;
  private String rowsTitle;
  private HashMap columnDefaults;

  private boolean loadDone = false;

  public AbstractMatrixModel(PropertiesCollection pc, String colPropName, String rowPropName, String rowsTitle) {
    super();
    this.pc = pc;
    this.colPropName = colPropName;
    this.rowPropName = rowPropName;
    this.rowsTitle = rowsTitle;

    //load();
  }

  public final void load() {

    // @@TODO do clever reload if already exists

    this.setColumnCount(0);

    AbstractList rows = getRows();
    if (rows != null) {
      addColumn(rowsTitle, rows.toArray());

      AbstractList columns = getColumns();

      if (columns != null) {
        for (Iterator i = columns.iterator(); i.hasNext();) {
          addColumn(i.next());
        }

        for (int currentColumn = 1; currentColumn < columns.size() + 1; currentColumn++) {
          Object defaultValue = null;

          if (columnDefaults != null) {
            if (debug) System.out.println("columnDefaults = " + columnDefaults);
            defaultValue = columnDefaults.get(columns.get(currentColumn - 1));
          }

          for (int currentRow = 0; currentRow < rows.size(); currentRow++) {

            Object cellValue = loadValue(rows.get(currentRow),
                                         columns.get(currentColumn - 1));

            if (cellValue == null) {
              if (debug) System.out.println("setting default of " + defaultValue + " for line " + currentRow);
              cellValue = defaultValue;
            }

            setValueAt(cellValue, currentRow, currentColumn);
          }
        }
      }
    }
    loadDone = true;
  }

  public final boolean isCellEditable(int row, int column) {
    return (column > 0);
  }

  protected abstract Object loadValue(Object column, Object row);

  private AbstractList getRows() {
    return (AbstractList) pc.getProperty(rowPropName);
  }

  private AbstractList getColumns() {
    AbstractList columns = (AbstractList) pc.getProperty(colPropName);
    Object first = columns.get(0);
    //System.out.println("first.getClass() = " + first.getClass());
    return columns;
  }

  protected final int getRowForObject(Object o) {
    return getRows().indexOf(o);
  }

  protected final int getColForObject(Object o) {
    return getColumns().indexOf(o) + 1;
  }

  public final Object getXAxis(int columnIndex) {
    Object result = null;
    if (columnIndex > 0) {
      result = getColumns().get(columnIndex - 1);
    }
    return result;
  }

  public abstract Collection getAllowedValues(Object columnObject, Object rowObject);

  public final Collection getAllowedValues(int columnIndex, int rowIndex) {
    return getAllowedValues(getXAxis(columnIndex), getValueAt(rowIndex, 0));
  }

  public void setDefaults(HashMap columnDefaults) {
    if (debug) System.out.println("SETTING DEFAULTS");
    this.columnDefaults = columnDefaults;
    if (loadDone) {
      if (debug) System.out.println("loading....");
      load();
    }
  }

  public String getColumnName(int column) {
    String name = null;

    if (column > 0) {
      Object colHeader = getColumns().get(column - 1);
      if (colHeader instanceof Frame) {
        name = ((Frame) colHeader).getName();
      }
      else {
        name = super.getColumnName(column);
      }
    }
    else {
      name = super.getColumnName(column);
    }
    return name;
  }
}
