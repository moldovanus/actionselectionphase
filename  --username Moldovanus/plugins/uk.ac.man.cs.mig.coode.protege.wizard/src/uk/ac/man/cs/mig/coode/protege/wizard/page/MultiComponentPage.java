package uk.ac.man.cs.mig.coode.protege.wizard.page;

import uk.ac.man.cs.mig.coode.protege.wizard.component.WizardPropertyEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public abstract class MultiComponentPage extends AbstractWizardPage {

  private static final boolean debug = false;

  private JTextPane promptLabel;

  private JPanel componentPanel;

  protected HashMap components;
//  private AbstractWizardEditor theComponent = null;

  private boolean valueHasChanged = false;

  private final ValueChangeListener vcListener = new ValueChangeListener() {
    public void valueChanged() {
      valueHasChanged = true;
      updateButtons();
    }

    public int compareTo(Object o) {
      if (this.equals(o))
        return 0;
      else
        return 1;
    }
  };

  /**
   * Can only be called from subclasses
   * if this call is used, the constructor must finish with a call to setComponent()
   *
   * @param pageName
   */
  protected MultiComponentPage(String pageName) {
    super(pageName);
    components = new HashMap();

    // create a pane for the prompt
    promptLabel = new JTextPane();
    promptLabel.setBackground(this.getBackground());
    add(promptLabel, BorderLayout.NORTH);

    // create a pane to add the components to
    componentPanel = new JPanel(new GridLayout(0, 1));
    add(componentPanel, BorderLayout.CENTER);
  }

  protected abstract void giveFocus();

  public void addComponent(WizardPropertyEditor component, String prop) {
    if (!components.containsKey(prop)) {
      components.put(prop, component);
      component.setPropertyName(getPropName());
      componentPanel.add(component.getComponent());
      component.addValueChangeListener(vcListener);
    }
    else {
      System.err.println("WARNING: ALREADY ADDED A COMPONENT FOR THAT PROPERTY: " + prop);
    }
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);

    for (Iterator i = components.keySet().iterator(); i.hasNext();) {
      String prop = (String) i.next();
      Object value = getPropertiesSource().getProperty(prop);
      WizardPropertyEditor editor = (WizardPropertyEditor) components.get(prop);
      editor.setValue(value);
    }

    // track when edits are made by the user so we can disable the next button
    // when required
    valueHasChanged = false;

    // make sure the prompt text is set
    promptLabel.setText(getPromptText());

    // the value might already be invalid, so update the buttons
    updateButtons();

    giveFocus();
  }

  protected void pageLosingFocus() {
    if (debug) System.out.println("ArbitraryComponentPage.pageLosingFocus");

    if (valueHasChanged) {
      for (Iterator i = components.keySet().iterator(); i.hasNext();) {
        String prop = (String) i.next();
        WizardPropertyEditor editor = (WizardPropertyEditor) components.get(prop);
        Object value = editor.getValue();
        getPropertiesSource().setProperty(prop, value);
      }
    }
    else {
      if (debug) System.out.println("Value not changed");
    }
  }

  private final void updateButtons() {

    boolean valid = true;
    for (Iterator i = components.values().iterator(); (i.hasNext() && valid == true);) {
      WizardPropertyEditor editor = (WizardPropertyEditor) i.next();
      valid = editor.currentValueIsValid();
    }

    if (valid) {
      if (debug) System.out.println("VALID - SETTING NEXT TO ENABLED");
      getWizard().setNextButtonEnabled(true);
    }
    else {
      if (debug) System.out.println("INVALID - SETTING NEXT TO DISABLED");
      getWizard().setNextButtonEnabled(false);
    }
  }
}
