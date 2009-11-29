package uk.ac.man.cs.mig.coode.protege.wizard.component.options;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Drummond
 * Date: 16-Feb-2005
 * Time: 15:58:06
 * To change this template use File | Settings | File Templates.
 */
public class OptionsComponent extends AbstractWizardEditor {

  private ButtonGroup radios;
  private HashMap optionsMap = null;
  private String current;

  private JPanel mainPanel;

  public OptionsComponent(List options) {
    if (options != null) {

      this.optionsMap = new HashMap(options.size());

      int numOptions = options.size();
      JPanel optionsPanel = new JPanel(new GridLayout(numOptions, 1));
      radios = new ButtonGroup();

      ActionListener buttonListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          current = e.getActionCommand();
          notifyValueChanged();
        }
      };

      for (int i = 0; i < options.size(); i++) {
        String optionString = (String) options.get(i);
        JRadioButton radio = new JRadioButton(optionString);
        radio.setActionCommand(optionString);
        radio.addActionListener(buttonListener);
        radios.add(radio);
        optionsMap.put(optionString, radio);
        optionsPanel.add(radio);
      }

      mainPanel = new JPanel(new BorderLayout(6, 6));
      mainPanel.add(optionsPanel, BorderLayout.NORTH);
    }
  }

  public boolean setValue(Object newValue) {
    boolean result = false;
    if (optionsMap.containsKey(newValue)) {
      JRadioButton b = (JRadioButton) optionsMap.get(newValue);
      b.setSelected(true);
      current = (String) newValue;
      result = true;
    }
    return result;
  }

  public Object getValue() {
    return current;
  }

  public boolean isSuitable(Object value) {
    return (value instanceof String);
  }

  public boolean currentValueIsValid() {
    return current != null;
  }

  public JComponent getComponent() {
    return mainPanel;
  }

  public void giveFocus() {
    JRadioButton b = (JRadioButton) optionsMap.get(current);
    b.grabFocus();
  }

  public synchronized void addKeyListener(KeyListener l) {
    for (Iterator i = optionsMap.values().iterator(); i.hasNext();) {
      JRadioButton b = (JRadioButton) i.next();
      b.addKeyListener(l);
    }
  }

  public synchronized void removeKeyListener(KeyListener l) {
    for (Iterator i = optionsMap.values().iterator(); i.hasNext();) {
      JRadioButton b = (JRadioButton) i.next();
      b.removeKeyListener(l);
    }
  }
}
