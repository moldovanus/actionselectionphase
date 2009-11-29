package uk.ac.man.cs.mig.coode.protege.wizard.component.test;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextField;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public class TestValidatedStringEntryComponent extends JFrame {

  public TestValidatedStringEntryComponent(AbstractWizardEditor c) throws HeadlessException {
    super();

    setBounds(100, 100, 400, 300);

    getContentPane().add(c.getComponent());

    c.setValue(createTestValue());

    setVisible(true);
  }

  protected Object createTestValue() {
    return "hello";
  }

  public static void main(String[] args) {
    StringValidator v = new StringValidator() {
      public boolean isValid(String s, Collection currentEntries, String prop, Collection errors) {
        if (s.startsWith("Test")) {
          errors.add("Cannot say Test at start of string");
          return false;
        }
        else {
          return true;
        }
      }

      public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
        if (s.startsWith("Test")) {
          s = s.replaceFirst("Test", "");
        }
        return s;
      }
    };

    ValidatedTextField vc = new ValidatedTextField(v);

    new TestValidatedStringEntryComponent(vc);
  }
}
