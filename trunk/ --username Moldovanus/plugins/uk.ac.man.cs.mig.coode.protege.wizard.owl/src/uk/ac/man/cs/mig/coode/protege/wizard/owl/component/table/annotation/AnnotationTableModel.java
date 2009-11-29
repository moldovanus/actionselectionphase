package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.table.annotation;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;

import javax.swing.table.AbstractTableModel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Aug-2004
 */
public class AnnotationTableModel extends AbstractTableModel {

  // below is static so annotations can retain their values each time
  private static AbstractList annotations;

  public AnnotationTableModel(OWLModel okb) {
    if (annotations == null) {
      annotations = new ArrayList();
    }
  }

  /**
   * @param column the column being queried
   * @return a string containing the default name of <code>column</code>
   */
  public String getColumnName(int column) {
    return AnnotationEntry.fieldNames[column];
  }

  public int getRowCount() {
    return annotations.size();
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return (columnIndex > 0);
  }

  public Class getColumnClass(int columnIndex) {
    return AnnotationEntry.fieldClasses[columnIndex];
  }

  public int getColumnCount() {
    return AnnotationEntry.fieldNames.length;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    AnnotationEntry entry = (AnnotationEntry) annotations.get(rowIndex);
    return entry.get(columnIndex);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    AnnotationEntry entry = (AnnotationEntry) annotations.get(rowIndex);
    entry.set(columnIndex, aValue);
    if (columnIndex == AnnotationEntry.FIELD_USE) {
      checkFunctional((Boolean) aValue, entry);
    }
    fireTableDataChanged();
  }

  /**
   * make sure, if annotation is functional, we disable others
   */
  private void checkFunctional(Boolean newValue, AnnotationEntry entry) {
    if (newValue == Boolean.TRUE) {
      OWLProperty annotProp = entry.annotationProp;

      if (annotProp.isFunctional()) {
        Iterator i = annotations.iterator();
        while (i.hasNext()) {

          AnnotationEntry currentEntry = (AnnotationEntry) i.next();
          if ((currentEntry.annotationProp == annotProp)
              && (currentEntry != entry)) {
            //System.out.println("Setting a duplicate to disabled");
            currentEntry.use = Boolean.FALSE;
            fireTableCellUpdated(annotations.indexOf(currentEntry),
                                 AnnotationEntry.FIELD_USE);
          }
        }
      }
    }
  }

  /**
   * @return list of AnnotationEntrys or null if no annotations in model
   */
  public AbstractList getSelectedAnnotations() {
    AbstractList result = null;
    Iterator i = annotations.iterator();
    while (i.hasNext()) {
      AnnotationEntry currentEntry = (AnnotationEntry) i.next();
      if (currentEntry.use == Boolean.TRUE) {
        if (result == null) {
          result = new ArrayList();
        }
        result.add(currentEntry);
      }
    }
    return result;
  }

  public int addNewAnnotation() {
    return addNewAnnotation(null);
  }

  public int addNewAnnotation(AbstractOWLProperty annotation) {
    AnnotationEntry newEntry = new AnnotationEntry(annotation, "", "", Boolean.TRUE);

    annotations.add(newEntry);

    int indexOfNewEntry = annotations.indexOf(newEntry);

    fireTableRowsInserted(indexOfNewEntry, indexOfNewEntry);

    checkFunctional(Boolean.TRUE, newEntry);

    return indexOfNewEntry;
  }

  public void removeAnnotation(int row) {
    if ((row >= 0) && (row < annotations.size())) {
      annotations.remove(row);
      fireTableRowsDeleted(row, row);
    }
  }
}
