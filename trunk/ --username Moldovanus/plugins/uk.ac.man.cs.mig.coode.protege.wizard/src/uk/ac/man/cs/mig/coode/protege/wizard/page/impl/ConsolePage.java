package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.console.TextAreaConsole;
import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Mar-2005
 */
public class ConsolePage extends AbstractWizardPage {

  private TextAreaConsole output = null;
  private JCheckBox hideNextTime;

  public ConsolePage() {
    super("Results");

    output = new TextAreaConsole();
    add(output, BorderLayout.CENTER);

    hideNextTime = new JCheckBox("do not show this page again", false);
    add(hideNextTime, BorderLayout.SOUTH);
  }

  public TextAreaConsole getConsole() {
    return output;
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    output.grabFocus();
  }

  protected void pageLosingFocus() {
    super.pageLosingFocus();
    WizardAPI.setShowResults(!hideNextTime.isSelected());
  }

  public synchronized void addKeyListener(KeyListener l) {
    output.addKeyListener(l);
  }

  public synchronized void removeKeyListener(KeyListener l) {
    output.removeKeyListener(l);
  }
}
