package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.DefaultTableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.TableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.DefaultTableCellRendererFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.TableCellRendererFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class MatrixComponent extends AbstractWizardEditor {

  private AbstractMatrixModel theModel;

  private JPanel theComponent;
  private RenEditTable theTable;
  private JLabel errorLabel;

  private TableCellRendererFactory rendererFactory = null;
  private TableCellEditorFactory editorFactory = null;

  public MatrixComponent() {
    this(null, new DefaultTableCellRendererFactory(), new DefaultTableCellEditorFactory());
  }

  public MatrixComponent(TableCellRendererFactory rf, TableCellEditorFactory ef) {
    this(null, rf, ef);
  }

  public MatrixComponent(AbstractMatrixModel model,
                         TableCellRendererFactory rf,
                         TableCellEditorFactory ef) {
    super();

    this.rendererFactory = rf;
    this.editorFactory = ef;

    theComponent = new JPanel();
    if (model != null) {
      setModel(model);
      theTable = new RenEditTable(theModel, editorFactory, rendererFactory);
      addTable();
    }
    else {
      addErrorLabel();
    }
  }

  private void setModel(AbstractMatrixModel model) {
    theModel = model;
    theModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        notifyValueChanged();
      }
    });
  }

  public boolean setValue(Object newValue) {

    boolean result = false;

    if (newValue != null) {
      setModel((AbstractMatrixModel) newValue);
      theModel.load();

      if (theTable == null) {
        theTable = new RenEditTable(theModel, editorFactory, rendererFactory);
        addTable();
      }
      else {
        theTable.setModel(theModel);
      }

      result = true;
    }

    return result;
  }

  public Object getValue() {
    return theModel;
  }

  public JComponent getComponent() {
    return theComponent;
  }

  public boolean isSuitable(Object value) {
    return value instanceof AbstractMatrixModel;
  }

  private void addTable() {
    theComponent.removeAll();
    theComponent.add(new JScrollPane(theTable));
  }

  private void addErrorLabel() {
    errorLabel = new JLabel("There are currently no axis set for the matrix");
    errorLabel.setHorizontalTextPosition(JLabel.CENTER);
    theComponent.removeAll();
    theComponent.add(errorLabel);
  }

//  public synchronized void addKeyListener(KeyListener l) {
//    theTable.addKeyListener(l);
//  }
//
//  public synchronized void removeKeyListener(KeyListener l) {
//    theTable.removeKeyListener(l);
//  }
}
