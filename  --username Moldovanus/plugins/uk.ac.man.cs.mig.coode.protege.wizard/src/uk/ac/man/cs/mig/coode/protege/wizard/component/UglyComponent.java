package uk.ac.man.cs.mig.coode.protege.wizard.component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: Nick Drummond
 * Date: 16-Feb-2005
 * Time: 16:22:50
 */
public class UglyComponent extends AbstractWizardEditor {

  private JLabel component;

  public UglyComponent() {
    component = new JLabel("ERROR");
    component.setBackground(Color.RED);
    component.setBorder(new LineBorder(Color.BLACK, 4));
  }

  public boolean isSuitable(Object value) {
    return false;
  }

  public boolean setValue(Object newValue) {
    return false;
  }

  public Object getValue() {
    return null;
  }

  public JComponent getComponent() {
    return component;
  }
}
