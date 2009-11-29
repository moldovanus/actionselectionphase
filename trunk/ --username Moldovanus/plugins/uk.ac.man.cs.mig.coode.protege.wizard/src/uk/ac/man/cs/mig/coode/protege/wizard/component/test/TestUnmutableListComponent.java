package uk.ac.man.cs.mig.coode.protege.wizard.component.test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class TestUnmutableListComponent extends JFrame {

  private UnmutableListComponent lc;

  public TestUnmutableListComponent() throws HeadlessException {
    super();
    setBounds(100, 100, 400, 300);

    lc = new UnmutableListComponent();

    getContentPane().add(lc.getComponent());

    lc.setValue(createTestCollection());
  }

  public static Collection createTestCollection() {
    Collection someList = new ArrayList();
    someList.add("fish");
    someList.add("for");
    someList.add("my");
    someList.add("veggie");
    someList.add("tea");
    return someList;
  }

  public static void main(String[] args) {
    TestUnmutableListComponent frame = new TestUnmutableListComponent();
    frame.setVisible(true);
  }
}
