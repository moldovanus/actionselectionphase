package uk.ac.man.cs.mig.coode.protege.wizard.component.console;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         07-Dec-2004
 */
public class TextAreaConsole extends JComponent implements Console {

  private JTextArea ta;

  public TextAreaConsole() {
    super();
    setLayout(new BorderLayout());

    ta = new JTextArea();
    ta.setEditable(false);
//    ta.getDocument().addDocumentListener(new DocumentListener() {
//
//      public void changedUpdate(DocumentEvent e) {
//        //System.out.println("changed");
//      }
//
//      public void insertUpdate(DocumentEvent e) {
//        ta.repaint();
//      }
//
//      public void removeUpdate(DocumentEvent e) {
//        //System.out.println("removed");
//      }
//    });
    add(new JScrollPane(ta), BorderLayout.CENTER);
  }

  public JTextArea getTextPane() {
    return ta;
  }

  public void out(String str) {
    //System.out.println("str = " + str);
    ta.append("\n" + str);
    //repaint();
  }

  public void err(String str) {
    ta.append("\nERROR -> " + str);
    //repaint();
  }

  public void warn(String str) {
    ta.append("\nWARNING -> " + str);
    //repaint();
  }

  public void clear() {
    ta.setText("");
    //repaint();
  }
}
