package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Feb-2005
 */
public class IntegerPage extends AbstractWizardPage {

  private JSpinner spinner;

  private JTextPane promptLabel = new JTextPane();

  public IntegerPage(String pageName, String propName) {
    super(pageName, propName);


    spinner = new JSpinner();


    promptLabel.setBackground(this.getBackground());
    add(promptLabel, BorderLayout.NORTH);

    // add a change listener to update the property
    spinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        getPropertiesSource().setProperty(getPropName(), spinner.getValue());
      }
    });


    JPanel panel = new JPanel(new BorderLayout(6, 6));
    panel.add(spinner, BorderLayout.EAST);
    panel.add(new JLabel("Current value: "), BorderLayout.WEST);

    add(panel, BorderLayout.NORTH);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    spinner.setValue(getPropertiesSource().getProperty(getPropName()));

    promptLabel.setText(getPromptText());
  }
}