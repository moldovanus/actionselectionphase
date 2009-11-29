package uk.ac.man.cs.mig.coode.protege.wizard.page;

import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesSource;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardPage;

import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Jul-2004
 */
public abstract class AbstractWizardPage extends WizardPage
        implements ProtegeWizardPage {

  private String propName;

  private ProtegeWizard protegeWizard;

  public AbstractWizardPage() {
    this("no page name set", null);
  }

  public AbstractWizardPage(String pageName) {
    this(pageName, null);
  }

  public AbstractWizardPage(String pageName, String propName) {
    super(pageName);

    setLayout(new BorderLayout(6, 6));

    this.propName = propName;
  }

  public String getName() {
    return super.getName();
  }

  public String toString() {
    return getName();
  }

  public WizardPage getWizardPage() {
    return this;
  }

  protected final PropertiesSource getPropertiesSource() {
    return protegeWizard;
  }

  public final void setProtegeWizard(ProtegeWizard wizard) {
    this.protegeWizard = wizard;
  }

  protected final ProtegeWizard getProtegeWizard() {
    return this.protegeWizard;
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);
    getWizard().setNextButtonEnabled(true);
//    getWizard().setPrevButtonEnabled(true);
  }

  protected void pageLosingFocus() {
  }

  public void nextButtonPressed(WizardDialog wizard) {
    pageLosingFocus();
    super.nextButtonPressed(wizard);
  }

  public void prevButtonPressed(WizardDialog wizard) {
    pageLosingFocus();
    super.prevButtonPressed(wizard);
  }

  public final void wizardSet(WizardDialog wizard) {
    super.wizardSet(wizard);
  }

  public String getPropName() {
    return propName;
  }

  public Object getPropValue() {
    Object value = null;
    if (propName != null) {
      value = getPropValue(propName);
    }
    return value;
  }

  public Object getPropValue(String propName) {
    Object result = null;
    try {
      result = getPropertiesSource().getProperty(propName);
    }
    catch (NullPointerException npe) {
      throw new RuntimeException("have not added the page yet, cannot get the property value");
    }
    return result;
  }

  /**
   * The prompt text is now defined by the owning wizard
   * In most cases this is because the wizard gets these values from a resource file
   *
   * @return a string for the prompt text - may be html
   */
  protected String getPromptText() {
    return getProtegeWizard().getPagePrompt(getName());
  }
}
