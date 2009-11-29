package uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Oct-2004
 */
public abstract class AbstractDialogCellEditor
        extends AbstractCellEditor implements TableCellEditor, ActionListener {

  private static final int borderSize = 6;
  private static final int defaultWidth = 500;
  private static final int defaultHeight = 400;

  private JDialog theDialog;
  private AbstractWizardEditor theEditor;

  private JButton button;
  protected static final String EDIT = "edit";
  private Frame parentFrame;

  private JButton okButton;
  private String title;

  public AbstractDialogCellEditor(Frame parentFrame, String title) {
    button = new JButton(EDIT);
    button.setActionCommand(EDIT);
    button.addActionListener(this);
    button.setBorderPainted(false);

    this.parentFrame = parentFrame;
    this.title = title;
  }

  public Object getCellEditorValue() {
    return theEditor.getValue();
  }

  public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected,
                                               int row, int column) {

    if (theEditor == null) {
      theEditor = createComponent();
      theEditor.setValue(value);
      theDialog = createDialog();
    }

    return button;
  }

  public void actionPerformed(ActionEvent e) {
    if (EDIT.equals(e.getActionCommand())) {
      //The user has clicked the cell, so bring up theDialog.
      theDialog.setVisible(true);
      //okButton.requestFocus();
    }
  }

  protected abstract AbstractWizardEditor createComponent();

  private JDialog createDialog() {
    JDialog dialog = new JDialog(parentFrame, title, true);

    Container cp = dialog.getContentPane();

    cp.setLayout(new BorderLayout(borderSize, borderSize));

    theEditor.getComponent().setBorder(new EmptyBorder(borderSize, borderSize, borderSize, borderSize));
    cp.add(theEditor.getComponent(), BorderLayout.CENTER);

    dialog.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        fireEditingCanceled();
      }
    });

    okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        finish();
      }
    });

    JButton cancelButton = new JButton("cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancel();
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // add the buttons to the bottom of the dialog
    cp.add(buttonPanel, BorderLayout.SOUTH);

    // set default size
    dialog.setSize(defaultWidth, defaultHeight);

    // centre on screen
    dialog.setLocationRelativeTo(null);

    theEditor.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        //System.err.println("XXXXXXXXXXXXXXXXXXX key rel");
        switch (e.getKeyCode()) {
          case KeyEvent.VK_ENTER:
            finish();
            break;
          case KeyEvent.VK_ESCAPE:
            cancel();
            break;
        }
      }

      public void keyPressed(KeyEvent e) {
        //System.err.println("XXXXXXXXXXXXXXXXXXXXXX  key pressed");
      }

      public void keyTyped(KeyEvent e) {
        //System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXX  key typed");
      }
    });

    theEditor.giveFocus();

    return dialog;
  }

  private void cancel() {
    fireEditingCanceled();
    theDialog.setVisible(false);
  }

  private void finish() {
    fireEditingStopped();
    theDialog.setVisible(false);
  }

}
