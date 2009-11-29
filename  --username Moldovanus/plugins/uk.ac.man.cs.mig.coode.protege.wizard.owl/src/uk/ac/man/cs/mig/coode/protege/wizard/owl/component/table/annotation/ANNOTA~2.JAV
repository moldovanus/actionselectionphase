package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.table.annotation;

import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.AbstractList;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Aug-2004
 */
public class AnnotationTableComponent extends AbstractWizardEditor {

  private static final boolean debug = Trace.Component.annotationTable;
  private JTable theTable;
  private AnnotationCombo addAnnotationCombo;

  private AnnotationTableModel theModel;
  private JPanel mainPanel;

  public boolean isSuitable(Object value) {
    return value instanceof AbstractList;
  }

  public AnnotationTableComponent() {
    super();

    OWLModel okb = (OWLModel) WizardAPI.getWizardManager().getKnowledgeBase();

    theModel = new AnnotationTableModel(okb);

    theModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (debug) System.out.println("TABLE CHANGED>>>>");
        notifyValueChanged();
      }
    });

    mainPanel = new JPanel(new BorderLayout(6, 6));
    mainPanel.add(createToolBar(), BorderLayout.NORTH);
    mainPanel.add(createTable(theModel), BorderLayout.CENTER);
  }

  private JComponent createToolBar() {
    JPanel tools = new JPanel();

    OWLModel okb = (OWLModel) WizardAPI.getWizardManager().getKnowledgeBase();
    OwlKbHelper helper = OwlKbHelper.getHelper(okb);
    AbstractList availableAnnotations = helper.getClassAnnotationProperties();

    addAnnotationCombo = new AnnotationCombo(availableAnnotations);
    addAnnotationCombo.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          AbstractOWLProperty selectedAnnotation = (AbstractOWLProperty) e.getItem();
          int newRow = theModel.addNewAnnotation(selectedAnnotation);
          theTable.requestFocusInWindow();
          theTable.changeSelection(newRow, AnnotationEntry.FIELD_VALUE, false, false);
          theTable.editCellAt(newRow, AnnotationEntry.FIELD_VALUE);
        }
      }
    });
    addAnnotationCombo.setRenderer(ResourceRenderer.createInstance());

    JButton addAnnotation = new JButton("+");
    addAnnotation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int newRow = theModel.addNewAnnotation((AbstractOWLProperty) addAnnotationCombo.getSelectedItem());
        theTable.requestFocusInWindow();
        theTable.changeSelection(newRow, AnnotationEntry.FIELD_VALUE, false, false);
        theTable.editCellAt(newRow, AnnotationEntry.FIELD_VALUE);
      }
    });

    JButton removeAnnotation = new JButton("-");
    removeAnnotation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        theTable.editingCanceled(null);
        int[] currentRows = theTable.getSelectedRows();
        if (currentRows.length == 0) {
          int numRows = theModel.getRowCount();
          if (numRows != 0) {
            theModel.removeAnnotation(numRows - 1);
            if (numRows - 1 >= 0) {
              theTable.changeSelection(numRows - 1, AnnotationEntry.FIELD_VALUE, false, false);
            }
          }
        }
        else {
          for (int i = currentRows.length - 1; i >= 0; i--) // backwards so not to affect indexing
          {
            theModel.removeAnnotation(currentRows[i]);
          }
          if (currentRows[0] - 1 >= 0) {
            theTable.changeSelection(currentRows[0] - 1, AnnotationEntry.FIELD_VALUE, false, false);
          }
        }
      }
    });

    tools.add(new JLabel(WizardAPI.getProperty("ui.annotations")));
    tools.add(addAnnotationCombo);
    tools.add(addAnnotation);
    tools.add(removeAnnotation);
    return tools;
  }

  private JComponent createTable(TableModel model) {
    theTable = new JTable(model);

//    defRenderer = new DefaultTableCellRenderer(){
//      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//      }
//    };

    // property column
    TableColumnModel cModel = theTable.getColumnModel();
    TableColumn column = cModel.getColumn(AnnotationEntry.FIELD_ANNOT_PROP);
    column.setCellRenderer(FrameRenderer.createInstance());
    column.setPreferredWidth(70);

    column = cModel.getColumn(AnnotationEntry.FIELD_VALUE);
    column.setCellRenderer(ResourceRenderer.createInstance());
    //column.setCellEditor();
    column.setPreferredWidth(120);

    // language column
    column = cModel.getColumn(AnnotationEntry.FIELD_LANG);
    //column.setCellRenderer(defRenderer);
    column.setPreferredWidth(40);
    column.setMaxWidth(40);
    column.setMinWidth(40);

    // use column
    column = cModel.getColumn(AnnotationEntry.FIELD_USE);
    //column.setCellRenderer(new CheckBoxRenderer());
    column.setPreferredWidth(30);
    column.setMaxWidth(30);
    column.setMinWidth(20);

    return new JScrollPane(theTable);
  }

  public boolean setValue(Object newValue) {
    return false;  // @@TODO change default
  }

  public Object getValue() {
    // this is a messy way of making sure the current editing has stopped
    theTable.editCellAt(theTable.getSelectedRow(), AnnotationEntry.FIELD_VALUE);
    return theModel.getSelectedAnnotations();
  }

  public JComponent getComponent() {
    return mainPanel;
  }

  private class AnnotationCombo extends JComboBox {

    public AnnotationCombo(AbstractList annotationProperties) {
      super();
      if (annotationProperties != null) {
        for (Iterator i = annotationProperties.iterator(); i.hasNext();) {
          addItem(i.next());
        }
      }
    }
  }

}

