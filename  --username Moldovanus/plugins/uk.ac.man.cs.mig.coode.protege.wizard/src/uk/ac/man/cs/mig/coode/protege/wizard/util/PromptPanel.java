package uk.ac.man.cs.mig.coode.protege.wizard.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         21-Oct-2005
 */
public class PromptPanel extends JPanel {

  private JTextPane label;
  private JCheckBox checkbox;

  public PromptPanel(String text) {
    super(new BorderLayout(6, 6));
    label = new JTextPane();
    label.setBackground(this.getBackground());
    label.setEditable(false);
    label.setText(text);
    label.setPreferredSize(new Dimension(350, 100));
    checkbox = new JCheckBox("always perform this action");
    add(label, BorderLayout.NORTH);
    add(checkbox, BorderLayout.SOUTH);
  }

  public boolean getShowAgain() {
    return !checkbox.isSelected();
  }
}
