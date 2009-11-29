package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Mar-2005
 */
public class IntroPage extends TextPage {

  private JCheckBox hideNextTime;

  public IntroPage(String introURLProp) {
    super("Introduction", introURLProp);

    hideNextTime = new JCheckBox("turn off the introduction page in future", false);
    add(hideNextTime, BorderLayout.SOUTH);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    wizard.setPrevButtonEnabled(false);
  }

  protected void pageLosingFocus() {
    super.pageLosingFocus();
    WizardAPI.setShowIntro(!hideNextTime.isSelected());
  }
}
