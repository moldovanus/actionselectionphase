package uk.ac.man.cs.mig.coode.protege.wizard.component.file;

import edu.stanford.smi.protege.util.FileField;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;

import javax.swing.*;
import java.io.File;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Feb-2005
 */
public class FileSelectorComponent extends AbstractWizardEditor {

  FileField theFileField;

  public FileSelectorComponent(String extension) {
    theFileField = new FileField(null, null, extension);
  }

  public boolean isSuitable(Object value) {
    return value instanceof File;
  }

  public boolean setValue(Object newValue) {
    return false;
  }

  public Object getValue() {
    return null;
  }

  public JComponent getComponent() {
    return theFileField;
  }
}
