package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry.test;

import uk.ac.man.cs.mig.coode.protege.wizard.component.listentry.UniqueEntryListEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.AnyStringValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         29-Jun-2004
 */
public class TestUniqueEntryListEditor extends JFrame {

  public TestUniqueEntryListEditor()
          throws HeadlessException {
    super();

    UniqueEntryListEditor editor = new UniqueEntryListEditor(new AnyStringValidator(), null);

    editor.setPreferredSize(new Dimension(400, 400));

    setBounds(200, 50, 400, 400);
    getContentPane().add(editor);

    pack();
  }

  public static void main(String[] args) {
    TestUniqueEntryListEditor t = new TestUniqueEntryListEditor();
    t.setVisible(true);

    t.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
  }
}
