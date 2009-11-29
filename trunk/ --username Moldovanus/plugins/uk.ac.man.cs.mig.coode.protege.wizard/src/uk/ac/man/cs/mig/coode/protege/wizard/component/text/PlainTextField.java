package uk.ac.man.cs.mig.coode.protege.wizard.component.text;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2005
 */
public class PlainTextField extends AbstractWizardEditor {

  protected JTextArea textBox;

  private JPanel mainPanel;

  public PlainTextField(int numRows) {
    super();
    textBox = new JTextArea();
    textBox.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
      }

      public void insertUpdate(DocumentEvent e) {
        notifyValueChanged();
      }

      public void removeUpdate(DocumentEvent e) {
        notifyValueChanged();
      }
    });
    textBox.setRows(numRows);
    mainPanel = new JPanel(new BorderLayout(6, 6));
    mainPanel.add(new JScrollPane(textBox), BorderLayout.NORTH);
  }

  public boolean isSuitable(Object value) {
    return value instanceof String;
  }

  public boolean setValue(Object newValue) {
    textBox.setText((String) newValue);
    return true;
  }

  public Object getValue() {
    return textBox.getText();
  }

  public JComponent getComponent() {
    return mainPanel;
  }

  public void giveFocus() {
    textBox.grabFocus();
  }

  public synchronized void addKeyListener(KeyListener l) {
    textBox.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    textBox.removeKeyListener(l);
  }
}
