package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry.test;

import uk.ac.man.cs.mig.coode.protege.wizard.component.listentry.HighlightLinesEditor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jun-2004
 */
public class TestHighlightLinesEditor extends JFrame {

  public HighlightLinesEditor editor;

  public TestHighlightLinesEditor()
          throws HeadlessException {
    super();

    editor = new HighlightLinesEditor();
    editor.setPreferredSize(new Dimension(400, 400));

    JButton testButton = new JButton("Try to highlight first, second & fifth lines");
    testButton.addActionListener(new ActionListener() {

      private boolean highlightOn = false;

      public void actionPerformed(ActionEvent e) {
        try {
          if (highlightOn) {
            editor.clearLine(0);
            editor.clearLine(1);
            editor.clearLine(4);
          }
          else {
            editor.highlightLine(0, Color.RED);
            editor.highlightLine(1, Color.GREEN);
            editor.highlightLine(4, Color.BLUE);
          }
        }
        catch (BadLocationException e1) {
          System.err.println(e1.getMessage());
        }
        highlightOn = !highlightOn;
      }
    });

    setBounds(200, 50, 400, 400);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout(6, 6));
    contentPane.add(editor, BorderLayout.CENTER);
    contentPane.add(testButton, BorderLayout.SOUTH);

    pack();
  }

  public static void main(String[] args) {
    TestHighlightLinesEditor t = new TestHighlightLinesEditor();
    t.setVisible(true);

    t.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
  }
}
