package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class OptionPage extends AbstractWizardPage {

  JCheckBox optionCheckbox;

  /**
   * Page containing a single checkbox option for boolean entry
   *
   * @param pageName the name of the page
   * @param propName the name of the wizard property editable by this page
   */
  public OptionPage(String pageName, String propName) {
    super(pageName, propName);
    optionCheckbox = new JCheckBox();

    // add a change listener to update the property
    optionCheckbox.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        checkboxValueChanged(optionCheckbox.isSelected());
      }
    });

    // finally add the tickbox to the wizard
    add(optionCheckbox, BorderLayout.NORTH);
  }

  protected void checkboxValueChanged(boolean selected) {
    getPropertiesSource().setProperty(getPropName(), new Boolean(selected));
  }

  public void pageSelected(WizardDialog wizard) {

    super.pageSelected(wizard);

    Boolean value = (Boolean) getPropertiesSource().getProperty(getPropName());

    optionCheckbox.setSelected(value.booleanValue());

    optionCheckbox.setText(getPromptText());

    optionCheckbox.grabFocus();
  }

  public synchronized void addKeyListener(KeyListener l) {
    optionCheckbox.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    optionCheckbox.removeKeyListener(l);
  }


}
