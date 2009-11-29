package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class StringEntryPageOld extends AbstractWizardPage {

  protected JTextArea textBox;

  private JTextPane promptLabel = new JTextPane();

  public StringEntryPageOld(String pageName, String propName) {
    super(pageName, propName);

    textBox = new JTextArea();
    textBox.setBorder(new LineBorder(Color.BLACK, 1));

    // finally add the components to the wizard
    JPanel panel = new JPanel(new BorderLayout(6, 6));
    panel.add(promptLabel, BorderLayout.NORTH);
    panel.add(textBox, BorderLayout.CENTER);

    add(panel, BorderLayout.NORTH);
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);

    Object value = getPropertiesSource().getProperty(getPropName());
    String text = "";
    if (value != null) {
      text = value.toString();
    }
    textBox.setText(text);

    promptLabel.setText(getPromptText());
  }

  public void nextButtonPressed(WizardDialog wizard) {
    getPropertiesSource().setProperty(getPropName(), textBox.getText());
    super.nextButtonPressed(wizard);
  }

  public void prevButtonPressed(WizardDialog wizard) {
    getPropertiesSource().setProperty(getPropName(), textBox.getText());
    super.prevButtonPressed(wizard);
  }
}
